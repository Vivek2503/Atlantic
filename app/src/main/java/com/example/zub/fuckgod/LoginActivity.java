package com.example.zub.fuckgod;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.signin.SignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class LoginActivity extends AppCompatActivity implements TextWatcher, CompoundButton.OnCheckedChangeListener{


    public EditText email;
    public  EditText pass;

    CheckBox remMe;
    TextView text;


    ImageView eye_open, eye_closed;
    FirebaseAuth mAuth;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;



    public static final String PREF_NAME = "prefs";
    public static final String KEY_REMEMBER = "remember";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASS = "pass";

    public void onBackPressed(){

        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        remMe = (CheckBox) findViewById(R.id.remMe);

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();



        mAuth = FirebaseAuth.getInstance();


        text = findViewById(R.id.textviewlogin);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/courgette.ttf");

        //post_title.setTypeface(typeface);

        text.setTypeface(typeface);



        String extra =getIntent().getStringExtra("extra");
        String extra2 =getIntent().getStringExtra("extra2");



        email = (EditText) findViewById(R.id.edittextUsername);
        pass = (EditText) findViewById(R.id.edittextPassword);

        email.setText(extra);
        pass.setText(extra2);


         //   email.setText(sharedPreferences.getString(KEY_USERNAME, ""));

         //   pass.setText(sharedPreferences.getString(KEY_PASS, ""));

        if(sharedPreferences.getBoolean(KEY_REMEMBER, false))
            remMe.setChecked(true);
        else
            remMe.setChecked(false);





        email.addTextChangedListener(this);
        pass.addTextChangedListener(this);
        remMe.setOnCheckedChangeListener(this);




        eye_open = (ImageView) findViewById(R.id.eye_open);
        eye_closed = (ImageView)findViewById(R.id.eye_closed);


        eye_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                pass.setTransformationMethod(null);
                eye_closed.setVisibility(View.VISIBLE);
                eye_open.setVisibility(View.INVISIBLE);
                eye_open.setClickable(false);
                eye_closed.setClickable(true);

            }
        });


        eye_closed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                pass.setTransformationMethod(new PasswordTransformationMethod());
                eye_open.setVisibility(View.VISIBLE);
                eye_closed.setVisibility(View.INVISIBLE);
                eye_open.setClickable(true);
                eye_closed.setClickable(false );

            }
        });


    }




    public void to_registration_btn(View v)
    {

        Intent i = new Intent(LoginActivity.this, Registration.class);
        startActivity(i);
    }


    public void to_home_btn(View v)
    {
        email = (EditText) findViewById(R.id.edittextUsername);
        pass = (EditText) findViewById(R.id.edittextPassword);



        String emailstr = email.getText().toString();
        String pass1str = pass.getText().toString();



        if(emailstr.isEmpty()){
            email.setError("Enter email address");
        }
        else if(pass1str.isEmpty()){
            pass.setError("Enter password");
        }

        else {

            LottieAnimationView lottieAnimationView = findViewById(R.id.av_loader);
            lottieAnimationView.setAnimation("loading.json");
            lottieAnimationView.setVisibility(View.VISIBLE);

            CardView cardView = findViewById(R.id.card);
            cardView.setVisibility(View.VISIBLE);
            //sign in...

            mAuth.signInWithEmailAndPassword(emailstr,pass1str).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    FirebaseUser user = mAuth.getCurrentUser();

                    if(task.isSuccessful())
                    {
                        lottieAnimationView.setVisibility(View.INVISIBLE);
                        cardView.setVisibility(View.INVISIBLE);


                        if(user.isEmailVerified()) {
                            Intent i = new Intent(LoginActivity.this, Home.class);
                            startActivity(i);
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, "Verification of email is incomplete.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    else
                    {
                        lottieAnimationView.setVisibility(View.INVISIBLE);
                        cardView.setVisibility(View.INVISIBLE);
                        Toast.makeText(LoginActivity.this, "Email and Password doesn't match. You sure you logged in?", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }
    }

    public void to_forgot_password_btn(View view) {
        Intent i = new Intent(LoginActivity.this, Forgot_password.class);
        startActivity(i);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        managePrefs();


    }


    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        managePrefs();

    }


    private void managePrefs() {

        if(remMe.isChecked()){

            editor.putString(KEY_USERNAME, email.getText().toString().trim());
            editor.putString(KEY_PASS, pass.getText().toString().trim());


            editor.putBoolean(KEY_REMEMBER, true);
            editor.apply();

        }
        else
        {


            editor.putBoolean(KEY_REMEMBER,false);
            editor.putString(KEY_USERNAME, "dffrf");
            editor.putString(KEY_PASS, "fefew");
            editor.apply();
        }



    }



}
