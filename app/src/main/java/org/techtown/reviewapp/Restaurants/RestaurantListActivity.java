package org.techtown.reviewapp.Restaurants;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.techtown.reviewapp.R;
import org.techtown.reviewapp.post.PostAdapter;

public class RestaurantListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RestaurantAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);

        TextView textView = findViewById(R.id.textView);
        Intent intent = getIntent();
        textView.setText(intent.getStringExtra("category"));

        ImageView imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.anim_not_move,R.anim.anim_slide_out_right);
            }
        });
    }
}
