package com.leestream.artgallery.Models;

public class Notification {
    private String userID;
    private  String text;
    private String postID;

    public Notification() {
    }

    public Notification(String userID, String text, String postID) {
        this.userID = userID;
        this.text = text;
        this.postID = postID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

}
