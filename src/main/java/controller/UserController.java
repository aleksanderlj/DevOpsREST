package controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import dao.UserDAO;
import exception.NotAuthorizedException;
import exception.ResourceConflictException;
import io.javalin.http.Handler;
import io.prometheus.client.Counter;
import token.JWTHandler;
import token.PwdAuth;
import model.LoginData;
import model.User;
import util.PropFile;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class UserController {
    public static Counter attempts = Counter.build().name("login_attempts").help("Total login attempts").register();
    public static Counter fails = Counter.build().name("login_failures").help("Total login failures").register();

    public static Handler fetchByQuery = ctx -> {
        UserDAO dao = UserDAO.instance();

        User filter = new User();
        Long id = ctx.queryParam("id") != null ? new Long(ctx.queryParam("id")) : null;
        filter.setId(id);
        filter.setGoogleId(ctx.queryParam("googleId"));
        filter.setUsername(ctx.queryParam("name"));

        List<User> user = dao.getUsersByFilter(filter);
        ctx.json(user);
        ctx.status(200);
    };

    public static Handler fetchById = ctx -> {
        UserDAO dao = UserDAO.instance();
        User user = dao.getUserById(ctx.pathParam("id"));
        if(user != null)
            ctx.json(user);
        ctx.status(200);
    };

    public static Handler insertUser = ctx -> {
        UserDAO dao = UserDAO.instance();
        User user = ctx.bodyAsClass(User.class);
        user.setDisplayName(user.getUsername());
        user.setPassword(PwdAuth.hash(user.getPassword()));

        // Check if user or displayname already exists
        User existingUser = dao.getUserByUsername(user.getUsername());
        if(existingUser != null) {
            throw new ResourceConflictException("A user already exists with that username");
        }
        do {
            existingUser = dao.getUserByDisplayName(user.getDisplayName());
            if(existingUser != null){
                byte[] array = new byte[7];
                new Random().nextBytes(array);
                String generatedString = new String(array, StandardCharsets.UTF_8);
                user.setDisplayName(user.getDisplayName() + generatedString);
            }
        } while(existingUser != null);

        Long id = dao.insertUser(user);
        user.setId(id);
        ctx.json(JWTHandler.generateJwtToken(user));
        ctx.status(200);
    };

    public static Handler deleteUser = ctx -> {
        UserDAO dao = UserDAO.instance();
        long count = dao.deleteUser(ctx.pathParam("id"));
        ctx.json(count);
        ctx.status(200);
    };

    public static Handler updateUser = ctx -> {
        UserDAO dao = UserDAO.instance();
        User user = ctx.bodyAsClass(User.class);
        if(user.getPassword() != null){
            user.setPassword(PwdAuth.hash(user.getPassword()));
        }
        dao.updateUser(user);
        ctx.status(200);
    };

    public static Handler login = ctx -> {
        try {
            attempts.inc();
            LoginData data = ctx.bodyAsClass(LoginData.class);
            if (data.getUsername() == null || data.getPassword() == null) {
                throw new NotAuthorizedException("Empty username or password");
            }

            UserDAO dao = UserDAO.instance();
            User user = dao.getUserByUsername(data.getUsername());
            if (user != null && PwdAuth.verify(data.getPassword(), user.getPassword())) {
                ctx.json(JWTHandler.generateJwtToken(user));
            } else {
                throw new NotAuthorizedException("Wrong username or password");
            }
        } catch(Exception e){
            fails.inc();
            throw e;
        }
        ctx.status(200);
    };

    public static Handler googleLogin = ctx -> {
        try {
            attempts.inc();
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(PropFile.getProperty("google-client-id")))
                    .build();

            GoogleIdToken idToken = verifier.verify(ctx.body());
            if (idToken != null) {
                UserDAO dao = UserDAO.instance();
                GoogleIdToken.Payload payload = idToken.getPayload();
                String googleId = payload.getSubject();
                User user = dao.getUserByGoogleId(googleId);
                if (user != null) {
                    ctx.json(JWTHandler.generateJwtToken(user));
                } else {
                    user = new User();
                    user.setGoogleId(googleId);
                    user.setDisplayName((String) payload.get("name"));
                    Long id = dao.insertUser(user);
                    ctx.json(JWTHandler.generateJwtToken(user));
                }
            } else {
                throw new NotAuthorizedException("Invalid Google ID token");
            }
        } catch (Exception e){
            fails.inc();
            throw e;
        }
        ctx.status(200);
    };
}
