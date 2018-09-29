package com.dev.fd.feederdaddy.model;

public class Rating {
    private String Username,Comment,Rate;

    public Rating() {
    }

    public Rating(String username, String comment, String rate) {
        Username = username;
        Comment = comment;
        Rate = rate;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getRate() {
        return Rate;
    }

    public void setRate(String rate) {
        Rate = rate;
    }
}
