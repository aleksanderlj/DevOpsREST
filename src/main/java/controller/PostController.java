package controller;
import dao.PostDAO;
import exception.InvalidPayloadException;
import io.javalin.http.Handler;
import io.prometheus.client.Counter;
import model.Post;
import token.JWTHandler;

import java.util.Comparator;
import java.util.List;

public class PostController {
    public static Counter posted = Counter.build().name("post_activity").help("Total post upload activity").register();
    public static Counter postClick = Counter.build().name("post_click").help("Total post click activity").register();

    public static Handler fetchById = ctx -> {
        PostDAO dao = PostDAO.instance();

        Post post = dao.getPostById(ctx.pathParam("id"));
        if (post != null) {
            postClick.inc();
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

    public static Handler fetchBySubforum = ctx -> {
        PostDAO dao = PostDAO.instance();
        List<Post> posts = dao.getPostsBySubforum(ctx.pathParam("forum"));
        ctx.json(posts);
        ctx.status(200);
    };

    public static Handler insertPost = ctx -> {
        PostDAO dao = PostDAO.instance();
        Post post = ctx.bodyAsClass(Post.class);
        String token = ctx.header("Authorization");
        JWTHandler.confirmIdentity(token, post.getUserId());
        Long id = dao.insertPost(post);
        posted.inc();
        ctx.json(id);
        ctx.status(200);
    };

    public static Handler deletePost = ctx -> {
        PostDAO dao = PostDAO.instance();
        Post post = dao.getPostById(ctx.pathParam("id"));
        if(post != null) {
            String token = ctx.header("Authorization");
            JWTHandler.confirmIdentity(token, post.getUserId());
            long count = dao.deletePost(ctx.pathParam("id"));
            ctx.json(count);
        } else {
            ctx.json(0);
        }
        ctx.status(200);
    };

    public static Handler updatePost = ctx -> {
        PostDAO dao = PostDAO.instance();
        Post post = ctx.bodyAsClass(Post.class);
        String token = ctx.header("Authorization");
        if(post.getId() == null){
            throw new InvalidPayloadException("Need post id to update post");
        }
        Post oldPost = dao.getPostById(Long.toString(post.getId()));
        if(oldPost != null) {
            JWTHandler.confirmIdentity(token, oldPost.getUserId());
            dao.updatePost(post);
        }
        ctx.status(200);
    };
}
