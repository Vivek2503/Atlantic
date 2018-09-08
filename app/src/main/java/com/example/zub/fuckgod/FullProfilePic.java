package com.example.zub.fuckgod;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class FullProfilePic extends AppCompatActivity {


    private CircleImageView profilePic;


    private Button removeDp;

    public DatabaseReference mdb;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_profile_pic);



        profilePic = (CircleImageView)findViewById(R.id.profpic);

        removeDp = (Button) findViewById(R.id.removeDP);

        mdb = FirebaseDatabase.getInstance().getReference("User_profilePic");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        ValueEventListener ref = FirebaseDatabase.getInstance().getReference().child("User_profilePic").child(userId).child("profilepic")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String ppc = dataSnapshot.getValue(String.class);
                        // Toast.makeText(getActivity(), check, Toast.LENGTH_SHORT).show();

                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.defimg);
                        try {

                            Glide.with(FullProfilePic.this).setDefaultRequestOptions(placeholderRequest).load(ppc).into(profilePic);

                        }catch(Exception e){}
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });





        removeDp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                PassProfilePic ppc =new PassProfilePic("https://firebasestorage.googleapis.com/v0/b/eauthor-98c9d.appspot.com/o/profile_pics%2F4QOLzg0xcuZ021fuqBJss3FKqG23.jpg?alt=media&token=17ca4735-4b6b-4e2d-bdb6-005593be7e68");
                mdb.child(userId).setValue(ppc).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Intent i = new Intent(FullProfilePic.this, Home.class);



                        startActivity(i);

                    }
                });



            }
        });



    }

    public void action_edit(View V){


        Intent i = new Intent(FullProfilePic.this, UploadImage.class);

        startActivity(i);

        finish();


    }


}
