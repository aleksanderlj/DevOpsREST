package controller;

import dao.UserDAO;
import io.javalin.http.Handler;
import model.User;

import java.util.List;

public class UserController {
    public static Handler fetchByQuery = ctx -> {
        UserDAO dao = UserDAO.instance();

        User filter = new User();
        Long id = ctx.queryParam("id") != null ? new Long(ctx.queryParam("id")) : null;
        filter.setId(id);
        filter.setGoogleId(ctx.queryParam("googleId"));
        filter.setName(ctx.queryParam("name"));

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
        Long id = dao.insertUser(user);
        ctx.json(id);
    };
}
