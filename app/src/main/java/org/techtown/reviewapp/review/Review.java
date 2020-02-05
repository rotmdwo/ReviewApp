package org.techtown.reviewapp.review;

import android.widget.ArrayAdapter;

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
    int like;
    ArrayList<String> photo = new ArrayList<>();
    int photo_num;

    public Review(ArrayList<Comment> comments, String user_id, String user_nickname, String user_rank, String restaurant, String date, String user_text, int like, int photo_num, ArrayList<String> photo) {
        this.comments = comments;
        this.user_id = user_id;
        this.user_nickname = user_nickname;
        this.user_rank = user_rank;
        this.restaurant = restaurant;
        this.date = date;
        this.user_text = user_text;
        this.like = like;
        this.photo_num = photo_num;
        this.photo = photo;
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

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public void setUser_rank(String user_rank) {
        this.user_rank = user_rank;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setUser_text(String user_text) {
        this.user_text = user_text;
    }

    public void setLike(int like) {
        this.like = like;
    }


    public int getPhoto_num() {
        return photo_num;
    }

    public void setPhoto_num(int photo_num) {
        this.photo_num = photo_num;
    }

    public ArrayList<String> getPhoto() {
        return photo;
    }

    public void setPhoto(ArrayList<String> photo) {
        this.photo = photo;
    }
}
