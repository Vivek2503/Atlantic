package com.example.zub.fuckgod;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LottieAnimation extends AppCompatActivity {

    private static int SPLASH_TIME_OUT ;


    String email ="nothing" , pass ="nothing";
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SPLASH_TIME_OUT= ((int) (Math.random()) %4000) + 3000;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottie_animation);





        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {


              SharedPreferences sp = getSharedPreferences("prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();



                try {
                    email = sp.getString("username", "");
                    pass = sp.getString("pass", "");

                }catch(Exception e){}

                mAuth = FirebaseAuth.getInstance();


                if(email.isEmpty())
                    email ="nothing";

                if(pass.isEmpty())
                    pass = "nothing";


                mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        FirebaseUser user = mAuth.getCurrentUser();
                        if(task.isSuccessful())
                        {

                            if(user.isEmailVerified())
                            {
                                Toast.makeText(LottieAnimation.this,"Logs in for " + email, Toast.LENGTH_SHORT).show();

                                Intent i = new Intent(LottieAnimation.this, Home.class);
                                startActivity(i);
                            }
                            else
                            {
                                Intent loginIntent = new Intent(LottieAnimation.this, LoginActivity.class);

                                startActivity(loginIntent);
                                finish();
                            }

                        }
                        else
                        {
                            Intent loginIntent = new Intent(LottieAnimation.this, LoginActivity.class);

                            startActivity(loginIntent);
                            finish();
                        }


                    }
                });





            }
        }, SPLASH_TIME_OUT);

    }

    public void onBackPressed(){

        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }




}
