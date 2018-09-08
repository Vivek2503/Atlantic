package com.example.zub.fuckgod;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserDP extends AppCompatActivity {

   ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dp);

        String id =getIntent().getStringExtra("extra");

        imageView = (ImageView) findViewById(R.id.profpicUser);

        ValueEventListener ref = FirebaseDatabase.getInstance().getReference().child("User_profilePic").child(id).child("profilepic")

                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String ppc = dataSnapshot.getValue(String.class);
                        // Toast.makeText(getActivity(), check, Toast.LENGTH_SHORT).show();


                        Glide.with(UserDP.this).load(ppc).into(imageView);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



    }
}
