import controller.JWTController;
import controller.LikeController;
import controller.PostController;
import controller.UserController;
import exception.ExceptionHandling;
import exception.InvalidPayloadException;
import exception.NotAuthorizedException;
import exception.ResourceConflictException;
import io.javalin.Javalin;
import io.javalin.core.util.Header;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.prometheus.client.exporter.HTTPServer;
import io.sentry.Sentry;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        // Sentry
        Sentry.init(options -> {
            options.setDsn("https://d6bf751465cc41eeaeb72dfc391ef6c9@o472376.ingest.sentry.io/6066145");
            // Set tracesSampleRate to 1.0 to capture 100% of transactions for performance monitoring.
            // We recommend adjusting this value in production.
            options.setTracesSampleRate(1.0);
            // When first trying Sentry it's good to see what the SDK is doing:
            options.setDebug(true);
        });

        // Prometheus
        HTTPServer prometheusServer = new HTTPServer(5010);

        // Javalin
        initializeJavalin(5000);
    }

    public static void initializeJavalin(int port){
        Javalin app = Javalin.create();
        app._conf.enableCorsForAllOrigins();
        app.start(port);

        app.before(ctx -> {
            ctx.header(Header.ACCESS_CONTROL_ALLOW_ORIGIN, "*"); // TODO
            ctx.header(Header.ACCESS_CONTROL_ALLOW_HEADERS, "*"); // TODO
            ctx.status(200); // Will be overwritten by exceptions
        });

        app.get("/user", UserController.fetchByQuery);
        app.get("/user/{id}", UserController.fetchById);
        app.post("/user", UserController.insertUser);
        app.delete("/user/{id}", UserController.deleteUser);
        app.patch("/user", UserController.updateUser);
        app.post("/user/login", UserController.login);
        app.post("/user/login/google", UserController.googleLogin);
        app.post("/token/validate", JWTController.decode);

        app.get("/post", PostController.fetchAll );
        app.get("/post/{id}", PostController.fetchById );
        app.get("/user/{userId}/posts", PostController.fetchByUserId );
        app.get("/forum/{forum}/posts", PostController.fetchBySubforum );
        app.post("/post", PostController.insertPost );
        app.delete("/post/{id}", PostController.deletePost);
        app.patch("/post", PostController.updatePost);

        app.post("/post/{id}/like", LikeController.likePost);
        app.post("/post/{id}/unlike", LikeController.unlikePost);
        app.get("/post/{id}/likestatus", LikeController.getLikeStatus);
        app.get("/post/{id}/like", LikeController.getPostLikeCount);

        app.exception(MalformedJwtException.class, ExceptionHandling.notAuthorized);
        app.exception(SignatureException.class, ExceptionHandling.notAuthorized);
        app.exception(NotAuthorizedException.class, ExceptionHandling.notAuthorized);
        app.exception(InvalidPayloadException.class, ExceptionHandling.invalidPayload);
        app.exception(ResourceConflictException.class, ExceptionHandling.resourceConflict);
        app.exception(ExpiredJwtException.class, ExceptionHandling.expiredJwt);
        app.exception(Exception.class, ExceptionHandling.generic);
    }
}
