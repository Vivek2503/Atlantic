package com.example.zub.fuckgod;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registration extends AppCompatActivity implements View.OnClickListener{

    public  EditText first;
    public  EditText last;
    public  EditText email;
    public  EditText pass1;
    public  EditText pass2;


    ImageView eye_open1, eye_closed1,eye_open2, eye_closed2 ;
    TextView text;


    public FirebaseAuth firebaseAuth;
    public DatabaseReference databaseReference;

    public DatabaseReference mdb;

    public void onBackPressed(){
        Intent i = new Intent(Registration.this, LoginActivity.class);

        startActivity(i);
        finish();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("User_Info");
        mdb = FirebaseDatabase.getInstance().getReference("User_profilePic");

        text = findViewById(R.id.textviewReg);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/courgette.ttf");

        //post_title.setTypeface(typeface);

        text.setTypeface(typeface);


        eye_open1 = (ImageView) findViewById(R.id.eye_open1);
        eye_closed1 = (ImageView)findViewById(R.id.eye_closed1);

        eye_open2 = (ImageView) findViewById(R.id.eye_open2);
        eye_closed2 = (ImageView)findViewById(R.id.eye_closed2);


        first = (EditText) findViewById(R.id.editText7);
        last = (EditText) findViewById(R.id.editText9);
        email = (EditText) findViewById(R.id.editText10);
        pass1 = (EditText) findViewById(R.id.editText11);
        pass2 = (EditText) findViewById(R.id.editText12);


        eye_open1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                pass1.setTransformationMethod(null);
                eye_closed1.setVisibility(View.VISIBLE);
                eye_open1.setVisibility(View.INVISIBLE);
                eye_open1.setClickable(false);
                eye_closed1.setClickable(true);

            }
        });


        eye_closed1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                pass1.setTransformationMethod(new PasswordTransformationMethod());
                eye_open1.setVisibility(View.VISIBLE);
                eye_closed1.setVisibility(View.INVISIBLE);
                eye_open1.setClickable(true);
                eye_closed1.setClickable(false );

            }
        });



        eye_open2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                pass2.setTransformationMethod(null);
                eye_closed2.setVisibility(View.VISIBLE);
                eye_open2.setVisibility(View.INVISIBLE);
                eye_open2.setClickable(false);
                eye_closed2.setClickable(true);

            }
        });


        eye_closed2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                pass2.setTransformationMethod(new PasswordTransformationMethod());
                eye_open2.setVisibility(View.VISIBLE);
                eye_closed2.setVisibility(View.INVISIBLE);
                eye_open2.setClickable(true);
                eye_closed2.setClickable(false );

            }
        });

    }







    public void action_btn(View v) {

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";





        String firststr = first.getText().toString();
        String laststr = last.getText().toString();
        String emailstr = email.getText().toString();
        String pass1str = pass1.getText().toString();
        String pass2str = pass2.getText().toString();

        if (firststr.isEmpty()) {

            first.setError("Enter first name");
            //Toast.makeText(Registration.this, "please enter the first name correctly", Toast.LENGTH_LONG).show();
            //first.requestFocus();

        }
        else if(laststr.isEmpty()){
            last.setError("Enter last name");
        }
        else if(emailstr.equals(null)){
            email.setError("Enter email address");
        }
        else if(!emailstr.matches(emailPattern))
        {
            email.setError("Enter a valid email address");
        }
        else if(pass1str.isEmpty()){
            pass1.setError("Enter password");
        }
        else if(pass1str.length()<4)
        {
            pass1.setError("Password is short. Password must be of at least 8 characters.");
        }

        else if(pass2str.isEmpty()){
            pass2.setError("Passwords do not match");
        }
        else if(!pass1str.equals(pass2str))
        {
            pass2.setError("passwords do not match");
        }

        else {


            LottieAnimationView lottieAnimationView = findViewById(R.id.av_loader);
            lottieAnimationView.setAnimation("progress_bar.json");
            lottieAnimationView.setVisibility(View.VISIBLE);

            CardView cardView = findViewById(R.id.card);
            cardView.setVisibility(View.VISIBLE);

            firebaseAuth.createUserWithEmailAndPassword(emailstr,pass1str)
                    .addOnCompleteListener((task) ->
                    {
                        final FirebaseUser user = firebaseAuth.getCurrentUser();
                        if(task.isSuccessful())
                        {
                            lottieAnimationView.setVisibility(View.INVISIBLE);
                            cardView.setVisibility(View.INVISIBLE);

                            Toast.makeText(Registration.this, "Registration Successful!", Toast.LENGTH_SHORT).show();

                            user.sendEmailVerification();

                            //firebase Realtime Database

                            String id = databaseReference.push().getKey();

                            FirebaseUser userM = FirebaseAuth.getInstance().getCurrentUser();
                            String userId = userM.getUid();

                            String name = firststr + " " + laststr;

                            name = name.toLowerCase();


                            PassInfos passInfos = new PassInfos(id, firststr,laststr,emailstr,pass1str, name,userId);
                            databaseReference.child(userId).setValue(passInfos);

                            PassProfilePic ppc =new PassProfilePic("https://firebasestorage.googleapis.com/v0/b/eauthor-98c9d.appspot.com/o/profile_pics%2F4QOLzg0xcuZ021fuqBJss3FKqG23.jpg?alt=media&token=17ca4735-4b6b-4e2d-bdb6-005593be7e68");
                            mdb.child(userId).setValue(ppc);


                            Intent i = new Intent(Registration.this, LoginActivity.class);

                            i.putExtra("extra", emailstr);
                            i.putExtra("extra2",pass1str);


                            startActivity(i);
                        }

                        else if(task.getException() instanceof FirebaseAuthUserCollisionException)
                        {
                            lottieAnimationView.setVisibility(View.INVISIBLE);
                            cardView.setVisibility(View.INVISIBLE);

                            Toast.makeText(Registration.this, "User Exists. Try logging in.", Toast.LENGTH_SHORT).show();
                        }

                        else
                        {


                            lottieAnimationView.setVisibility(View.INVISIBLE);
                            cardView.setVisibility(View.INVISIBLE);

                            Toast.makeText(Registration.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    });



        }
    }


    @Override
    public void onClick(View v) {

    }
}
