package org.techtown.reviewapp.Restaurants.restaurant;

import java.util.ArrayList;

public class Review {
    String user_id, user_nickname, date, user_text, DB_num;
    String user_rank;
    int type;
    int like = 0;
    ArrayList<String> photo = null;
    int photo_num;

    //사진있는 리뷰
    public void setReview(String user_id, String user_nickname, String date, String user_text, int type, String user_rank, int like, String DB_num, ArrayList<String> photo, int photo_num) {
        this.user_id = user_id;
        this.user_nickname = user_nickname;
        this.date = date;
        this.user_text = user_text;
        this.type = type;
        this.user_rank = user_rank;
        this.like = like;
        this.DB_num = DB_num;
        this.photo = photo;
        this.photo_num = photo_num;
    }

    //사진없는 리뷰
    public void setNoPhoto(String user_id, String user_nickname, String date, String user_text, int type, String user_rank, int like, String DB_num) {
        this.user_id = user_id;
        this.user_nickname = user_nickname;
        this.date = date;
        this.user_text = user_text;
        this.type = type;
        this.user_rank = user_rank;
        this.like = like;
        this.DB_num = DB_num;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public String getDate() {
        return date;
    }

    public String getUser_text() {
        return user_text;
    }

    public String getUser_rank() {
        return user_rank;
    }

    public int getType() {
        return type;
    }

    public ArrayList<String> getPhoto() {
        return photo;
    }

    public int getPhoto_num() {
        return photo_num;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public String getDB_num() {
        return DB_num;
    }
}
