package org.techtown.reviewapp.post;

import java.util.ArrayList;

public class Post {
    //Variables
    //Review, Status, 댓글의 공통적인 요소
    String user_id, user_nickname, date, user_text;
    int type;

    //Review, Status 공통 요소
    String user_rank, restaurant;
    int like = 0;
    int comment_num, DB_num;

    //Review 요소
    ArrayList<String> photo = null;
    int photo_num = 0;

    //methods
    //Item이 리뷰일때 setter 메서드
    public void setReview(String user_id, String user_nickname, String date, String user_text, int type, String user_rank, String restaurant, int like, int DB_num, ArrayList<String> photo, int photo_num) {
        this.user_id = user_id;
        this.user_nickname = user_nickname;
        this.date = date;
        this.user_text = user_text;
        this.type = type;
        this.user_rank = user_rank;
        this.restaurant = restaurant;
        this.like = like;
        this.DB_num = DB_num;
        this.photo = photo;
        this.photo_num = photo_num;
    }

    //Item이 Status일때 setter 메서드
    public void setStatus(String user_id, String user_nickname, String date, String user_text, int type, String user_rank, String restaurant, int like, int DB_num) {
        this.user_id = user_id;
        this.user_nickname = user_nickname;
        this.date = date;
        this.user_text = user_text;
        this.type = type;
        this.user_rank = user_rank;
        this.restaurant = restaurant;
        this.like = like;
        this.DB_num = DB_num;
    }

    //Item이 댓글일때 setter 메서드
    public void setComment(String user_id, String user_nickname, String date, String user_text, int type) {
        this.user_id = user_id;
        this.user_nickname = user_nickname;
        this.date = date;
        this.user_text = user_text;
        this.type = type;
    }

    //getter 메서드
    public String getUser_id() {
        return user_id;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public String getUser_rank() {
        return user_rank;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public String getDate() {
        return date;
    }

    public String getUser_text() {
        return user_text;
    }

    public int getLike() {
        return like;
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

    public int getComment_num() {
        return comment_num;
    }

    public void setComment_num(int comment_num) {
        this.comment_num = comment_num;
    }

    public int getDB_num() {
        return DB_num;
    }

    public void setDB_num(int DB_num) {
        this.DB_num = DB_num;
    }

    public void setLike(int like) {
        this.like = like;
    }
}
