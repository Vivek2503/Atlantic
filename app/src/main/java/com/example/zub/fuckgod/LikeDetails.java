package com.example.zub.fuckgod;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class LikeDetails extends Fragment {

    private RecyclerView mBlogList;
    String profileid;
    private DatabaseReference mDatabase;

    private LinearLayoutManager mLayoutManager;

    public LikeDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        profileid = getArguments().getString("extra");
        getActivity().setTitle("Starred");

        return inflater.inflate(R.layout.fragment_like_details, container, false);
    }






    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<LikeBlog, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<LikeBlog, BlogViewHolder>(

                LikeBlog.class,
                R.layout.likedetails,
                BlogViewHolder.class,
                mDatabase


        ) {


            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, LikeBlog model, int position) {

                String post_key = model.getId();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String userId = user.getUid();


                viewHolder.setId(model.getId());


                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(post_key.equals(userId))
                        {

                            ProfileFragment prf = new ProfileFragment();

                            Home.mBlogList.setVisibility(View.INVISIBLE);
//Inflate the fragment
                            android.support.v4.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

                            fragmentTransaction.replace(R.id.fragment_container, prf).commit();


                        }
                        else
                        {
                            UserActivityFragment uaf = new UserActivityFragment();
                            Bundle args = new Bundle();

                            args.putString("extra", model.getId());
                            uaf.setArguments(args);

//Inflate the fragment
                            android.support.v4.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

                            fragmentTransaction.replace(R.id.fragment_container, uaf).commit();
                        }

                    }
                });


            }
        }
                ;


        mBlogList.setAdapter(firebaseRecyclerAdapter);






    }


    public static class BlogViewHolder extends RecyclerView.ViewHolder {

        View mView;

        TextView name;

        DatabaseReference userInfo;


        public BlogViewHolder(View itemView) {
            super(itemView);

            mView = itemView;


        }

        public void setId(String id) {

            name = (TextView) mView.findViewById(R.id.likeName);

            Typeface typeface = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/courgette.ttf");

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String userId = user.getUid();

            this.name.setTypeface(typeface);


            CircleImageView profilepic = (CircleImageView) mView.findViewById((R.id.likeDP));

            ValueEventListener ref = FirebaseDatabase.getInstance().getReference().child("User_profilePic").child(id).child("profilepic")

                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            String ppc = dataSnapshot.getValue(String.class);
                            // Toast.makeText(getActivity(), check, Toast.LENGTH_SHORT).show();


                            Glide.with(mView.getContext()).load(ppc).into(profilepic);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


            ValueEventListener ref1 = FirebaseDatabase.getInstance().getReference().child("User_Info").child(id).child("name")

                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            String UserName = dataSnapshot.getValue(String.class);
                            // Toast.makeText(getActivity(), check, Toast.LENGTH_SHORT).show();

                            if(id.equals(userId))
                                UserName = "You";

                            name.setText(UserName);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


        }




    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("Likes").child(profileid);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);


        mBlogList = (RecyclerView) getView().findViewById(R.id.likeRecView) ;
        mBlogList.setHasFixedSize(true);

        mBlogList.setLayoutManager(mLayoutManager);

        mBlogList.setNestedScrollingEnabled(false);



    }





}
