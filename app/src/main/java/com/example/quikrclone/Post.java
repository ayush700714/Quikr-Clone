package com.example.quikrclone;

import java.util.Date;

public class Post extends BlogPostId{
    private String title,desc,imageUrl,category,userName;
    private Date time_stamp;

    public Post() {
    }

    public Post(String title, String desc, String imageUrl, String category, String userName, Date time_stamp) {
        this.title = title;
        this.desc = desc;
        this.imageUrl = imageUrl;
        this.category = category;
        this.userName = userName;
        this.time_stamp = time_stamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(Date time_stamp) {
        this.time_stamp = time_stamp;
    }
}
