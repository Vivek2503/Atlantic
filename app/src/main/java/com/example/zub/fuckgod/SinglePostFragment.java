package com.example.zub.fuckgod;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.graphics.Color.*;


/**
 * A simple {@link Fragment} subclass.
 */
public class SinglePostFragment extends Fragment {

    String extra, extra2, extra3;

    DatabaseReference databaseReference, likes;

    DatabaseReference profiledb;

    RelativeLayout ImageButtonView;

    ImageButton rate;

    TextView rateDetails;

    int x =0;

    String ProfileId;

    boolean rateClickable = true;

    boolean mProcessLike=false;

    public SinglePostFragment() {
        // Required empty public constructor
    }






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //getActivity().setTitle(" ");

         extra = getArguments().getString("extra");

        extra2 = getArguments().getString("extra2");

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_single_post, container, false);
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        ImageButtonView =(RelativeLayout) getView().findViewById(R.id.ImageButtonView);

        rate= (ImageButton) getView().findViewById(R.id.Rate);





        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        if(extra2.equals("profile"))
        {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("UserPosts").child(userId).child(extra);

            likes = FirebaseDatabase.getInstance().getReference().child("Likes").child(extra);
        }

        else if(extra2.equals("home"))
        {
            databaseReference=FirebaseDatabase.getInstance().getReference().child("BlogPosts").child(extra);

            extra3 = getArguments().getString("extra3");

            likes = FirebaseDatabase.getInstance().getReference().child("Likes").child(extra3);


        }
        else
        {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("UserPosts").child(extra2).child(extra);

            likes = FirebaseDatabase.getInstance().getReference().child("Likes").child(extra);
        }




        TextView title = (TextView) getView().findViewById(R.id.post_title);

        TextView context = (TextView) getView().findViewById(R.id.post_desc);

        TextView name = (TextView) getView().findViewById(R.id.post_username);

        TextView usertimetext = (TextView) getView().findViewById(R.id.usertime);

        CircleImageView profilepic = (CircleImageView) getView().findViewById(R.id.profilepicBlog) ;

        ImageButton menu = (ImageButton) getView().findViewById(R.id.tools);

        CircleImageView userdp = (CircleImageView) getView().findViewById(R.id.userDp);

        rateDetails = (TextView)getView().findViewById(R.id.rateDetails);


        //user dp for comment section......

        ValueEventListener ref = FirebaseDatabase.getInstance().getReference().child("User_profilePic").child(userId).child("profilepic")

                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String ppc = dataSnapshot.getValue(String.class);
                        // Toast.makeText(getActivity(), check, Toast.LENGTH_SHORT).show();


                        Glide.with(getActivity()).load(ppc).into(userdp);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });





        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try {
                    title.setText(dataSnapshot.child("title").getValue().toString());

                    getActivity().setTitle(dataSnapshot.child("title").getValue().toString());

                    context.setText(dataSnapshot.child("Context").getValue().toString());

                    name.setText(dataSnapshot.child("name").getValue().toString());

                    String usertime = dataSnapshot.child("Usertime").getValue().toString();



                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyy HH:mm:ss z");
                    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                    long times = sdf.parse(usertime).getTime();
                    long now = System.currentTimeMillis();

                    CharSequence ago =
                            DateUtils.getRelativeTimeSpanString(times, now, DateUtils.MINUTE_IN_MILLIS);


                    Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/tnr.ttf");

                    //post_title.setTypeface(typeface);

                    usertimetext.setTypeface(typeface);

                    if(ago.equals("0 minutes ago"))
                        ago = "just now";

                    usertimetext.setText(ago);


                    String id = dataSnapshot.child("UserId").getValue().toString();

                    ValueEventListener ref = FirebaseDatabase.getInstance().getReference().child("User_profilePic").child(id).child("profilepic")

                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    String ppc = dataSnapshot.getValue(String.class);
                                    // Toast.makeText(getActivity(), check, Toast.LENGTH_SHORT).show();


                                    Glide.with(getActivity()).load(ppc).into(profilepic);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                    name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String userId = user.getUid();


                            if (id.equals(userId)) {
                                ProfileFragment pf = new ProfileFragment();

                                android.support.v4.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

                                fragmentTransaction.replace(R.id.fragment_container, pf).commit();

                            } else {
                                UserActivityFragment uaf = new UserActivityFragment();
                                Bundle args = new Bundle();

                                args.putString("extra", id);
                                uaf.setArguments(args);

//Inflate the fragment
                                android.support.v4.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

                                fragmentTransaction.replace(R.id.fragment_container, uaf).commit();
                            }

                        }
                    });

                    profilepic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            Intent i = new Intent(getActivity(), UserDP.class);

                            i.putExtra("extra", id);


                            startActivity(i);


                        }
                    });


                    menu.setOnClickListener(new View.OnClickListener() {


                        @Override
                        public void onClick(View v) {

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String userId = user.getUid();

                            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);

                            MenuInflater inflater = popupMenu.getMenuInflater();

                            if (id.equals(userId)) {

                                inflater.inflate(R.menu.popup_menu, popupMenu.getMenu());

                                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem menuItem) {
                                        switch (menuItem.getItemId()) {

                                            case R.id.Hide:


                                                if (extra2.equals("profile")) {
                                                    databaseReference.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                            if (dataSnapshot.exists()) {

                                                                String homeid = dataSnapshot.child("HomeId").getValue().toString();


                                                                hideFromHome(homeid, "profile");


                                                            }
                                                        }


                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {


                                                        }
                                                    });


                                                }
                                                else if(extra2.equals("home"))
                                                {

                                                    databaseReference.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                            if (dataSnapshot.exists()) {

                                                                try {

                                                                    String profileid = dataSnapshot.child("ProfileId").getValue().toString();


                                                                    //  DatabaseReference temp = FirebaseDatabase.getInstance().getReference().child("UserPosts").child(userId).child(extra);


                                                                    hideFromHome(profileid, "home");

                                                                }catch(Exception e){}

                                                            }
                                                        }


                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {


                                                        }
                                                    });

                                                }



                                                return true;
                                            case R.id.Delete:

                                                // Toast.makeText(getActivity(), "Delete Post", Toast.LENGTH_SHORT).show();

                                                if (extra2.equals("profile")) {
                                                    databaseReference.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                            if (dataSnapshot.exists()) {

                                                                String homeid = dataSnapshot.child("HomeId").getValue().toString();


                                                             deletePost(homeid, "profile");


                                                            }
                                                        }


                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {


                                                        }
                                                    });


                                                } else if (extra2.equals("home")) {



                                                    databaseReference.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                            if (dataSnapshot.exists()) {

                                                                String profileid = dataSnapshot.child("ProfileId").getValue().toString();


                                                                deletePost(profileid, "home");


                                                            }
                                                        }


                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {


                                                        }
                                                    });



                                                }
                                                return true;

                                            default:

                                                return false;
                                        }
                                    }
                                });


                            } else {
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
                            }


                            popupMenu.show();
                        }
                    });


                }catch (Exception e){}
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });


        likes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                long countLikes = dataSnapshot.getChildrenCount();
                rateDetails.setText((int) countLikes + " "+ "Starred");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        likes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(userId))
                    rate.setImageResource(R.drawable.icons8_star_rated);
                else
                    rate.setImageResource(R.drawable.icons8_star);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(extra2.equals("profile"))
        {

            databaseReference.child("hidden").addValueEventListener(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(dataSnapshot.exists()){
                                try {
                                    if (dataSnapshot.getValue().toString().equals("true")) {
                                        ImageButtonView.setBackgroundColor(Color.parseColor("#c2d6d5"));

                                        rateClickable = false;

                                        rate.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                Toast.makeText(getActivity(), "Rating or other activities are not available for hidden posts!", Toast.LENGTH_SHORT).show();

                                            }
                                        });

                                    }

                                }catch(Exception e){}
                        }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    }
            );

        }


        rateDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LikeDetails ldt = new LikeDetails();
                Bundle args = new Bundle();

                if(extra2.equals("profile"))
                  args.putString("extra",extra);
                else if(extra2.equals("home"))
                    args.putString("extra",ProfileId);
                else
                    args.putString("extra",extra);

                ldt.setArguments(args);


                android.support.v4.app.FragmentTransaction fragmentTransaction =getFragmentManager().beginTransaction();

                fragmentTransaction.replace(R.id.fragment_container, ldt).commit();

            }
        });

        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if(rateClickable)
               {

                   mProcessLike =true;

                   FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                   String userId = user.getUid();

                   // Toast.makeText(Home.this, "User Exists. Try logging in.", Toast.LENGTH_SHORT).show();



                   likes.addValueEventListener(
                           new ValueEventListener() {
                               @Override
                               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                   if (mProcessLike) {
                                       if (dataSnapshot.hasChild(userId)) {
                                           likes.child(userId).removeValue();
                                           rate.setImageResource(R.drawable.icons8_star);
                                           mProcessLike = false;
                                       } else {


                                           likes.child(userId).child("id").setValue(userId);

                                           rate.setImageResource(R.drawable.icons8_star_rated);
                                           mProcessLike = false;
                                       }

                                   }

                               }
                               @Override
                               public void onCancelled(@NonNull DatabaseError databaseError) {

                               }
                           }
                   );


               }


            }
        });




    }
