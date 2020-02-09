package org.techtown.reviewapp.Restaurants;

public class Restaurant implements Comparable<Restaurant>{
    String name;
    String picture;
    float rating;
    int num_of_reviews;

    public Restaurant(String name, String picture, float rating, int num_of_reviews) {
        this.name = name;
        this.picture = picture;
        this.rating = rating;
        this.num_of_reviews = num_of_reviews;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getNum_of_reviews() {
        return num_of_reviews;
    }

    public void setNum_of_reviews(int num_of_reviews) {
        this.num_of_reviews = num_of_reviews;
    }

    @Override
    public int compareTo(Restaurant o) {  // 리뷰 개수 당 0.01의 가중치를 줌
        if(rating + (float)num_of_reviews*0.01 > o.getRating() + (float) num_of_reviews*0.01){
            return -1;
        } else if(rating + (float)num_of_reviews*0.01 < o.getRating() + (float) num_of_reviews*0.01){
            return 1;
        } else{
            return 0;
        }
    }
}
