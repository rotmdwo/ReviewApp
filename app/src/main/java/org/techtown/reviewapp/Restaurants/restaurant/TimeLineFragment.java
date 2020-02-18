package org.techtown.reviewapp.Restaurants.restaurant;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.techtown.reviewapp.R;
import org.techtown.reviewapp.Restaurants.RestaurantActivity;
import org.techtown.reviewapp.home.HomeFragment;
import org.techtown.reviewapp.post.AddPostListener;
import org.techtown.reviewapp.post.Post;

import java.util.ArrayList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimeLineFragment extends Fragment {
    RecyclerView recyclerView;
    ReviewAdapter reviewAdapter;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("SKKU");
    public static TimeLineFragment mContext;
    String restaurantName = ((RestaurantActivity)RestaurantActivity.mContext).name;
    Query query = reference.child("Review").child(restaurantName).limitToLast(10);
    Query query2;

    public TimeLineFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //타임라인 프래그먼트가 선택되었으니까 아이콘 바꾸기
        //((HomeActivity) HomeActivity.mContext).home.setImageResource(R.drawable.home_selected);
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_time_line, container, false);
        mContext = this;
        reference.addValueEventListener(dataListener);
        reviewAdapter = new ReviewAdapter(getContext());
        recyclerView = rootView.findViewById(R.id.timeline) ;
        LinearLayoutManager manager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,true);
        manager.setStackFromEnd(true);
        manager.setItemPrefetchEnabled(true);  // 리사이클러뷰 정보 미리 불러오기
        recyclerView.setLayoutManager(manager);
        return rootView;
    }

    //최초에 리뷰 10개 불러옴
    ValueEventListener dataListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            //디비 안의 내용이 바뀔때마다 실행되는 구조임, 최초에만 실행되게 변경
            if(!reviewAdapter.items.isEmpty()) {
                return;
            }
            //먼저 유저 정보를 dataSnapshot2에 담음
            final DataSnapshot dataSnapshot2 = dataSnapshot.child("user");
            //쿼리문: status에서 최근 10개 게시물을 불러옴
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //최근 10개 게시물에 대해 반복
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.getKey().equals("num_of_reviews")) {
                            continue;
                        }

                        //게시물에 필요한 변수
                        String id;
                        String date;
                        int like;
                        String picture;
                        String text;
                        String user_num;
                        String nickname;

                        //게시물의 키값
                        String DB_key = snapshot.getKey();

                        //게시물에 대한 정보: message_post
                        //id, date, like, picture, restaurant, text, user_num
                        Map<String, Object> message_post = (Map<String, Object>) snapshot.getValue();
                        id = (String) message_post.get("id");
                        date = (String) message_post.get("date");
                        like = Integer.parseInt(message_post.get("like").toString());
                        picture = (String) message_post.get("picture");
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
                            Review noPhoto = new Review();
                            noPhoto.setNoPhoto(id, nickname, date, text, 1, "레벨 " + level, like, DB_key);
                            reviewAdapter.addItem(noPhoto);
                        } else {
                            Review review = new Review();
                            review.setReview(id, nickname, date, text, 0, "레벨 " + level, like, DB_key, photo, 1);
                            reviewAdapter.addItem(review);
                        }
                    }
                    //어댑터에 반영
                    recyclerView.setAdapter(reviewAdapter);
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

    //포스트를 추가했을때 DB에서 정보를 가져오는 리스너
    ValueEventListener dataListener3 = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            //먼저 유저 정보를 dataSnapshot2에 담음
            final DataSnapshot dataSnapshot2 = dataSnapshot.child("user");

            query2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Long lastDBNum = Long.parseLong(reviewAdapter.getLast_DB_num());
                    Log.d("debug", "lastDBNum: " + lastDBNum);
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if(dataSnapshot1.getKey().equals("num_of_reviews")) {
                            continue;
                        }
                        if(Long.parseLong(dataSnapshot1.getKey()) > lastDBNum) {
                            String DB_key = dataSnapshot1.getKey();

                            //게시물에 대한 정보: message_post
                            //id, date, like, picture, restaurant, text, user_num
                            Map<String, Object> message_post = (Map<String, Object>) dataSnapshot1.getValue();
                            String id = (String) message_post.get("id");
                            String date = (String) message_post.get("date");
                            int like = Integer.parseInt(message_post.get("like").toString());
                            String picture = (String) message_post.get("picture");
                            String restaurant = (String) message_post.get("restaurant");
                            String text = (String) message_post.get("text");
                            String user_num = message_post.get("user_num").toString();
                            //사진이 여러개일 때를 대비하여 ArrayList 선언
                            ArrayList<String> photo = new ArrayList<>();
                            photo.add(picture);

                            //게시물을 작성한 유저에 대한 정보: message_user
                            //nickname, level
                            //현재 user_num으로 찾음
                            //(주: 굳이 user_num으로 찾을 필요가 없음, id는 유니크하기 때문에 구조를 바꿀 필요가 있을듯)
                            Map<String, Object> message_user = (Map<String, Object>) dataSnapshot2.child(user_num).getValue();
                            String nickname = (String) message_user.get("nickname");
                            int level = Integer.parseInt(message_user.get("level").toString());

                            //받아온 정보를 바탕으로 새로운 Post를 만든다
                            if (picture.equals("NO")) {
                                Review noPhoto = new Review();
                                noPhoto.setNoPhoto(id, nickname, date, text, 1, "레벨 " + level, like, DB_key);
                                reviewAdapter.addItem(noPhoto);
                            } else {
                                Review review = new Review();
                                review.setReview(id, nickname, date, text, 0, "레벨 " + level, like, DB_key, photo, 1);
                                reviewAdapter.addItem(review);
                            }
                            reviewAdapter.notifyItemInserted(reviewAdapter.items.size());
                        }
                    }
                    recyclerView.scrollToPosition(reviewAdapter.getItemCount()-1); //자동스크롤
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

    public void reviewAdded() {
        Log.d("debug", "테스트");
        query2 = reference.child("Review").child(restaurantName).limitToLast(10);
        reference.addListenerForSingleValueEvent(dataListener3);
    }
}
