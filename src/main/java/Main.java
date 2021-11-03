import controller.JWTController;
import controller.UserController;
import io.javalin.Javalin;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(5000);

        app.get("/test", ctx -> ctx.html("Hahawee"));
        app.get("/user", UserController.fetchByQuery);
        app.get("/user/{id}", UserController.fetchById);
        app.post("/user/insert", UserController.insertUser);
        app.post("/user/login", UserController.login);
        app.post("/token/validate", JWTController.decode);
    }
}
