import controller.JWTController;
import controller.UserController;
import io.javalin.Javalin;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(5000);

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
