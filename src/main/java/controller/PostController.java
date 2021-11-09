package controller;
import dao.PostDAO;
import io.javalin.http.Handler;
import model.Post;

import java.util.List;

public class PostController {
    public static Handler fetchById = ctx -> {
        PostDAO dao = PostDAO.instance();

        Post post = dao.getPostById(ctx.pathParam("id"));
        if (post != null) {
            ctx.json(post);
        }
    };

    public static Handler fetchAll = ctx -> {
        PostDAO dao = PostDAO.instance();
        List<Post> posts = dao.getPosts();
        ctx.json(posts);
    };

    public static Handler fetchByUserId = ctx -> {
        PostDAO dao = PostDAO.instance();
        List<Post> posts = dao.getPostsByUserId(ctx.pathParam("userId"));
        ctx.json(posts);
    };

    public static Handler insertPost = ctx -> {
        PostDAO dao = PostDAO.instance();
        Post post = ctx.bodyAsClass(Post.class);
        Long id = dao.insertPost(post);
        ctx.json(id);
    };

    public static Handler deletePost = ctx -> {
        PostDAO dao = PostDAO.instance();
        long count = dao.deletePost(ctx.pathParam("id"));
        ctx.json(count);
    };

    public static Handler updatePost = ctx -> {
        PostDAO dao = PostDAO.instance();
        Post post = ctx.bodyAsClass(Post.class);
        dao.updatePost(post);
    };
}
