package com.example.zub.fuckgod;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserActivityFragment extends Fragment {

    String id;

    private DatabaseReference mDatabase;

    private RecyclerView mBlogList;

    private LinearLayoutManager mLayoutManager;

    private TextView username;
    String firstname, lastname;
    private  CircleImageView prof;

    DatabaseReference databaseReference;

    public boolean mProcessLike =false;

    DatabaseReference likedata;

    Query firebaseSearchQuery;

    public UserActivityFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle(" ");

        id = getArguments().getString("extra");

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_activity, container, false);
    }




    @Override
    public void onStart() {
        super.onStart();

        firebaseSearchQuery = FirebaseDatabase.getInstance().getReference()
                .child("UserPosts").child(id).orderByChild("hidden").equalTo("false");


        FirebaseRecyclerAdapter<Blog, UserActivityFragment.BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, UserActivityFragment.BlogViewHolder>(

                Blog.class,
                R.layout.blog_row,
                UserActivityFragment.BlogViewHolder.class,
                firebaseSearchQuery


        ) {
            @Override
            protected void populateViewHolder(UserActivityFragment.BlogViewHolder viewHolder, Blog model, int position) {




                String post_key = getRef(position).getKey();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String userId = user.getUid();

               // firebaseSearchQuery = mDatabase.child(post_key).child("hidden").equalTo("false");

                viewHolder.setTitle(model.getTitle());
                viewHolder.setContext(model.getContext());
                viewHolder.setName(model.getName());
                viewHolder.setUserId(model.getUserId());
                try {
                    viewHolder.setUsertime(model.getUsertime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                DatabaseReference likes = FirebaseDatabase.getInstance().getReference().child("Likes").child(model.getProfileId());

                viewHolder.lottie.playAnimation();

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        SinglePostFragment spf = new SinglePostFragment ();
                        Bundle args = new Bundle();
                        args.putString("extra", post_key);
                        args.putString("extra2", model.getUserId());
                        spf.setArguments(args);

//Inflate the fragment
                        android.support.v4.app.FragmentTransaction fragmentTransaction =getFragmentManager().beginTransaction();

                        fragmentTransaction.replace(R.id.fragment_container, spf).commit();




                    }
                });


                //tools work;;;;;





                viewHolder.name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String id = model.getUserId();

                        UserActivityFragment uaf = new UserActivityFragment();
                        Bundle args = new Bundle();

                        args.putString("extra", id);
                        uaf.setArguments(args);

//Inflate the fragment
                        android.support.v4.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

                        fragmentTransaction.replace(R.id.fragment_container, uaf).commit();


                    }
                });



                viewHolder.rate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Liking(model.getProfileId(), viewHolder);


                    }
                });

                //number of likes....

                likes.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        long countLikes = dataSnapshot.getChildrenCount();
                        viewHolder.rateDetails.setText((int) countLikes + " "+ "Starred");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                likes.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(userId))
                            viewHolder.rate.setImageResource(R.drawable.icons8_star_rated);
                        else
                            viewHolder.rate.setImageResource(R.drawable.icons8_star);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                viewHolder.rateDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        LikeDetails ldt = new LikeDetails();
                        Bundle args = new Bundle();

                        args.putString("extra", model.getProfileId());
                        ldt.setArguments(args);


                        android.support.v4.app.FragmentTransaction fragmentTransaction =getFragmentManager().beginTransaction();

                        fragmentTransaction.replace(R.id.fragment_container, ldt).commit();

                    }
                });





                viewHolder.tools.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        showPopupMenu(viewHolder.tools,position);

                    }
                });



            }
        }
                ;


        mBlogList.setAdapter(firebaseRecyclerAdapter);





    }



    private void Liking(String ProfileId, BlogViewHolder viewHolder) {

        likedata = FirebaseDatabase.getInstance().getReference()
                .child("Likes").child(ProfileId);

        mProcessLike =true;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        // Toast.makeText(Home.this, "User Exists. Try logging in.", Toast.LENGTH_SHORT).show();



        likedata.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (mProcessLike) {
                            if (dataSnapshot.hasChild(userId)) {
                                likedata.child(userId).removeValue();
                                viewHolder.rate.setImageResource(R.drawable.icons8_star);
                                mProcessLike = false;
                            } else {
                                FirebaseDatabase.getInstance().getReference()
                                        .child("Likes").child(ProfileId).child(userId).child("id").setValue(userId);

                                viewHolder.rate.setImageResource(R.drawable.icons8_star_rated);
                                mProcessLike = false;
                            }

                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );



        return;



    }





    private void showPopupMenu(View view, int position) {


        PopupMenu popupMenu = new PopupMenu(view.getContext(),view);

        MenuInflater inflater = popupMenu.getMenuInflater();

        inflater.inflate(R.menu.popup_menu_user, popupMenu.getMenu());


        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.Report:

                        Toast.makeText(getActivity(), "Report Post", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.Unfollow:

                        Toast.makeText(getActivity(), "Unfollow User", Toast.LENGTH_SHORT).show();
                        return true;

                    default:

                        return false;
                }
            }
        });

        popupMenu.show();

    }


    public static class BlogViewHolder extends RecyclerView.ViewHolder{

        View mView;

        ImageButton tools, rate;

        TextView name, rateDetails;
        LottieAnimationView lottie;


        public BlogViewHolder(View itemView) {
            super(itemView);

            mView = itemView ;

            tools =(ImageButton) mView.findViewById(R.id.tools);
            rateDetails = (TextView) mView.findViewById(R.id.rateDetails);
            name = (TextView) mView.findViewById(R.id.post_username);
            rate =(ImageButton) mView.findViewById(R.id.Rate);


            lottie = mView.findViewById(R.id.profile_round);
            //lottieAnimationView.setAnimation("star.json");
            lottie.setAnimation("profile_round.json");


        }


        public void setTitle(String title)
        {
            TextView post_title = (TextView) mView.findViewById(R.id.post_title);
            post_title.setText(title);

        }


        public void setContext(String context){

            TextView post_context = (TextView) mView.findViewById(R.id.post_desc);
            post_context.setText(context);

        }

        public void setName(String name)
        {
            TextView post_username = (TextView) mView.findViewById(R.id.post_username);
            post_username.setText(name);


        }

        public void setUsertime(String usertime) throws ParseException {

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyy HH:mm:ss z");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            long times = sdf.parse(usertime).getTime();
            long now = System.currentTimeMillis();

            CharSequence ago =
                    DateUtils.getRelativeTimeSpanString(times, now, DateUtils.MINUTE_IN_MILLIS);

            TextView time = (TextView) mView.findViewById(R.id.usertime);

            Typeface typeface = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/tnr.ttf");

            //post_title.setTypeface(typeface);

            time.setTypeface(typeface);
            if(ago.equals("0 minutes ago"))
                ago = "just now";


            time.setText(ago);
        }


        public void setUserId(String UserId)
        {
            CircleImageView profilepic = (CircleImageView) mView.findViewById((R.id.profilepicBlog));



            ValueEventListener ref = FirebaseDatabase.getInstance().getReference().child("User_profilePic").child(UserId).child("profilepic")

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


        }


    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {


        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mBlogList = (RecyclerView) getView().findViewById(R.id.User_blog_list) ;
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(mLayoutManager);

        mDatabase =FirebaseDatabase.getInstance().getReference().child("UserPosts").child(id);


        username = (TextView) getView().findViewById(R.id.name);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("User_Info").child(id);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                firstname=dataSnapshot.child("firstname").getValue().toString();
                lastname=dataSnapshot.child("lastname").getValue().toString();

                Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/segoeprint.ttf");



                username.setTypeface(typeface);

                username.setText(firstname+ " "+ lastname);

                getActivity().setTitle(firstname+ " "+ lastname);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        prof = (CircleImageView) getView().findViewById(R.id.pic);

        ValueEventListener ref = FirebaseDatabase.getInstance().getReference().child("User_profilePic").child(id).child("profilepic")

                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String ppc = dataSnapshot.getValue(String.class);
                        // Toast.makeText(getActivity(), check, Toast.LENGTH_SHORT).show();


                        Glide.with(getActivity()).load(ppc).into(prof);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        prof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), UserDP.class);

                i.putExtra("extra", id);


                startActivity(i);

            }
        });




    }








}
