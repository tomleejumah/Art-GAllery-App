package com.leestream.artgallery.Models;

public class User {
    private String Bio;
    private String Email;
    private String ID;
    private String FirstName;
    private String LastName;
    private String UserName;
    private String imageUrl;

    public User() {
    }

    public User(String bio, String email, String ID, String firstName, String lastName, String userName, String imageUrl) {
        Bio = bio;
        Email = email;
        this.ID = ID;
        FirstName = firstName;
        LastName = lastName;
        UserName = userName;
        this.imageUrl = imageUrl;
    }

    public String getBio() {
        return Bio;
    }

    public void setBio(String bio) {
        Bio = bio;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
