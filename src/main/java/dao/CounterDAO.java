package dao;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import model.Counter;
import mongo.SingleDinkleMan;
import org.bson.Document;

import java.net.UnknownHostException;

import static com.mongodb.client.model.Filters.eq;

public class CounterDAO {
    public static Long getNextSequenceValue(String seq) throws UnknownHostException {
        MongoDatabase db = SingleDinkleMan.instance();
        BasicDBObject increase = new BasicDBObject("sequence_value", 1);
        BasicDBObject update = new BasicDBObject("$inc", increase);
        Document doc = db.getCollection("counters")
                .findOneAndUpdate(eq("_id", seq), update);
        return doc != null ? doc.getLong("sequence_value") : null;
    }

    public static void initiateCounters() throws UnknownHostException {
        MongoDatabase db = SingleDinkleMan.instance();
        MongoCollection<Counter> collection = db.getCollection("counters", Counter.class);
        collection.insertOne(new Counter("postid", 1L));
        collection.insertOne(new Counter("userid", 1L));
        collection.insertOne(new Counter("likeid", 1L));
    }
}
