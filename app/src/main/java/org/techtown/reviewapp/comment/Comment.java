package org.techtown.reviewapp.comment;

public class Comment {
    //클래스 안의 변수는 카멜 케이스가 아닌 _로 구분.
    String user_id;
    String user_nickname;
    String comment_text;
    String date;

    //생성자
    public Comment(String user_id, String comment_text, String date) {
        this.user_id = user_id;
        this.comment_text = comment_text;
        this.date = date;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public String getComment_text() {
        return comment_text;
    }

    public void setComment_text(String comment_text) {
        this.comment_text = comment_text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


}
