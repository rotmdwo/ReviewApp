package org.techtown.reviewapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.techtown.reviewapp.Login_SignUp.LoginActivity;
import org.techtown.reviewapp.home.HomeActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!restoreState().equals("")){ //로그인 된 아이디가 있으면
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else{  //로그인 된 아이디가 없으면
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        finish();
    }

    public String restoreState(){  //로그인 된 아이디 복원
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        if((pref!=null)&&(pref.contains("id"))){
            String id = pref.getString("id","");
            return id;
        }
        else return "";
    }
}
