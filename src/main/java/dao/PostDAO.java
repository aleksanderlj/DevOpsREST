package dao;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import exception.InvalidPayloadException;
import model.Post;
import mongo.SingleDinkleMan;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class PostDAO {
    private static PostDAO postDao = null;

    public static PostDAO instance() {
        if (postDao == null){
            postDao = new PostDAO();
        }
        return postDao;
    }

    public Post getPostById(String id) throws UnknownHostException {
        MongoDatabase db = SingleDinkleMan.instance();

        return db.getCollection("posts", Post.class)
                .find(eq("_id", new Long(id)))
                .first();
    }

    public List<Post> getPosts() throws  UnknownHostException {
        MongoDatabase db = SingleDinkleMan.instance();

        FindIterable<Post> iterable = db.getCollection("posts", Post.class).find();

        List<Post> result = new ArrayList<Post>();
        iterable.forEach(doc -> result.add(doc));
        return result;
    }

    public List<Post> getPostsByUserId(String id) throws UnknownHostException {
        MongoDatabase db = SingleDinkleMan.instance();

        FindIterable<Post> iterable = db.getCollection("posts", Post.class).find(eq("userId", new Long(id)));

        List<Post> result = new ArrayList<Post>();
        iterable.forEach(doc -> result.add(doc));
        return result;
    }

    public Long insertPost(Post post) throws UnknownHostException {
        MongoDatabase db = SingleDinkleMan.instance();

        Long id = CounterDAO.getNextSequenceValue("postid");
        post.setId(id);
        db.getCollection("posts", Post.class).insertOne(post);
        return id;
    }

    public long deletePost(String id) throws UnknownHostException {
        MongoDatabase db = SingleDinkleMan.instance();
        return db.getCollection("posts", Post.class)
                .deleteOne(eq("_id", new Long(id)))
                .getDeletedCount();
    }

    public void updatePost(Post post) throws UnknownHostException, InvalidPayloadException {
        if (post.getId() != null) {
            MongoDatabase db = SingleDinkleMan.instance();
            db.getCollection("posts", Post.class)
                    .replaceOne(eq("_id", post.getId()), post);
        } else {
            throw new InvalidPayloadException("Missing ID");
        }
    }
}
