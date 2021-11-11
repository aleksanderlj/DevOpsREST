package controller;

import dao.LikeDAO;
import io.javalin.http.Handler;
import token.JWTHandler;

public class LikeController {
    public static Handler likePost = ctx -> {
        Long postId = new Long(ctx.pathParam("id"));
        String token = ctx.header("Authorization");
        Long userId = new Long(JWTHandler.validateAndDecode(token).getBody().get("id").toString());
        LikeDAO dao = LikeDAO.instance();
        boolean success = dao.likePost(postId, userId);
        ctx.json(success);
    };

    public static Handler unlikePost = ctx -> {
        Long postId = new Long(ctx.pathParam("id"));
        String token = ctx.header("Authorization");
        Long userId = new Long(JWTHandler.validateAndDecode(token).getBody().get("id").toString());
        LikeDAO dao = LikeDAO.instance();
        boolean success = dao.unlikePost(postId, userId);
        ctx.json(success);
    };

    public static Handler getPostLikeCount = ctx -> {
        Long postId = new Long(ctx.pathParam("id"));
        LikeDAO dao = LikeDAO.instance();
        ctx.json(dao.getPostLikeCount(postId));
    };

    public static Handler getLikeStatus = ctx -> {
        Long postId = new Long(ctx.pathParam("id"));
        String token = ctx.header("Authorization");
        Long userId = new Long(JWTHandler.validateAndDecode(token).getBody().get("id").toString());
        LikeDAO dao = LikeDAO.instance();
        boolean status = dao.likeStatus(postId, userId);
        ctx.json(status);
    };
}
