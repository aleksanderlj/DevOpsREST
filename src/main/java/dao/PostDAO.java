package dao;

import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Field;
import exception.InvalidPayloadException;
import model.Post;
import mongo.SingleDinkleMan;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
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
                .aggregate(Arrays.asList(
                        Aggregates.match(eq("_id", new Long(id))
                        ),
                        Aggregates.lookup(
                                "likes",
                                "_id",
                                "postId",
                                "likeCount"
                        ),
                        Aggregates.addFields(new Field("likeCount", new BasicDBObject("$size", "$likeCount")))
                        ))
                .first();
    }

    public List<Post> getPosts() throws  UnknownHostException {
        MongoDatabase db = SingleDinkleMan.instance();

        AggregateIterable<Post> iterable = db.getCollection("posts", Post.class)
                .aggregate(Arrays.asList(
                        Aggregates.lookup(
                                "likes",
                                "_id",
                                "postId",
                                "likeCount"
                        ),
                        Aggregates.addFields(new Field("likeCount", new BasicDBObject("$size", "$likeCount")))
                ));

        List<Post> result = new ArrayList<>();
        iterable.forEach(doc -> result.add(doc));
        return result;
    }

    public List<Post> getPostsByUserId(String id) throws UnknownHostException {
        MongoDatabase db = SingleDinkleMan.instance();

        AggregateIterable<Post> iterable = db.getCollection("posts", Post.class)
                .aggregate(Arrays.asList(
                        Aggregates.match(eq("userId", new Long(id))
                        ),
                        Aggregates.lookup(
                                "likes",
                                "_id",
                                "postId",
                                "likeCount"
                        ),
                        Aggregates.addFields(new Field("likeCount", new BasicDBObject("$size", "$likeCount")))
                ));

        List<Post> result = new ArrayList<Post>();
        iterable.forEach(doc -> result.add(doc));
        return result;
    }

    public List<Post> getPostsBySubforum(String forum) throws UnknownHostException {
        MongoDatabase db = SingleDinkleMan.instance();

        AggregateIterable<Post> iterable = db.getCollection("posts", Post.class)
                .aggregate(Arrays.asList(
                        Aggregates.match(eq("subforum", forum)
                        ),
                        Aggregates.lookup(
                                "likes",
                                "_id",
                                "postId",
                                "likeCount"
                        ),
                        Aggregates.addFields(new Field("likeCount", new BasicDBObject("$size", "$likeCount")))
                ));

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
