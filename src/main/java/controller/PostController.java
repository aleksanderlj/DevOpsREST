package controller;
import dao.PostDAO;
import io.javalin.http.Handler;
import model.Post;

import java.util.Comparator;
import java.util.List;

public class PostController {
    public static Handler fetchById = ctx -> {
        PostDAO dao = PostDAO.instance();

        Post post = dao.getPostById(ctx.pathParam("id"));
        if (post != null) {
            ctx.json(post);
        }
        ctx.status(200);
    };

    public static Handler fetchAll = ctx -> {
        PostDAO dao = PostDAO.instance();
        List<Post> posts = dao.getPosts();
        posts.sort(Comparator.comparing(Post::getPostDate).reversed());
        ctx.json(posts);
        ctx.status(200);
    };

    public static Handler fetchByUserId = ctx -> {
        PostDAO dao = PostDAO.instance();
        List<Post> posts = dao.getPostsByUserId(ctx.pathParam("userId"));
        ctx.json(posts);
        ctx.status(200);
    };

    public static Handler insertPost = ctx -> {
        PostDAO dao = PostDAO.instance();
        Post post = ctx.bodyAsClass(Post.class);
        Long id = dao.insertPost(post);
        ctx.json(id);
        ctx.status(200);
    };

    public static Handler deletePost = ctx -> {
        PostDAO dao = PostDAO.instance();
        long count = dao.deletePost(ctx.pathParam("id"));
        ctx.json(count);
        ctx.status(200);
    };

    public static Handler updatePost = ctx -> {
        PostDAO dao = PostDAO.instance();
        Post post = ctx.bodyAsClass(Post.class);
        dao.updatePost(post);
        ctx.status(200);
    };
}
