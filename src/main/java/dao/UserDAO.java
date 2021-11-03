package dao;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertOneResult;
import model.User;
import mongo.SingleDinkleMan;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class UserDAO {
    private static UserDAO userDAO = null;

    public static UserDAO instance(){
        if(userDAO == null){
            userDAO = new UserDAO();
        }
        return userDAO;
    }

    public List<User> getUsersByFilter(User user) throws UnknownHostException {
        MongoDatabase db = SingleDinkleMan.instance();

        MongoCollection<User> collection = db.getCollection("users", User.class);
        FindIterable<User> iterable = db.getCollection("users", User.class)
                .find(user.asBasicDBObject());

        List<User> result = new ArrayList<>();
        iterable.forEach(doc -> result.add(doc));
        return result;
    };

    public User getUserById(String id) throws UnknownHostException {
        MongoDatabase db = SingleDinkleMan.instance();

        MongoCollection<User> collection = db.getCollection("users", User.class);
        return db.getCollection("users", User.class)
                .find(eq("_id", new ObjectId(id)))
                .first();
    }

    public ObjectId insertUser(User user) throws UnknownHostException {
        MongoDatabase db = SingleDinkleMan.instance();
        InsertOneResult result = db.getCollection("users", User.class).insertOne(user);
        return null;
    }
}
