package com.example.zub.fuckgod;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostFragment extends Fragment {


    String time;

    String title, context;

    TextView  skip;

    Button post;

    boolean background= false;

    boolean pictureSelectorSkip = false;

    private DatabaseReference databaseReference;
    private DatabaseReference mDatabaseUser;
    private  DatabaseReference mUserDB;


    public ProfileFragment profileFragment;

    public PostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle("Post");

        title = getArguments().getString("extra");

        context = getArguments().getString("extra2");


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post, container, false);
    }




    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        post = (Button) getView().findViewById(R.id.postit);

        skip = (TextView) getView().findViewById(R.id.skip);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("BlogPosts");

        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("User_Info").child(userId);

        mUserDB = FirebaseDatabase.getInstance().getReference().child("UserPosts").child(userId);


        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                background = true;

                View newView = getView().findViewById(R.id.relativeLayout2);

                ViewGroup parent = (ViewGroup) newView.getParent();

                parent.removeView(newView);

                pictureSelectorSkip = true;


            }
        });



        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                post.setClickable(false);

                if (pictureSelectorSkip == false) {
                    Toast.makeText(getActivity(), "Use an image or skip for now", Toast.LENGTH_SHORT).show();
                }


                else{
                DatabaseReference unique = databaseReference.push();

                DatabaseReference newPost = mUserDB.push();

                String uniqueId = unique.toString();
                String newPostId = newPost.toString();




                profileFragment = new ProfileFragment();


                mDatabaseUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String name = dataSnapshot.child("firstname").getValue().toString() + " " + dataSnapshot.child("lastname").getValue().toString();



                        unique.child("name").setValue(name).addOnCompleteListener(
                                new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {

                                            unique.child("title").setValue(title);
                                            unique.child("Context").setValue(context);
                                            unique.child("UserId").setValue(userId);

                                            unique.child("ProfileId").setValue(newPostId.replace("https://eauthor-98c9d.firebaseio.com/UserPosts/"+ userId + "/", ""));

                                            time =GetUnixTime();

                                            unique.child("Usertime").setValue(time);

                                            if (background == true) {
                                                unique.child("defaultImage").setValue("true");
                                            } else {
                                                unique.child("defaultImage").setValue("false");
                                            }



                                        }


                                    }
                                }
                        );


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });





                mDatabaseUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                        String name = dataSnapshot.child("firstname").getValue().toString() + " " + dataSnapshot.child("lastname").getValue().toString();

                        newPost.child("name").setValue(name).addOnCompleteListener(
                                new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {

                                            newPost.child("title").setValue(title);
                                            newPost.child("Context").setValue(context);
                                            newPost.child("UserId").setValue(userId);
                                            newPost.child("ProfileId").setValue(newPostId.replace("https://eauthor-98c9d.firebaseio.com/UserPosts/"+ userId + "/", ""));
                                            newPost.child("HomeId").setValue(uniqueId.replace("https://eauthor-98c9d.firebaseio.com/BlogPosts/", ""));

                                            newPost.child("Usertime").setValue(time);

                                            if (background == true) {
                                                newPost.child("defaultImage").setValue("true");
                                            } else {
                                                newPost.child("defaultImage").setValue("false");
                                            }

                                            try {
                                                newPost.child("hidden").setValue("false");
                                            }catch(Exception e){}

                                            setFragment(profileFragment);

                                        }


                                    }
                                }
                        );


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }


            }
        });






    }

    private String GetUnixTime() {

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("dd-MM-yyy HH:mm:ss z");
        date.setTimeZone(TimeZone.getTimeZone("GMT"));
        String localTime = date.format(currentLocalTime);

        return (localTime);

    }


    private void setFragment(Fragment fragment) {
        android.support.v4.app.FragmentTransaction fragmentTransaction =getFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container, fragment).commit();
    }



}
