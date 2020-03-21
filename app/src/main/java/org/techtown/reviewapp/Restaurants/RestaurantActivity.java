package org.techtown.reviewapp.Restaurants;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.techtown.reviewapp.R;
import org.techtown.reviewapp.Restaurants.restaurant.InfoFragment;
import org.techtown.reviewapp.Restaurants.restaurant.TimeLineFragment;
import org.techtown.reviewapp.Restaurants.restaurant.WriteActivity;

public class RestaurantActivity extends AppCompatActivity {
    FirebaseStorage storage = FirebaseStorage.getInstance();
    public FragmentManager manager;
    InfoFragment infoFragment;
    TimeLineFragment timeLineFragment;
    ImageView prev, write;
    TextView time_line, resto_info;
    public double latitude;
    public double longitude;
    public static Context mContext;

    TextView restaurant_name, star_rating;
    ImageView restaurant_image;

    public String name, picture;
    Float rating;
    int reviewNum;

    public NestedScrollView nestedScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("name");
        picture = bundle.getString("picture");
        rating = bundle.getFloat("rating");
        reviewNum = bundle.getInt("reviewNum");
        String location = bundle.getString("location");
        latitude = Double.parseDouble(location.substring(0,location.indexOf(",")));
        longitude = Double.parseDouble(location.substring(location.indexOf(" ")+1));

        restaurant_name = findViewById(R.id.restaurant_name);
        star_rating = findViewById(R.id.star_rating);
        restaurant_image = findViewById(R.id.restaurant_image);

        nestedScrollView = findViewById(R.id.nestedScrollView);

        restaurant_name.setText(name);
        star_rating.setText(rating.toString());

        StorageReference ref = storage.getReference().child(picture);
        ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    Glide.with(mContext).load(task.getResult()).into(restaurant_image);
                }
            }
        });

        manager = getSupportFragmentManager();
        mContext = this;

        prev = findViewById(R.id.prev);
        write = findViewById(R.id.write);
        time_line = findViewById(R.id.time_line);
        resto_info = findViewById(R.id.resto_info);

        infoFragment = new InfoFragment(this);
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

        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, WriteActivity.class);
                Bundle bundle = new Bundle();
                bundle.putFloat("rating", rating);
                bundle.putInt("reviewNum", reviewNum);
                startActivityForResult(intent, 0);
                overridePendingTransition(R.anim.anim_slide_in_bottom, R.anim.anim_slide_in_bottom);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 1) {
            timeLineFragment.reviewAdded();
        }
    }
}
