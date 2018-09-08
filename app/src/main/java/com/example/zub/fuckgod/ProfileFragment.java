package com.example.zub.fuckgod;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.Proxy;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {



    private CircleImageView profilePic;

    private Uri mainImageURI = null;



    private StorageReference storageReference;

    private FirebaseAuth firebaseAuth;




    DatabaseReference databaseReference;
    private DatabaseReference mDatabase;

    private TextView textView;

    private String firstname;
    private String lastname;


    public BottomNavigationView bottomNavigationView;

    private AddFragment addFragment;

    private RecyclerView mBlogList;

    private LinearLayoutManager mLayoutManager;



    String title, Context, Usertime,name, defaultImage;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Profile");

        // thiscontext = inflater.getContext();
        return inflater.inflate(R.layout.fragment_profile, container, false);


    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(

                Blog.class,
                R.layout.profile_row,
                BlogViewHolder.class,
                mDatabase


        ) {
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, Blog model, int position) {


                String post_key = getRef(position).getKey();

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

                viewHolder.setHidden(model.getHidden());


                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        SinglePostFragment spf = new SinglePostFragment();
                        Bundle args = new Bundle();
                        args.putString("extra", post_key);
                        args.putString("extra2", "profile");
                        spf.setArguments(args);

//Inflate the fragment
                        android.support.v4.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

                        fragmentTransaction.replace(R.id.fragment_container, spf).commit();


                    }
                });


                viewHolder.post_hidden.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        retrieveActivity(post_key);



                    }
                });



            }
        };


        mBlogList.setAdapter(firebaseRecyclerAdapter);


        // mBlogList.setNestedScrollingEnabled(false);


    }

    private void retrieveActivity(String post_key) {


        ViewDialog alert = new ViewDialog();
        alert.showDialog(getActivity(), post_key);



    }



    public class ViewDialog {

        public void showDialog(Activity activity, String key){
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.dialog);

            TextView dialogtext = (TextView) dialog.findViewById(R.id.dialogtext);

            Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/courgette.ttf");



            dialogtext.setTypeface(typeface);

            TextView retrieveText = (TextView) dialog.findViewById(R.id.retrieveText);

            Typeface typeface1 = Typeface.createFromAsset(getContext().getAssets(), "fonts/tnr.ttf");



            retrieveText.setTypeface(typeface1);


            TextView cancelText = (TextView) dialog.findViewById(R.id.cancelText);

            cancelText.setTypeface(typeface1);

            cancelText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });






            retrieveText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                    RePostFragment rep = new RePostFragment();
                    Bundle args = new Bundle();

                    args.putString("extra", key);
                    rep.setArguments(args);

//Inflate the fragment
                    android.support.v4.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

                    fragmentTransaction.replace(R.id.fragment_container, rep).commit();





                    dialog.dismiss();
                }
            });

            dialog.show();

        }
    }




    public static class BlogViewHolder extends RecyclerView.ViewHolder{

        View mView;





        TextView post_title, post_context;



        ImageButton post_hidden;





        public BlogViewHolder(View itemView) {
            super(itemView);

             mView = itemView ;




        }



        public void setTitle(String title)
        {
             post_title = (TextView) mView.findViewById(R.id.post_title);


            Typeface typeface = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/courgette.ttf");



            this.post_title.setTypeface(typeface);

            post_title.setText(title.replace("\n"," "));

        }


        public void setContext(String context){

             post_context = (TextView) mView.findViewById(R.id.post_desc);

            Typeface typeface = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/tnr.ttf");



            this.post_context.setTypeface(typeface);

            post_context.setText(context.replace("\n"," "));




        }

        public void setHidden(String hidden)
        {
            post_hidden = (ImageButton)mView.findViewById(R.id.post_hidden);

            if(hidden.equals("true"))
            {
                post_hidden.setVisibility(View.VISIBLE);
                post_hidden.setClickable(true);
            }

        }

        public void setName(String name)
        {

            //to do later(maybe)....
        }

        public void setDefaultImage(String defaultImage)
        {

           //to do later......

        }

        public void setUsertime(String usertime) throws ParseException {


            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyy HH:mm:ss z");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            long times = sdf.parse(usertime).getTime();
            long now = System.currentTimeMillis();

            CharSequence ago =
                    DateUtils.getRelativeTimeSpanString(times, now, DateUtils.MINUTE_IN_MILLIS);

            TextView time = (TextView) mView.findViewById(R.id.posttime);

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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {


        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        textView = (TextView) getView().findViewById(R.id.name);
        bottomNavigationView = (BottomNavigationView) getView().findViewById(R.id.bottom_nav);

        mBlogList = (RecyclerView) getView().findViewById(R.id.blog_list) ;
        mBlogList.setHasFixedSize(true);

        mBlogList.setLayoutManager(mLayoutManager);

        mBlogList.setNestedScrollingEnabled(false);



        databaseReference = FirebaseDatabase.getInstance().getReference().child("User_Info");
        mDatabase =FirebaseDatabase.getInstance().getReference().child("UserPosts").child(userId);





        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {




                firstname=dataSnapshot.child(userId).child("firstname").getValue().toString();
                lastname=dataSnapshot.child(userId).child("lastname").getValue().toString();

                //textView.setVisibility(View.VISIBLE);



                Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/courgette.ttf");



                textView.setTypeface(typeface);

                textView.setText("Hey, " +firstname+ " "+ lastname+" !");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(getActivity(), "sss Exists. Try logging in.", Toast.LENGTH_SHORT).show();
            }
        });


        //Retrieve Data from Database.....

        





        
            profilePic = (CircleImageView) getView().findViewById(R.id.pic);



            profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent i = new Intent(getActivity(), FullProfilePic.class);

                getActivity().onBackPressed();

                getActivity().finish();

                startActivity(i);






            }
        });













//changes are here.......




    ValueEventListener ref = FirebaseDatabase.getInstance().getReference().child("User_profilePic").child(userId).child("profilepic")
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String ppc = dataSnapshot.getValue(String.class);
                   // Toast.makeText(getActivity(), check, Toast.LENGTH_SHORT).show();

                    RequestOptions placeholderRequest = new RequestOptions();
                    placeholderRequest.placeholder(R.drawable.defimg);
                    try {

                        Glide.with(getActivity()).setDefaultRequestOptions(placeholderRequest).load(ppc).into(profilePic);

                    }catch(Exception e){}

                    }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        addFragment = new AddFragment();




        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.add :
                        //bottomNavigationView.setItemBackgroundResource(R.color.another);
                        setFragment(addFragment);
                        return true;

                    default:
                        return false;

                }
            }
        });



    }

    private void setFragment(Fragment fragment) {
        android.support.v4.app.FragmentTransaction fragmentTransaction =getFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container, fragment).commit();
    }




}
