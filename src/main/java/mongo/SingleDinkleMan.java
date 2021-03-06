package mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import util.PropFile;

import java.net.UnknownHostException;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class SingleDinkleMan {
    private static MongoClient mongoClient = null;
    private static final String URI = PropFile.getProperty("mongodb");

    public static MongoDatabase instance() throws UnknownHostException {
        if(mongoClient == null){
            mongoClient = MongoClients.create(URI);
        }

        return mongoClient.getDatabase(PropFile.getProperty("mongo-env"))
                .withCodecRegistry(getPOJOCodecRegistry());
    }

    private static CodecRegistry getPOJOCodecRegistry(){
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        return fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
    }
}