//method to delete....
    private void deletePost(String id, String activityname) {

        ViewDialog alert = new ViewDialog();
        alert.showDialognext(getActivity(), id, activityname);


    }


    //method to hide...
    private void hideFromHome(String id, String activityname) {

        ViewDialog alert = new ViewDialog();
        try {
            alert.showDialog(getActivity(), id, activityname);
        }catch (Exception e){}

    }

    public class ViewDialog {

        public void showDialog(Activity activity, String id, String activityname){

            try {
                final Dialog dialog = new Dialog(activity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.hide_dialog);

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String userId = user.getUid();

                TextView dialogtext = (TextView) dialog.findViewById(R.id.dialogtext);

                Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/courgette.ttf");


                dialogtext.setTypeface(typeface);

                TextView HideText = (TextView) dialog.findViewById(R.id.HideText);

                Typeface typeface1 = Typeface.createFromAsset(getContext().getAssets(), "fonts/tnr.ttf");


                HideText.setTypeface(typeface1);


                TextView cancelText = (TextView) dialog.findViewById(R.id.cancelText);

                cancelText.setTypeface(typeface1);

                cancelText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                try {
                    dialog.show();
                }catch(Exception e){}

                HideText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        HideText.setClickable(false);

                        DatabaseReference profiledb, homedb;

                        if (activityname.equals("profile")) {
                            profiledb = FirebaseDatabase.getInstance().getReference().child("UserPosts").child(userId).child(extra);

                            homedb = FirebaseDatabase.getInstance().getReference().child("BlogPosts").child(id);

                            homedb.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    profiledb.child("hidden").setValue("true").addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                           dialog.dismiss();

                                            try {

                                                Intent startMain = new Intent(getActivity(), Home.class);

                                                startActivity(startMain);
                                                getActivity().finish();
                                            } catch (Exception e) {
                                            }
                                        }
                                    });

                                }
                            });

                        } else if (activityname.equals("home")) {
                            homedb = FirebaseDatabase.getInstance().getReference().child("BlogPosts").child(extra);

                            profiledb = FirebaseDatabase.getInstance().getReference().child("UserPosts").child(userId).child(id);

                            homedb.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    profiledb.child("hidden").setValue("true").addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                           dialog.dismiss();

                                            try {

                                                Intent startMain = new Intent(getActivity(), Home.class);

                                                startActivity(startMain);
                                                getActivity().finish();
                                            } catch (Exception e) {
                                            }

                                        }
                                    });


                                }
                            });


                        }

                        // dialog.dismiss();
                    }
                });


            }catch(Exception e)
            {

            }
        }

        public void showDialognext(Activity activity, String id, String activityname) {


            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.delete_dialog);


            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String userId = user.getUid();

            TextView dialogtext = (TextView) dialog.findViewById(R.id.dialogtext);

            Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/courgette.ttf");



            dialogtext.setTypeface(typeface);

            TextView DeleteText = (TextView) dialog.findViewById(R.id.DeleteText);

            Typeface typeface1 = Typeface.createFromAsset(getContext().getAssets(), "fonts/tnr.ttf");



            DeleteText.setTypeface(typeface1);


            TextView cancelText = (TextView) dialog.findViewById(R.id.cancelText);

            cancelText.setTypeface(typeface1);

            cancelText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            try {

                 dialog.show();
            }catch(Exception e){}


            DeleteText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DeleteText.setClickable(false);

                    DatabaseReference profiledb,   homedb, likes;

                    if(activityname.equals("profile")) {

                        profiledb = FirebaseDatabase.getInstance().getReference().child("UserPosts").child(userId).child(extra);

                        homedb = FirebaseDatabase.getInstance().getReference().child("BlogPosts").child(id);

                        likes = FirebaseDatabase.getInstance().getReference().child("Likes").child(extra);

                        homedb.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {


                                profiledb.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        likes.removeValue();
                                        dialog.dismiss();


                                        try {

                                            Intent startMain = new Intent(getActivity(), Home.class);

                                            startActivity(startMain);
                                            getActivity().finish();
                                        }catch(Exception e){}


                                    }
                                });


                            }
                        });

                    }
                    else if(activityname.equals("home"))
                    {


                        homedb = FirebaseDatabase.getInstance().getReference().child("BlogPosts").child(extra);

                        profiledb = FirebaseDatabase.getInstance().getReference().child("UserPosts").child(userId).child(id);

                        likes= FirebaseDatabase.getInstance().getReference().child("Likes").child(id);

                        profiledb.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {


                                homedb.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        likes.removeValue();
                                        dialog.dismiss();
                                        try {

                                            Intent startMain = new Intent(getActivity(), Home.class);

                                            startActivity(startMain);
                                            getActivity().finish();
                                        }catch(Exception e){}

                                    }
                                });


                            }
                        });





                    }


                }
            });


//            dialog.show();

        }

    }



}