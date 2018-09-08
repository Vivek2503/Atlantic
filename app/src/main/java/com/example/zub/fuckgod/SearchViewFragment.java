package com.example.zub.fuckgod;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchViewFragment extends Fragment {


    EditText msearchfield;
    ImageButton mSearchBtn;
    RecyclerView mResultList;

    private DatabaseReference mdatabaseref, databaseReference;


    private LinearLayoutManager mLayoutManager;


    public SearchViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Search");
        return inflater.inflate(R.layout.fragment_search_view, container, false);
    }




    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {


        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        msearchfield =(EditText)getView().findViewById(R.id.search_field);
        mSearchBtn = (ImageButton)getView().findViewById(R.id.sbtn);
        mResultList = (RecyclerView) getView().findViewById(R.id.searchcontent);

        mdatabaseref = FirebaseDatabase.getInstance().getReference().child("User_Info");

        databaseReference = FirebaseDatabase.getInstance().getReference();

        mResultList.setHasFixedSize(true);


        mResultList.setLayoutManager(mLayoutManager);

        mResultList.setNestedScrollingEnabled(false);


        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String searchquery = msearchfield.getText().toString();

                if(!searchquery.isEmpty()){
                    firebaseUserSearch(searchquery);
                }



            }
        });


        msearchfield.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {





            }

            @Override
            public void afterTextChanged(Editable s) {


                try {
                    if (!s.toString().isEmpty()) {
                        setAdapter(s.toString());
                    }

                }catch(Exception e)
                {

                }


            }
        });








    }

    private void setAdapter(String s) {


        databaseReference.child("User_Info").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot snapshot : dataSnapshot.getChildren())
                        {

                            firebaseUserSearch(s);
                            //  }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

    }


    private void firebaseUserSearch(String searchquery) {

        Query firebaseSearchQuery = mdatabaseref.orderByChild("name").startAt(searchquery.toLowerCase()).endAt(searchquery.toLowerCase()+"\uf8ff");

        FirebaseRecyclerAdapter<SearchUsers,SearchViewFragment.usersViewHolder > firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<SearchUsers, SearchViewFragment.usersViewHolder>(

                SearchUsers.class,
                R.layout.list_layout,
                SearchViewFragment.usersViewHolder.class,
                firebaseSearchQuery


        ) {
            @Override
            protected void populateViewHolder(SearchViewFragment.usersViewHolder viewHolder, SearchUsers model, int position) {



                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String userId = user.getUid();

                String id = model.getUserId();

                viewHolder.setDetails(model.getFirstname(), model.getLastname());
                viewHolder.setuserId(model.getUserId());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(id.equals(userId))
                        {


                           // Toast.makeText(getActivity(), "User Exists. Try logging in.", Toast.LENGTH_SHORT).show();


                            ProfileFragment pf = new ProfileFragment();

                            android.support.v4.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

                            fragmentTransaction.replace(R.id.fragment_container, pf).commit();

                        }
                        else
                        {



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


            }
        };


        mResultList.setAdapter(firebaseRecyclerAdapter);
    }



    public static class usersViewHolder extends  RecyclerView.ViewHolder{

        View mView;

        public usersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setDetails(String firstname, String lastname){

            TextView username = (TextView)mView.findViewById(R.id.searchname);

            username.setText(firstname + " " + lastname);





        }


        public void setuserId(String userId) {


            CircleImageView profilepic = (CircleImageView) mView.findViewById((R.id.profiledp));


            ValueEventListener ref = FirebaseDatabase.getInstance().getReference().child("User_profilePic").child(userId).child("profilepic")

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



}
