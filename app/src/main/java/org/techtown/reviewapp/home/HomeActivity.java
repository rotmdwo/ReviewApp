package org.techtown.reviewapp.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import org.techtown.reviewapp.R;

public class HomeActivity extends AppCompatActivity implements AutoPermissionsListener {
    ImageButton home, restaurant, rank, settings;
    HomeFragment frg_home;
    UserRankFragment frg_rank;
    FragmentManager manager;
    public static Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        AutoPermissions.Companion.loadAllPermissions(this,102);

        manager= getSupportFragmentManager();
        mContext = this;

        home = findViewById(R.id.home);
        restaurant = findViewById(R.id.restaurant);
        rank = findViewById(R.id.userRank);
        settings = findViewById(R.id.setting);

        frg_home = new HomeFragment();
        frg_rank = new UserRankFragment();
        manager.beginTransaction().add(R.id.frameLayout,frg_home).add(R.id.frameLayout,frg_rank).hide(frg_rank).commit();


        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //manager.beginTransaction().replace(R.id.frameLayout,frg_home).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                manager.beginTransaction().hide(frg_rank).show(frg_home).commit();
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
                //manager.beginTransaction().replace(R.id.frameLayout,frg_rank).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                manager.beginTransaction().hide(frg_home).show(frg_rank).commit();
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        AutoPermissions.Companion.parsePermissions(this,requestCode,permissions,this);
    }
    @Override
    public void onDenied(int i, String[] strings) {
    }

    @Override
    public void onGranted(int i, String[] strings) {

    }
}
