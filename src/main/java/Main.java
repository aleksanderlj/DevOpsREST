import controller.JWTController;
import controller.UserController;
import io.javalin.Javalin;
import io.javalin.core.util.Header;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(5000);

        app.before(ctx -> {
            ctx.header(Header.ACCESS_CONTROL_ALLOW_ORIGIN, "*"); // TODO
        });

        app.get("/user", UserController.fetchByQuery);
        app.get("/user/{id}", UserController.fetchById);
        app.post("/user", UserController.insertUser);
        app.delete("/user/{id}", UserController.deleteUser);
        app.patch("/user", UserController.updateUser);
        app.post("/user/login", UserController.login);
        app.post("/user/login/google", UserController.googleLogin);
        app.post("/token/validate", JWTController.decode);
    }
}
