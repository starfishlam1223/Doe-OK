package com.starfishlam.doeok;

import java.io.Serializable;

public class Review implements Serializable {
    String fullname;
    String commentDate;
    int id, rating;
    String title;
    String content;
    String restName;

    public Review(String restName, String fullname, String commentDate, int rating, String title, String content) {
        this.fullname = fullname;
        this.commentDate = commentDate;
        this.rating = rating;
        this.title = title;
        this.content = content;
        this.restName = restName;
    }

    public Review(int id, String restName, String fullname, String commentDate, int rating, String title, String content) {
        this.fullname = fullname;
        this.commentDate = commentDate;
        this.id = id;
        this.rating = rating;
        this.title = title;
        this.content = content;
        this.restName = restName;
    }

    public String getFullname() {
        return fullname;
    }

    public String getCommentDate() {
        return commentDate;
    }

    public int getRating() {
        return rating;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public int getId() {
        return id;
    }

    public String getRestName() {
        return restName;
    }
}
