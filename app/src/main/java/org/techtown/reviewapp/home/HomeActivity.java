package org.techtown.reviewapp.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import org.techtown.reviewapp.R;

public class HomeActivity extends AppCompatActivity {
    ImageButton home, restaurant, rank, settings;
    HomeFragment frg_home;
    UserRankFragment frg_rank;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d("asdf","a");
        home = findViewById(R.id.home);
        restaurant = findViewById(R.id.restaurant);
        rank = findViewById(R.id.userRank);
        settings = findViewById(R.id.setting);
        Log.d("asdf","1");
        frg_home = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.frameLayout);Log.d("asdf","2");
        frg_rank = new UserRankFragment();Log.d("asdf","3");

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,frg_home).commit();
            }
        });

        restaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        rank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,frg_rank).commit();
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
