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
    Query query = reference.child("Status").limitToLast(10);
    //Query query2;

    int list_position;
    long add_comment;
    String upload_num;
    boolean isAdded = false;
    public static HomeFragment mContext;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);
        mContext = this;
        reference.addValueEventListener(dataListener);
        postAdapter = new PostAdapter(getContext());
        postAdapter.itemAddListener = this;
        postAdapter.postOptionListener = this;
        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        recyclerView = rootView.findViewById(R.id.review_list) ;
        LinearLayoutManager manager =new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,true);
        manager.setStackFromEnd(true);
        manager.setItemPrefetchEnabled(true);  // 리사이클러뷰 정보 미리 불러오기
        recyclerView.setLayoutManager(manager);

        ImageButton imageButton = rootView.findViewById(R.id.imageButton);  // 포스트 버튼
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) HomeActivity.mContext).manager.beginTransaction().add(R.id.frameLayout,new StatusFragment()).commit();

                /*recyclerView.setLayoutManager(new LinearLayoutManager(getContext()){  // 스크롤 막기
                    @Override
                    public boolean canScrollVertically(){
                        return false;
                    }
                });
                 */

                // Status Fragment가 떴을 때 네비게이션 버튼 비활성화
                ((HomeActivity) HomeActivity.mContext).home.setEnabled(false);
                ((HomeActivity) HomeActivity.mContext).restaurant.setEnabled(false);
                ((HomeActivity) HomeActivity.mContext).settings.setEnabled(false);
                ((HomeActivity) HomeActivity.mContext).rank.setEnabled(false);
            }
        });

        return rootView;
    }

    //최초 프래그먼트 생성시 DB에서 정보를 가져오는 리스너
    ValueEventListener dataListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            //디비 안의 내용이 바뀔때마다 실행되는 구조임, 최초에만 실행되게 변경
            if(!postAdapter.posts.isEmpty()) {
                return;
            }
            //먼저 유저 정보를 dataSnapshot2에 담음
            final DataSnapshot dataSnapshot2 = dataSnapshot.child("user");
            //쿼리문: status에서 최근 10개 게시물을 불러옴
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //최근 10개 게시물에 대해 반복
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        //게시물에 필요한 변수
                        String id;
                        String date;
                        int like;
                        String picture;
                        String restaurant;
                        String text;
                        String user_num;
                        String nickname;

                        //게시물의 키값
                        String DB_key = snapshot.getKey();

                        //게시물 밑의 댓글에 대한 정보: message_comment
                        Map<String, Object> message_comment = (Map<String, Object>) snapshot.child("comments").getValue();
                        int comment_num = (int) snapshot.child("comments").getChildrenCount() -1;

                        //댓글 추가
                        for(DataSnapshot dataSnapshot1 : snapshot.child("comments").getChildren()) {
                            if(dataSnapshot1.getKey().equals("num")) {
                                continue;
                            } else {
                                Map<String, Object> message_comment2 = (Map<String, Object>) dataSnapshot1.getValue();
                                date = (String) message_comment2.get("date");
                                id = (String) message_comment2.get("id");
                                text = (String) message_comment2.get("text");
                                user_num = message_comment2.get("user_num").toString();
                                Map<String, Object> message_user2 = (Map<String, Object>) dataSnapshot2.child(user_num).getValue();
                                nickname = (String) message_user2.get("nickname");

                                Post comment = new Post();
                                comment.setComment(id, nickname, date, text, 2, dataSnapshot1.getKey());
                                postAdapter.addItem(comment);
                            }
                        }

                        //게시물에 대한 정보: message_post
                        //id, date, like, picture, restaurant, text, user_num
                        Map<String, Object> message_post = (Map<String, Object>) snapshot.getValue();
                        id = (String) message_post.get("id");
                        date = (String) message_post.get("date");
                        like = Integer.parseInt(message_post.get("like").toString());
                        picture = (String) message_post.get("picture");
                        restaurant = (String) message_post.get("restaurant");
                        text = (String) message_post.get("text");
                        user_num = message_post.get("user_num").toString();
                        //사진이 여러개일 때를 대비하여 ArrayList 선언
                        ArrayList<String> photo = new ArrayList<>();
                        photo.add(picture);

                        //게시물을 작성한 유저에 대한 정보: message_user
                        //nickname, level
                        //현재 user_num으로 찾음
                        //(주: 굳이 user_num으로 찾을 필요가 없음, id는 유니크하기 때문에 구조를 바꿀 필요가 있을듯)
                        Map<String, Object> message_user = (Map<String, Object>) dataSnapshot2.child(user_num).getValue();
                        nickname = (String) message_user.get("nickname");
                        int level = Integer.parseInt(message_user.get("level").toString());

                        //받아온 정보를 바탕으로 새로운 Post를 만든다
                        if (picture.equals("NO")) {
                            Post status = new Post();
                            status.setComment_num(comment_num);
                            status.setStatus(id, nickname, date, text, 1, "레벨 " + level, restaurant, like, DB_key);
                            postAdapter.addItem(status);
                        } else {
                            Post review = new Post();
                            review.setComment_num(comment_num);
                            review.setReview(id, nickname, date, text, 0, "레벨 " + level, restaurant, like, DB_key, photo, 1);
                            postAdapter.addItem(review);
                        }
                    }
                    //어댑터에 반영
                    recyclerView.setAdapter(postAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    //PostAdapter에서 댓글을 달았을때 DB에서 댓글을 갱신하는 리스너
    ValueEventListener dataListener2 = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            //댓글이 있고, add_comment가 그 댓글의 DB_num을 들고있음, -1이면 기존 댓글이 없음

            //해당 게시물의 댓글에 접근해서 num이나 이미 받아온 댓글빼고 다 받아서 adapter에 add해줌
            for(DataSnapshot dataSnapshot1 : dataSnapshot.child("Status").child(upload_num).child("comments").getChildren()) {
                if(dataSnapshot1.getKey().equals("num")) {
                    continue;
                } else if(Long.parseLong(dataSnapshot1.getKey()) > add_comment) {
                    Map<String, Object> message1 = (Map<String, Object>) dataSnapshot1.getValue();
                    String date = (String) message1.get("date");
                    String id = (String) message1.get("id");
                    String text = (String) message1.get("text");
                    int user_num = Integer.parseInt(message1.get("user_num").toString());

                    Map<String, Object> message_user = (Map<String, Object>) dataSnapshot.child("user").child(Integer.toString(user_num)).getValue();
                    String nickname = (String) message_user.get("nickname");

                    postAdapter.comment_add(list_position);
                    Post comment = new Post();
                    comment.setComment(id, nickname, date, text,2, dataSnapshot1.getKey());
                    postAdapter.addItem(comment, list_position);
                    postAdapter.notifyItemInserted(list_position);
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    //포스트를 추가했을때 DB에서 정보를 가져오는 리스너
    /*ValueEventListener dataListener3 = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            //먼저 유저 정보를 dataSnapshot2에 담음
            final DataSnapshot dataSnapshot2 = dataSnapshot.child("user");
            query2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d("debug", "1");
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Log.d("debug", dataSnapshot1.getKey());
                    }
                    Log.d("debug", "2");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

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
    };*/

    @Override
    public void itemAdded(long prev_num, int position, String DB_num) {
        add_comment = prev_num; //어디다가 추가할지
        list_position = position; //
        upload_num = DB_num; //게시물이 디비에서 몇번인가
        //query2 = reference.child("Status").startAt(upload_num);
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
