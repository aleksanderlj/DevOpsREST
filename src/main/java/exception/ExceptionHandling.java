package exception;

import io.javalin.http.ExceptionHandler;
import io.javalin.http.Handler;
import io.jsonwebtoken.ExpiredJwtException;

public class ExceptionHandling {
    public static ExceptionHandler<NotAuthorizedException> notAuthorized = (e, ctx) -> {
        ctx.status(401);
        ctx.result("401: Not authorized");
    };

    public static ExceptionHandler<InvalidPayloadException> invalidPayload = (e, ctx) -> {
        ctx.status(400);
        ctx.result("400: Bad request");
    };

    public static ExceptionHandler<ResourceConflictException> resourceConflict = (e, ctx) -> {
        ctx.status(422);
        ctx.result("422: Conflicting resource");
    };

    public static ExceptionHandler<ExpiredJwtException> expiredJwt = (e, ctx) -> {
        ctx.status(401);
        ctx.result("401: Expired access token");
    };
}
