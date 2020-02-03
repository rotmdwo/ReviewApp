package org.techtown.reviewapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import org.techtown.reviewapp.comment.Comment;
import org.techtown.reviewapp.review.Review;
import org.techtown.reviewapp.review.ReviewAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ReviewAdapter reviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reviewAdapter = new ReviewAdapter(this);

        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        recyclerView = findViewById(R.id.review_list) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(this)) ;

        recyclerView.setAdapter(reviewAdapter);

        ArrayList<Comment> comments1 = new ArrayList<>();
        Comment comment1 = new Comment("bestowing", "아싸라비야", "1년전");
        Comment comment2 = new Comment("bestowing", "으이구", "1년전");
        comments1.add(comment1);
        comments1.add(comment2);
        reviewAdapter.addReview(new Review(comments1, "bestowing", "청수", "레벨 10", "중국성", "2년전", "여기 진짜 맛있다.", 3));
    }
}
