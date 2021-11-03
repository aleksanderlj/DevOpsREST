package controller;

import io.javalin.http.Handler;
import jwt.JWTHandler;

public class JWTController {
    public static Handler decode = ctx ->{
        ctx.json(JWTHandler.validateAndDecode(ctx.body()));
    };
}
