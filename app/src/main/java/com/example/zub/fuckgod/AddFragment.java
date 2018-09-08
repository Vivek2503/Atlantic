package com.example.zub.fuckgod;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

import java.sql.Timestamp;
import java.util.Date;
import java.text.ParseException;

import java.util.Locale;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddFragment extends Fragment {

    public EditText title;
    public EditText context;
    public Button post;


    private FirebaseAuth firebaseAuth;

    public ProgressBar progressBar;












    public AddFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Add Your Post");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add, container, false);
    }




    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();






        title = (EditText) getView().findViewById(R.id.title);

        context = (EditText) getView().findViewById(R.id.context);
       // context.setMovementMethod(new ScrollingMovementMethod());

        post = (Button) getView().findViewById(R.id.post);

        progressBar = (ProgressBar) getView().findViewById(R.id.addprogressBar);

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String titletext = title.getText().toString();
                String contextText = context.getText().toString();

                post.setTextColor(Color.parseColor("#ffffff"));



                if(titletext.isEmpty())
                {
                    title.setError("Title is missing");

                }

                else if(contextText.isEmpty())
                {
                    context.setError("Your content is missing");
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);


                    PostFragment pfrag = new PostFragment();

                    Bundle args = new Bundle();
                    args.putString("extra", titletext);
                    args.putString("extra2", contextText);
                    pfrag.setArguments(args);


                    android.support.v4.app.FragmentTransaction fragmentTransaction =getFragmentManager().beginTransaction();

                    fragmentTransaction.replace(R.id.fragment_container, pfrag).commit();





                    // newPost.child("image").setValue(downloadURL.tostring());




                }





            }
        });



    }



}
