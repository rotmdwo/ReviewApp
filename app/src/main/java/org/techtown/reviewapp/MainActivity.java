package org.techtown.reviewapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import org.techtown.reviewapp.comment.Comment;
import org.techtown.reviewapp.home.HomeActivity;
import org.techtown.reviewapp.review.Review;
import org.techtown.reviewapp.review.ReviewAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        saveState("rotmdwo",1);
        Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
        startActivity(intent);
    }

    public void saveState(String id, int user_num){
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("id",id);
        editor.putInt("user_num", user_num);
        editor.commit();
    }
}
