package org.techtown.reviewapp.home;


import android.content.Context;
import android.database.SQLException;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.techtown.reviewapp.R;
import org.techtown.reviewapp.comment.Comment;
import org.techtown.reviewapp.review.Review;
import org.techtown.reviewapp.review.ReviewAdapter;

import java.util.ArrayList;
import java.util.Map;


public class HomeFragment extends Fragment {
    RecyclerView recyclerView;
    ReviewAdapter reviewAdapter;
    int status_num;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("SKKU");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);

        reviewAdapter = new ReviewAdapter(getContext());

        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        recyclerView = rootView.findViewById(R.id.review_list) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false)) ;

        reference.addListenerForSingleValueEvent(dataListener);

        ImageButton imageButton = rootView.findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) HomeActivity.mContext).manager.beginTransaction().add(R.id.frameLayout,new StatusFragment()).commit();
            }
        });

        return rootView;
    }

    ValueEventListener dataListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) { Log.d("asdf","1");
            Map<String, Object> message0 = (Map<String, Object>) dataSnapshot.getValue();
            Map<String, Object> message_status = (Map<String, Object>) message0.get("Status");
            status_num = Integer.parseInt(message_status.get("num").toString());  Log.d("asdf","2");
            if(status_num >= 10){  // 아직 미완성
                for(int i = status_num ; i > status_num - 10 ; i--){
                    Map<String, Object> message1 = (Map<String, Object>) message_status.get(Integer.toString(i));
                    //reviewAdapter.addReview(new Review(comments1, "bestowing", "청수", "레벨 10", "중국성", "2년전", "여기 진짜 맛있다.", 3,0));
                }
            } else{  Log.d("asdf","3");
                for(int i = status_num ; i >= 1 ; i--){
                    Map<String, Object> message1 = (Map<String, Object>) message_status.get(Integer.toString(i));
                    Map<String, Object> message_comment = (Map<String, Object>) message1.get("comments");
                    int comment_num = Integer.parseInt(message_comment.get("num").toString());  Log.d("asdf","4");
                    ArrayList<Comment> comments = new ArrayList<>();
                    for(int j=1 ; j<=comment_num ; j++){
                        Map<String, Object> message2 = (Map<String, Object>) message_comment.get(Integer.toString(j));
                        String date = (String) message2.get("date");
                        String id = (String) message2.get("id");
                        String text = (String) message2.get("text");

                        comments.add(new Comment(id,text,date));
                    }

                    String id = (String) message1.get("id");
                    int user_num = Integer.parseInt(message1.get("user_num").toString());
                    Map<String, Object> message_user = (Map<String, Object>) message0.get("user");
                    Map<String, Object> message_user2 = (Map<String, Object>) message_user.get(Integer.toString(user_num));
                    String nickname = (String) message_user2.get("nickname");
                    int level = Integer.parseInt(message_user2.get("level").toString());
                    String restaurant = (String) message1.get("restaurant");
                    String date = (String) message1.get("date");
                    String text = (String) message1.get("text");
                    int like = Integer.parseInt(message1.get("like").toString());
                    String picture = (String) message1.get("picture");

                    if(picture.equals("NO")){
                        reviewAdapter.addReview(new Review(comments, id, nickname, "레벨 "+Integer.toString(level), restaurant, date, text, like,0));
                    } else{
                        reviewAdapter.addReview(new Review(comments, id, nickname, "레벨 "+Integer.toString(level), restaurant, date, text, like,1));
                    }
                }
            }

            recyclerView.setAdapter(reviewAdapter);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };


}
