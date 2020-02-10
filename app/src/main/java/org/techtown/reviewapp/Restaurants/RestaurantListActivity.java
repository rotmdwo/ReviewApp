package org.techtown.reviewapp.Restaurants;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.techtown.reviewapp.R;
import org.techtown.reviewapp.UserRank.UserRank;
import org.techtown.reviewapp.UserRank.UserRankAdapter;
import org.techtown.reviewapp.post.PostAdapter;

import java.util.Arrays;
import java.util.Map;

public class RestaurantListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RestaurantAdapter adapter;
    public static Context mContext;
    Intent intent;
    TextView textView2;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("SKKU");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);
        mContext = this;

        TextView textView = findViewById(R.id.textView);
        intent = getIntent();
        textView.setText(intent.getStringExtra("category_kor"));
        textView2 = findViewById(R.id.textView2);

        ImageView imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.anim_not_move,R.anim.anim_slide_out_right);
            }
        });

        adapter = new RestaurantAdapter(this);  // 생성자를 만들 때 Context를 안 넘겨주면 프래그먼트 나갔다가 들어올 때 마다 리사이클러뷰에 중복으로 쌓이는 버그 생김
        recyclerView = findViewById(R.id.recyclerView) ;
        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        manager.setItemPrefetchEnabled(true);
        recyclerView.setLayoutManager(manager) ;
        reference.addListenerForSingleValueEvent(dataListener);
    }

    ValueEventListener dataListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Map<String, Object> message_SKKU = (Map<String, Object>) dataSnapshot.getValue();
            Map<String, Object> message_Restaurants = (Map<String, Object>) message_SKKU.get("Restaurants");
            Map<String, Object> message_Category = (Map<String, Object>) message_Restaurants.get(intent.getStringExtra("category_eng"));
            int restaurants_num = Integer.parseInt(message_Category.get("num").toString());
            textView2.setText(restaurants_num+"개의 검색결과");

            Restaurant[] array = new Restaurant[restaurants_num];

            for(int i=1 ; i <=restaurants_num ; i++){
                Map<String, Object> message_i = (Map<String, Object>) message_Category.get(Integer.toString(i));
                array[i-1] = new Restaurant((String)message_i.get("name"),(String)message_i.get("picture"),Float.parseFloat(message_i.get("rating").toString()),Integer.parseInt(message_i.get("num_of_reviews").toString()));

            }

            Arrays.sort(array);

            for(int i=0;i<restaurants_num;i++){
                adapter.addRestaurant(array[i]);
            }

            recyclerView.setAdapter(adapter);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
}
