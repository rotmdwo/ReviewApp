package org.techtown.reviewapp.review;

import org.techtown.reviewapp.comment.Comment;

import java.util.ArrayList;

public class Review {
    ArrayList<Comment> comments = new ArrayList<>();
    String user_id;
    String user_nickname;
    String user_rank;
    String restaurant;
    String date;
    String user_text;
    //String photo_num;
    int like;

    public Review(ArrayList<Comment> comments, String user_id, String user_nickname, String user_rank, String restaurant, String date, String user_text, int like) {
        this.comments = comments;
        this.user_id = user_id;
        this.user_nickname = user_nickname;
        this.user_rank = user_rank;
        this.restaurant = restaurant;
        this.date = date;
        this.user_text = user_text;
        this.like = like;
    }

    public String getUser_id() { return this.user_id; }

    public String getUser_nickname() {
        return this.user_nickname;
    }

    public String getUser_rank() {
        return this.user_rank;
    }

    public String getRestaurant() {
        return this.restaurant;
    }

    public String getDate() {
        return this.date;
    }

    public String getUser_text() {
        return this.user_text;
    }

    public int getLike() {
        return this.like;
    }
}
