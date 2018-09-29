package com.dev.fd.feederdaddy.model;

public class Reviews {
    private String Username,Rate,Comment;

    public Reviews(){

    }

    public Reviews(String username, String rate, String comment) {
        Username = username;
        Rate = rate;
        Comment = comment;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getRate() {
        return Rate;
    }

    public void setRate(String rate) {
        Rate = rate;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }
}
