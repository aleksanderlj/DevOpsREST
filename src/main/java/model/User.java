package model;

import com.mongodb.BasicDBObject;

public class User {
    private Long id;
    private String googleId;
    private String username;
    private String password;
    private String displayName;

    public User(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public BasicDBObject asFilter(){
        BasicDBObject basicDBObject = new BasicDBObject();
        if (getId() != null)            basicDBObject.append("_id", getId());
        if (getGoogleId() != null)      basicDBObject.append("googleId", getGoogleId());
        if (getUsername() != null)      basicDBObject.append("username", getUsername());
        if (getDisplayName() != null)   basicDBObject.append("displayName", getDisplayName());
        return basicDBObject;
    }
}
