package org.techtown.reviewapp.home;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.techtown.reviewapp.R;
import org.techtown.reviewapp.post.Post;
import org.techtown.reviewapp.post.PostAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;


public class HomeFragment extends Fragment implements PostAdapter.ItemAddListener, PostAdapter.PostOptionListener {
    RecyclerView recyclerView;
    PostAdapter postAdapter;
    int status_num;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("SKKU");
    Query query = reference.child("Status").limitToLast(11);

    int list_position, add_comment;
    String upload_num;
    public static HomeFragment mContext;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);
        mContext = this;
        query.addValueEventListener(dataListener);

        postAdapter = new PostAdapter(getContext());
        postAdapter.itemAddListener = this;
        postAdapter.postOptionListener = this;
        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        recyclerView = rootView.findViewById(R.id.review_list) ;
        LinearLayoutManager manager =new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        manager.setItemPrefetchEnabled(true);  // 리사이클러뷰 정보 미리 불러오기
        recyclerView.setLayoutManager(manager);

        ImageButton imageButton = rootView.findViewById(R.id.imageButton);  // 포스트 버튼
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) HomeActivity.mContext).manager.beginTransaction().add(R.id.frameLayout,new StatusFragment()).commit();
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()){  // 스크롤 막기
                    @Override
                    public boolean canScrollVertically(){
                        return false;
                    }
                });

                // Status Fragment가 떴을 때 네비게이션 버튼 비활성화
                ((HomeActivity) HomeActivity.mContext).home.setEnabled(false);
                ((HomeActivity) HomeActivity.mContext).restaurant.setEnabled(false);
                ((HomeActivity) HomeActivity.mContext).settings.setEnabled(false);
                ((HomeActivity) HomeActivity.mContext).rank.setEnabled(false);
            }
        });

        return rootView;
    }

    ValueEventListener dataListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                if (snapshot.getKey().equals("num")) {
                    continue;
                }
                final String DB_key = snapshot.getKey();
                Map<String, Object> message1 = (Map<String, Object>) snapshot.getValue();
                final Map<String, Object> message_comment = (Map<String, Object>) message1.get("comments"); //댓글 정보

                String id = (String) message1.get("id");
                int user_num = Integer.parseInt(message1.get("user_num").toString());
                String restaurant = (String) message1.get("restaurant");
                String date = (String) message1.get("date");
                String text = (String) message1.get("text");
                int like = Integer.parseInt(message1.get("like").toString());
                String picture = (String) message1.get("picture");

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d("shit", "안에: " + DB_key);
                        int comment_num = Integer.parseInt(message_comment.get("num").toString());
                        Map<String, Object> message_user = (Map<String, Object>) dataSnapshot.child("user").child(Integer.toString(user_num)).getValue();
                        String nickname = (String) message_user.get("nickname");
                        int level = Integer.parseInt(message_user.get("level").toString());

                        ArrayList<String> photo = new ArrayList<>();
                        photo.add(picture);

                        if (picture.equals("NO")) {
                            Post status = new Post();
                            status.setComment_num(comment_num);
                            status.setStatus(id, nickname, date, text, 1, "레벨 " + level, restaurant, like, DB_key);
                            Log.d("debug", status.getUser_text());
                            postAdapter.addItem(status);
                        } else {
                            Post review = new Post();
                            review.setComment_num(comment_num);
                            review.setReview(id, nickname, date, text, 0, "레벨 " + level, restaurant, like, DB_key, photo, 1);
                            Log.d("debug", review.getUser_text());
                            postAdapter.addItem(review);
                        }

                        for (int j = comment_num; j >= 1; j--) {
                            Map<String, Object> message2 = (Map<String, Object>) message_comment.get(Integer.toString(j));
                            String date = (String) message2.get("date");
                            String id = (String) message2.get("id");
                            String text = (String) message2.get("text");
                            int user_num = Integer.parseInt(message2.get("user_num").toString());
                            message_user = (Map<String, Object>) dataSnapshot.child("user").child(Integer.toString(user_num)).getValue();
                            nickname = (String) message_user.get("nickname");

                            Post comment = new Post();
                            comment.setComment(id, nickname, date, text, 2);
                            postAdapter.addItem(comment);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
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

            Map<String, Object> message0 = (Map<String, Object>) dataSnapshot
                    .child("Status")
                    .child(upload_num)
                    .child("comments").getValue();

            int target_comment = Integer.parseInt(message0.get("num").toString());
            for(int i=add_comment+1; i<=target_comment; i++) {
                Map<String, Object> message1 = (Map<String, Object>) message0.get(Integer.toString(i));
                String date = (String) message1.get("date");
                String id = (String) message1.get("id");
                String text = (String) message1.get("text");
                int user_num = Integer.parseInt(message1.get("user_num").toString());

                Map<String, Object> message_user = (Map<String, Object>) dataSnapshot.child("user").child(Integer.toString(user_num)).getValue();
                String nickname = (String) message_user.get("nickname");

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
    /*
    ValueEventListener dataListener3 = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Map<String, Object> message0 = (Map<String, Object>) dataSnapshot.getValue();
            Map<String, Object> message_status = (Map<String, Object>) message0.get("Status");
            status_num = Integer.parseInt(message_status.get("num").toString());

            Map<String, Object> message1 = null;
            for(int i = postAdapter.getFirst_DB_num()+1; i <= status_num; i++) {
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

                if (picture.equals("NO")) {
                    Post status = new Post();
                    status.setComment_num(comment_num);
                    status.setStatus(id, nickname, date, text, 1, "레벨 " + level, restaurant, like, i);
                    postAdapter.addItem(status, 0);
                } else {
                    Post review = new Post();
                    review.setComment_num(comment_num);
                    review.setReview(id, nickname, date, text, 0, "레벨 " + level, restaurant, like, i, photo, 1);
                    postAdapter.addItem(review, 0);
                }

                for (int j = comment_num; j >= 1; j--) {
                    Map<String, Object> message2 = (Map<String, Object>) message_comment.get(Integer.toString(j));
                    date = (String) message2.get("date");
                    id = (String) message2.get("id");
                    text = (String) message2.get("text");
                    user_num = Integer.parseInt(message2.get("user_num").toString());
                    message_user = (Map<String, Object>) message0.get("user");
                    message_user2 = (Map<String, Object>) message_user.get(Integer.toString(user_num));
                    nickname = (String) message_user2.get("nickname");

                    Post comment = new Post();
                    comment.setComment(id, nickname, date, text, 2);
                    postAdapter.addItem(comment,1);
                }
                postAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(0);  //자동 스크롤
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

     */

    @Override
    public void itemAdded(int prev_num, int position, String DB_num) {
        add_comment = prev_num; //어디다가 추가할지
        list_position = position; //
        upload_num = DB_num; //디비에서 몇번인가
        reference.addListenerForSingleValueEvent(dataListener2);
    }

    public void PostAdded() {
        //reference.addListenerForSingleValueEvent(dataListener3);
    }

    @Override
    public void optionTouched(int post_num_in_DB, Boolean isWriter) {
        ((HomeActivity) HomeActivity.mContext).
                manager.beginTransaction().
                setCustomAnimations(R.anim.anim_slide_in_bottom, R.anim.anim_slide_out_bottom).
                add(R.id.frameLayout,new PostOptionFragment()).
                commit();
    }

    @Override
    public void commentTouched(int comment_num_in_DB, Boolean isWriter) {
        ((HomeActivity) HomeActivity.mContext).
                manager.beginTransaction().
                setCustomAnimations(R.anim.anim_slide_in_bottom, R.anim.anim_slide_out_bottom).
                add(R.id.frameLayout,new CommentOptionFragment()).
                commit();
    }
}
