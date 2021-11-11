package dao;

import com.mongodb.MongoBulkWriteException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoDatabase;
import model.Like;
import mongo.SingleDinkleMan;

import java.net.UnknownHostException;

import static com.mongodb.client.model.Filters.eq;

public class LikeDAO {
    private static LikeDAO likeDAO = null;

    public static LikeDAO instance(){
        if(likeDAO == null){
            likeDAO = new LikeDAO();
        }
        return likeDAO;
    }

    public boolean likePost(Long postId, Long userId) throws UnknownHostException {
        MongoDatabase db = SingleDinkleMan.instance();

        Like like = new Like();
        like.setId(CounterDAO.getNextSequenceValue("likeid"));
        like.setPostId(postId);
        like.setUserId(userId);

        boolean result;
        try {
            db.getCollection("likes", Like.class)
                    .insertOne(like);
            result = true;
        } catch (MongoWriteException e){
            result = false;
        }

        return result;
    }

    public long getPostLikeCount(Long postId) throws UnknownHostException {
        MongoDatabase db = SingleDinkleMan.instance();
        return db.getCollection("likes", Like.class)
                .countDocuments(eq("postId", postId));
    }
}
