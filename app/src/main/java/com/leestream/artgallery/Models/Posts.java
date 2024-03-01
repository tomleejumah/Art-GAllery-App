package com.leestream.artgallery.Models;

public class Posts {
    private String imageUrl;
    private String postID;
    private String PublisherID;
    private String description;
    private String UserName;
    private String price;
    private String Category;

    public Posts() {
    }

    public Posts(String imageUrl, String postID, String publisherID, String description,
                 String userName, String price, String category) {
        this.imageUrl = imageUrl;
        this.postID = postID;
        PublisherID = publisherID;
        this.description = description;
        UserName = userName;
        this.price = price;
        Category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }


    public String getPublisherID() {
        return PublisherID;
    }

    public void setPublisherID(String publisherID) {
        PublisherID = publisherID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }
}
