package org.techtown.reviewapp.home;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.techtown.reviewapp.R;
import org.techtown.reviewapp.post.Post;
import org.techtown.reviewapp.post.PostAdapter;

import java.util.ArrayList;
import java.util.Map;


public class HomeFragment extends Fragment implements PostAdapter.ItemAddListener {
    RecyclerView recyclerView;
    PostAdapter postAdapter;
    int status_num;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("SKKU");
    int list_position, add_comment, upload_num;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);

        postAdapter = new PostAdapter(getContext());
        postAdapter.itemAddListener = this;
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
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Map<String, Object> message0 = (Map<String, Object>) dataSnapshot.getValue();
            Map<String, Object> message_status = (Map<String, Object>) message0.get("Status");
            status_num = Integer.parseInt(message_status.get("num").toString());

            Map<String, Object> message1 = null;
            if(status_num >= 10){  // 아직 미완성
                for(int i = status_num ; i > status_num - 10 ; i--){
                    message1 = (Map<String, Object>) message_status.get(Integer.toString(i));
                    Map<String, Object> message_comment = (Map<String, Object>) message1.get("comments");
                    int comment_num = Integer.parseInt(message_comment.get("num").toString());

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
                    ArrayList<String> photo = new ArrayList<>();
                    photo.add(picture);

                    Post post = new Post();

                    if(picture.equals("NO")){
                        post.setStatus(id, nickname, date, text,1, "레벨 "+ level, restaurant,  like, status_num);
                        postAdapter.addItem(post);
                    } else{
                        post.setReview(id, nickname, date, text,0, "레벨 "+ level, restaurant,  like, status_num, photo, 1);
                        postAdapter.addItem(post);
                    }

                }
            } else {
                for(int i = status_num ; i >= 1 ; i--){
                    message1 = (Map<String, Object>) message_status.get(Integer.toString(i)); //게시글 정보
                    Map<String, Object> message_comment = (Map<String, Object>) message1.get("comments"); //댓글 정보
                    int comment_num = Integer.parseInt(message_comment.get("num").toString());

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
                    ArrayList<String> photo = new ArrayList<>();
                    photo.add(picture);

                    if(picture.equals("NO")){
                        Post status = new Post();
                        status.setComment_num(comment_num);
                        status.setStatus(id, nickname, date, text,1, "레벨 "+ level, restaurant,  like, i);
                        postAdapter.addItem(status);
                    } else {
                        Post review = new Post();
                        review.setComment_num(comment_num);
                        review.setReview(id, nickname, date, text,0, "레벨 "+ level, restaurant,  like, i, photo, 1);
                        postAdapter.addItem(review);
                    }

                    for(int j=comment_num ; j>=1 ; j--){
                        Map<String, Object> message2 = (Map<String, Object>) message_comment.get(Integer.toString(j));
                        date = (String) message2.get("date");
                        id = (String) message2.get("id");
                        text = (String) message2.get("text");
                        user_num = Integer.parseInt(message2.get("user_num").toString());
                        message_user = (Map<String, Object>) message0.get("user");
                        message_user2 = (Map<String, Object>) message_user.get(Integer.toString(user_num));
                        nickname = (String) message_user2.get("nickname");

                        Post comment = new Post();
                        comment.setComment(id, nickname, date, text,2);
                        postAdapter.addItem(comment);
                    }
                }
            }
            recyclerView.setAdapter(postAdapter);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    ValueEventListener dataListener2 = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            Map<String, Object> message0 = (Map<String, Object>) dataSnapshot.child("Status").child(Integer.toString(upload_num)).child("comments").getValue();
            int target_comment = Integer.parseInt(message0.get("num").toString());
            for(int i=target_comment; i>=add_comment+1; i--) {
                Map<String, Object> message1 = (Map<String, Object>) message0.get(Integer.toString(i));
                String date = (String) message1.get("date");
                String id = (String) message1.get("id");
                String text = (String) message1.get("text");
                int user_num = Integer.parseInt(message1.get("user_num").toString());

                Map<String, Object> message_user = (Map<String, Object>) dataSnapshot.child("user").child(Integer.toString(user_num)).getValue();
                String nickname = (String) message_user.get("nickname");
                //message_user = (Map<String, Object>) message0.get("user");
                //message_user2 = (Map<String, Object>) message_user.get(Integer.toString(user_num));
                //nickname = (String) message_user2.get("nickname");

                postAdapter.comment_add(list_position);
                Post comment = new Post();
                comment.setComment(id, nickname, date, text,2);
                postAdapter.addItem(comment, list_position+1);
                postAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    public void itemAdded(int prev_num, int position, int DB_num) {
        //DB에서
        add_comment = prev_num;
        list_position = position;
        upload_num = DB_num;
        reference.addListenerForSingleValueEvent(dataListener2);
    }
}
