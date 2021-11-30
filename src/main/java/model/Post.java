package model;

import org.bson.codecs.pojo.annotations.BsonIgnore;

import java.util.Date;

public class Post {
    private Long id;
    private String title;
    private String content;
    private Long userId;
    private Date postDate;
    private String imageUrl;
    private int likeCount;
    private String subforum;

    public Post() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSubforum(){
        return subforum;
    }

    public void setSubforum(String subforum) {
        this.subforum = subforum;
    }

    @BsonIgnore // Makes likeCount only show on output
    public int getLikeCount() {return likeCount;}

    public void setLikeCount(int likeCount) {this.likeCount = likeCount;}
}
