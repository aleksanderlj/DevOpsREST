package controller;

import dao.UserDAO;
import io.javalin.http.Handler;
import model.User;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Objects;

public class UserController {
    public static Handler fetchByQuery = ctx -> {
        UserDAO dao = UserDAO.instance();

        User filter = new User();
        ObjectId id = ctx.queryParam("id") != null ? new ObjectId(ctx.queryParam("id")) : null;
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
        dao.insertUser(user);
    };
}
