package org.techtown.reviewapp.UserRank;

public class UserRank implements Comparable<UserRank>{
    String id;
    String nickname;
    int exp;
    int num_of_reviews;

    public UserRank(String id, String nickname, int exp, int num_of_reviews) {
        this.id = id;
        this.nickname = nickname;
        this.exp = exp;
        this.num_of_reviews = num_of_reviews;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getNum_of_reviews() {
        return num_of_reviews;
    }

    public void setNum_of_reviews(int num_of_reviews) {
        this.num_of_reviews = num_of_reviews;
    }

    @Override
    public int compareTo(UserRank o) {
        if(exp > o.getExp()){
            return -1;
        } else if(exp < o.getExp()){
            return 1;
        } else{
            return 0;
        }
    }
}
