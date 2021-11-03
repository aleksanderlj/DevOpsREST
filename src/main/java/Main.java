import controller.UserController;
import io.javalin.Javalin;
import service.TestController;

import java.io.File;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(5000);

        app.get("/test", ctx -> ctx.html("Hahawee"));
        app.get("/user", UserController.fetchByQuery);
        app.get("/user/{id}", UserController.fetchById);
        app.post("/user/insert", UserController.insertUser);
    }
}
