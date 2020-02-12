package org.techtown.reviewapp.Restaurants;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.techtown.reviewapp.R;
import org.techtown.reviewapp.Restaurants.restaurant.InfoFragment;
import org.techtown.reviewapp.Restaurants.restaurant.TimeLineFragment;

public class RestaurantActivity extends AppCompatActivity {
    FragmentManager manager;
    InfoFragment infoFragment;
    TimeLineFragment timeLineFragment;
    ImageView prev, option;
    TextView time_line, resto_info;
    public static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        manager = getSupportFragmentManager();
        mContext = this;

        prev = findViewById(R.id.prev);
        option = findViewById(R.id.option);
        time_line = findViewById(R.id.time_line);
        resto_info = findViewById(R.id.resto_info);

        infoFragment = new InfoFragment();
        timeLineFragment = new TimeLineFragment();
        manager.beginTransaction()
                .add(R.id.frameLayout, timeLineFragment)
                .add(R.id.frameLayout, infoFragment).hide(infoFragment)
                .commit();

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        time_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manager.beginTransaction().hide(infoFragment).show(timeLineFragment).commit();
            }
        });

        resto_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manager.beginTransaction().hide(timeLineFragment).show(infoFragment).commit();
            }
        });
    }
}
