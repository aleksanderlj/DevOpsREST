package controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import dao.UserDAO;
import exception.NotAuthorizedException;
import io.javalin.http.Handler;
import token.JWTHandler;
import token.PwdAuth;
import model.LoginData;
import model.User;
import util.PropFile;

import java.util.Collections;
import java.util.List;

public class UserController {
    public static Handler fetchByQuery = ctx -> {
        UserDAO dao = UserDAO.instance();

        User filter = new User();
        Long id = ctx.queryParam("id") != null ? new Long(ctx.queryParam("id")) : null;
        filter.setId(id);
        filter.setGoogleId(ctx.queryParam("googleId"));
        filter.setUsername(ctx.queryParam("name"));

        List<User> user = dao.getUsersByFilter(filter);
        ctx.json(user);
    };

    public static Handler fetchById = ctx -> {
        UserDAO dao = UserDAO.instance();
        User user = dao.getUserById(ctx.pathParam("id"));
        if(user != null)
            ctx.json(user);
    };

    public static Handler insertUser = ctx -> {
        UserDAO dao = UserDAO.instance();
        User user = ctx.bodyAsClass(User.class);
        user.setPassword(PwdAuth.hash(user.getPassword()));
        Long id = dao.insertUser(user);
        ctx.json(id);
    };

    public static Handler deleteUser = ctx -> {
        UserDAO dao = UserDAO.instance();
        long count = dao.deleteUser(ctx.pathParam("id"));
        ctx.json(count);
    };

    public static Handler updateUser = ctx -> {
        UserDAO dao = UserDAO.instance();
        User user = ctx.bodyAsClass(User.class);
        dao.updateUser(user);
    };

    public static Handler login = ctx -> {
        LoginData data = ctx.bodyAsClass(LoginData.class);
        if(data.getUsername() == null || data.getPassword() == null){
            throw new NotAuthorizedException("Empty username or password");
        }

        UserDAO dao = UserDAO.instance();
        User user = dao.getUserByUsername(data.getUsername());
        if(user != null && PwdAuth.verify(data.getPassword(), user.getPassword())){
            ctx.json(JWTHandler.generateJwtToken(user));
        } else {
            throw new NotAuthorizedException("Wrong username or password");
        }
    };

    public static Handler googleLogin = ctx -> {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(PropFile.getProperty("google-client-id")))
                .build();

        GoogleIdToken idToken = verifier.verify(ctx.body());
        if(idToken != null){
            UserDAO dao = UserDAO.instance();
            GoogleIdToken.Payload payload = idToken.getPayload();
            String googleId = payload.getSubject();
            User user = dao.getUserByGoogleId(googleId);
            if(user != null){
                ctx.json(JWTHandler.generateJwtToken(user));
            } else {
                user = new User();
                user.setGoogleId(googleId);
                user.setDisplayName((String)payload.get("name"));
                Long id = dao.insertUser(user);
                ctx.json(JWTHandler.generateJwtToken(user));
            }
        } else {
            throw new NotAuthorizedException("Invalid Google ID token");
        }
    };
}
