package com.starfishlam.doeok.details;

public class Review {
    String fullname;
    String commentDate;
    int rating;
    String title;
    String content;

    public Review(String fullname, String commentDate, int rating, String title, String content) {
        this.fullname = fullname;
        this.commentDate = commentDate;
        this.rating = rating;
        this.title = title;
        this.content = content;
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
}
