package com.example.zub.fuckgod;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawer;

    public FirebaseAuth firebaseAuth;




    public ImageView searchview;

    private DatabaseReference mDatabase;


    public static RecyclerView mBlogList;


    private LinearLayoutManager mLayoutManager;


    DatabaseReference likedata;

    public boolean mProcessLike =false;


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Blog, Home.BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, Home.BlogViewHolder>(

                Blog.class,
                R.layout.blog_row,
                Home.BlogViewHolder.class,
                mDatabase


        ) {


            @Override
            protected void populateViewHolder(Home.BlogViewHolder viewHolder, Blog model, int position) {

                String post_key = getRef(position).getKey();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String userId = user.getUid();



                viewHolder.setTitle(model.getTitle());
                viewHolder.setContext(model.getContext());
                viewHolder.setName(model.getName());
                viewHolder.setUserId(model.getUserId());
                viewHolder.setDefaultImage(model.getDefaultImage());
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


                        mBlogList.setVisibility(View.INVISIBLE);

                        Bundle bundle = new Bundle();

                        bundle.putString("extra", post_key);

                        bundle.putString("extra2","home");

                        bundle.putString("extra3", model.getProfileId());

                        SinglePostFragment spf = new SinglePostFragment();

                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                spf).commit();

                        spf.setArguments(bundle);



                    }
                });


                viewHolder.tools.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                       // Toast.makeText(Home.this, " Successful!", Toast.LENGTH_SHORT).show();

                        showPopupMenu(viewHolder.tools,position,model.getUserId().toString());


                    }
                });



                viewHolder.name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        String id = model.getUserId();

                        if(id.equals(userId))
                        {
                            ProfileFragment pf = new ProfileFragment();


                            mBlogList.setVisibility(View.INVISIBLE);

                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    pf).commit();

                        }
                        else {
                            UserActivityFragment uaf = new UserActivityFragment();
                            Bundle args = new Bundle();

                            args.putString("extra", id);
                            uaf.setArguments(args);

//Inflate the fragment

                            mBlogList.setVisibility(View.INVISIBLE);

                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                   uaf).commit();
                        }


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


                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                ldt).commit();

                    }
                });


                Calendar c = Calendar.getInstance();
                int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

                if(timeOfDay >= 8 && timeOfDay < 16){

                    viewHolder.homecdView.setCardBackgroundColor(Color.parseColor("#BEb6c2d6"));

                }else if(timeOfDay >= 16 && timeOfDay < 20){

                    viewHolder.homecdView.setCardBackgroundColor(Color.parseColor("#BEf28335"));
                    viewHolder.rateDetails.setTextColor(Color.parseColor("#193d77"));

                }else if(timeOfDay >= 20 && timeOfDay < 24){

                    viewHolder.homecdView.setCardBackgroundColor(Color.parseColor("#BE778fb2"));
                    viewHolder.rateDetails.setTextColor(Color.parseColor("#69ef6d"));

                }else if(timeOfDay>=0 && timeOfDay<4){
                    viewHolder.homecdView.setCardBackgroundColor(Color.parseColor("#BE778fb2"));
                    viewHolder.rateDetails.setTextColor(Color.parseColor("#69ef6d"));
                }

                else if(timeOfDay >= 4 && timeOfDay < 8){

                    //color for dawn.....

                }

               // viewHolder.homecdView.setCardBackgroundColor(Color.parseColor("#AAabef7f"));


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

    private void showPopupMenu(View view, int position, String id) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        PopupMenu popupMenu = new PopupMenu(view.getContext(),view);

        MenuInflater inflater = popupMenu.getMenuInflater();



        if(id.equals(userId)) {

            inflater.inflate(R.menu.home_prof_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {

                        case R.id.goto_Profile:

                            mBlogList.setVisibility(View.INVISIBLE);

                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new ProfileFragment()).commit();

                            return true;
                        case R.id.CopyImage:


                            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("some text", "fuck ya!");
                            clipboard.setPrimaryClip(clip);

                            Toast.makeText(Home.this, "Saved to clip board", Toast.LENGTH_SHORT).show();


                            return true;

                        default:

                            return false;
                    }
                }
            });



        }

        else{

            inflater.inflate(R.menu.popup_menu_user, popupMenu.getMenu());


            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {

                        case R.id.Report:

                            Toast.makeText(Home.this, "Report Post", Toast.LENGTH_SHORT).show();
                            return true;
                        case R.id.Unfollow:

                            Toast.makeText(Home.this, "Unfollow User", Toast.LENGTH_SHORT).show();
                            return true;

                        default:

                            return false;
                    }
                }
            });

        }
        //  popupMenu.setOnMenuItemClickListener(new MyMenuItemClickListener(position));



        popupMenu.show();

    }


    public static class BlogViewHolder extends RecyclerView.ViewHolder{

        View mView;

        ImageButton tools;

        TextView name, rateDetails;

        CardView homecdView;

        int lines;

        TextView post_context, post_title;

        ImageButton rate;

        LottieAnimationView lottie;

        public BlogViewHolder(View itemView) {
            super(itemView);

            mView = itemView ;

            tools =(ImageButton) mView.findViewById(R.id.tools);

            name = (TextView) mView.findViewById(R.id.post_username);
            rateDetails = (TextView) mView.findViewById(R.id.rateDetails);
            rate = (ImageButton) mView.findViewById(R.id.Rate);

            homecdView = (CardView) mView.findViewById(R.id.home_cardView);


            lottie = mView.findViewById(R.id.profile_round);
            //lottieAnimationView.setAnimation("star.json");
            lottie.setAnimation("profile_round.json");


            Typeface typeface = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/tnr.ttf");

            //post_title.setTypeface(typeface);

            rateDetails.setTypeface(typeface);


            
        }


        public void setTitle(String title)
        {
             post_title = (TextView) mView.findViewById(R.id.post_title);


            Typeface typeface = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/courgette.ttf");

            //post_title.setTypeface(typeface);

           post_title.setTypeface(typeface);

            post_title.setText(title);

        }


        public void setContext(String context){

             post_context = (TextView) mView.findViewById(R.id.post_desc);

            Typeface typeface = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/tnr.ttf");

            //post_title.setTypeface(typeface);

           post_context.setTypeface(typeface);


            post_context.setText(context);

            lines = context.split("\r\n|\r|\n").length;

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


        public void setDefaultImage(String defaultImage)
        {

            if(defaultImage.equals("true"))
            {

                ImageView post_image = (ImageView) mView.findViewById(R.id.post_image);



                if(lines <=8) {
                    post_image.setImageResource(R.drawable.emptiness);
                }

                else if(lines>8 && lines<=14)
                {
                    ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) post_image.getLayoutParams();
                    params.width = 400;


                    post_image.setImageResource(R.drawable.emptiness);
                }

                else
                {
                    ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) post_image.getLayoutParams();
                    params.width = 550;

                    post_image.setImageResource(R.drawable.inuyasha);
                }

                post_title.setTextColor(Color.parseColor("#397fef"));

                post_context.setTextColor(Color.parseColor("#a80b1b"));
            }

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


                            try {

                                Glide.with(mView.getContext()).load(ppc).into(profilepic);
                            }catch(Exception e){}

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


        }


    }


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Writers' Corner");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);






        mLayoutManager = new LinearLayoutManager(Home.this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mBlogList = (RecyclerView) findViewById(R.id.home_blog_list) ;
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(mLayoutManager);

        mDatabase =FirebaseDatabase.getInstance().getReference().child("BlogPosts");
        DatabaseReference db = mDatabase.child("Usertime");


        mBlogList.setVisibility(View.VISIBLE);



       // mBlogList.setVisibility(View.VISIBLE);


        firebaseAuth = FirebaseAuth.getInstance();


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        String userId = firebaseAuth.getCurrentUser().getUid();



        searchview = (ImageView) findViewById(R.id.search);



        searchview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mBlogList.setVisibility(View.INVISIBLE);


                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SearchViewFragment()).commit();
            }
        });


        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        ConstraintLayout homeLayout = findViewById(R.id.homelayout);

        if(timeOfDay >= 8 && timeOfDay < 16){


            homeLayout.setBackgroundResource(R.drawable.day);
        }else if(timeOfDay >= 16 && timeOfDay < 20){


            homeLayout.setBackgroundResource(R.drawable.dusk);

        }
        else if(timeOfDay >= 20 && timeOfDay < 24){

            homeLayout.setBackgroundResource(R.drawable.night);
        }
        else if(timeOfDay>=0 && timeOfDay<4){
            homeLayout.setBackgroundResource(R.drawable.night); }

        else if(timeOfDay >= 4 && timeOfDay < 8){

            homeLayout.setBackgroundResource(R.drawable.pic);
        }

        //homeLayout.setBackgroundResource(R.drawable.day);


    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {

            case R.id.nav_home:
                Intent i = new Intent(Home.this, Home.class);
                startActivity(i);
                break;


            case R.id.nav_about:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AboutFragment()).commit();

                break;


            case R.id.nav_profile:


                mBlogList.setVisibility(View.INVISIBLE);

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();



                break;


            case R.id.nav_settings:


                mBlogList.setVisibility(View.INVISIBLE);

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SettingsFragment()).commit();
                break;



            case R.id.nav_logout:

                firebaseAuth.getInstance().signOut();

                finish();
                startActivity(new Intent(this, LoginActivity.class));

                break;



        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent startMain = new Intent(Home.this, Home.class);

            startActivity(startMain);
            finish();
        }
    }






}
