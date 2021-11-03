package controller;

import dao.UserDAO;
import exception.NotAuthorizedException;
import io.javalin.http.Handler;
import jwt.JWTHandler;
import jwt.PwdAuth;
import model.LoginData;
import model.User;

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
}
