package com.example.zub.fuckgod;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public static final String PREF_NAME = "prefs";
    public static final String KEY_REMEMBER = "remember";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASS = "pass";


    private static int SPLASH_TIME_OUT =2500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();

                Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                        .getBoolean("isFirstRun", true);

                if(isFirstRun){

                     editor.putString(KEY_USERNAME, "nothing");
                     editor.putString(KEY_PASS, "nothing");
                     editor.putBoolean(KEY_REMEMBER, true);
                     editor.apply();
                }

                getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                        .putBoolean("isFirstRun", false).commit();


                    Intent loginIntent = new Intent(MainActivity.this, LottieAnimation.class);

                    startActivity(loginIntent);
                    finish();

            }
        }, SPLASH_TIME_OUT);

    }




}
