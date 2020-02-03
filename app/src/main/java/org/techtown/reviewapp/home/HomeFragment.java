package org.techtown.reviewapp.home;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import org.techtown.reviewapp.R;
import org.techtown.reviewapp.comment.Comment;
import org.techtown.reviewapp.review.Review;
import org.techtown.reviewapp.review.ReviewAdapter;

import java.util.ArrayList;


public class HomeFragment extends Fragment {
    RecyclerView recyclerView;
    ReviewAdapter reviewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);

        reviewAdapter = new ReviewAdapter(getContext());

        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        recyclerView = rootView.findViewById(R.id.review_list) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false)) ;


        ArrayList<Comment> comments1 = new ArrayList<>();
        Comment comment1 = new Comment("bestowing", "아싸라비야", "1년전");
        Comment comment2 = new Comment("bestowing", "으이구", "1년전");
        comments1.add(comment1);
        comments1.add(comment2);
        reviewAdapter.addReview(new Review(comments1, "bestowing", "청수", "레벨 10", "중국성", "2년전", "여기 진짜 맛있다.", 3));

        recyclerView.setAdapter(reviewAdapter);

        ImageButton imageButton = rootView.findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) HomeActivity.mContext).manager.beginTransaction().add(R.id.frameLayout,new StatusFragment()).commit();
            }
        });

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();

    }

}
