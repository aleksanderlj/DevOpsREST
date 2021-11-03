package model;

import com.mongodb.BasicDBObject;
import org.bson.types.ObjectId;

public class User {
    private ObjectId id;
    private String googleId;
    private String name;

    public User(){}

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BasicDBObject asBasicDBObject(){
        BasicDBObject basicDBObject = new BasicDBObject();
        if (getId() != null)        basicDBObject.append("_id", getId());
        if (getGoogleId() != null)  basicDBObject.append("googleId", getGoogleId());
        if (getName() != null)      basicDBObject.append("name", getName());
        return basicDBObject;
    }
}
