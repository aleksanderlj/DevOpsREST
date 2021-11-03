package dao;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoDatabase;
import mongo.SingleDinkleMan;
import org.bson.Document;

import java.net.UnknownHostException;

import static com.mongodb.client.model.Filters.eq;

public class CounterDAO {
    private static CounterDAO counterDAO = null;

    public static CounterDAO instance(){
        if(counterDAO == null){
            counterDAO = new CounterDAO();
        }
        return counterDAO;
    }

    public static Long getNextSequenceValue(String seq) throws UnknownHostException {
        MongoDatabase db = SingleDinkleMan.instance();
        BasicDBObject increase = new BasicDBObject("sequence_value", 1);
        BasicDBObject update = new BasicDBObject("$inc", increase);
        Document doc = db.getCollection("counters")
                .findOneAndUpdate(eq("_id", seq), update);
        return doc != null ? doc.getLong("sequence_value") : null;
    }
}
