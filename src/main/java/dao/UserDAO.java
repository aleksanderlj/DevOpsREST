package dao;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import model.User;
import mongo.SingleDinkleMan;

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

        FindIterable<User> iterable = db.getCollection("users", User.class)
                .find(user.asBasicDBObject());

        List<User> result = new ArrayList<>();
        iterable.forEach(doc -> result.add(doc));
        return result;
    };

    public User getUserById(String id) throws UnknownHostException {
        MongoDatabase db = SingleDinkleMan.instance();

        return db.getCollection("users", User.class)
                .find(eq("_id", new Long(id)))
                .first();
    }

    public Long insertUser(User user) throws UnknownHostException {
        MongoDatabase db = SingleDinkleMan.instance();
        Long id = CounterDAO.getNextSequenceValue("userid");
        user.setId(id);
        db.getCollection("users", User.class).insertOne(user);
        return id;
    }
}
