package com.example.zub.fuckgod;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class UploadImage extends AppCompatActivity {


    private CircleImageView profilePic;

    private Uri mainImageURI = null;

    private ProgressBar progressBar;

    private Button upload;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);


        profilePic = (CircleImageView)findViewById(R.id.picToUpload);
        upload=(Button)findViewById(R.id.upload);


        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();



        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(UploadImage.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(UploadImage.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {




                         CropImage.activity(mainImageURI)
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1,1)
                                .setAutoZoomEnabled(false)
                                .start(UploadImage.this);
                                ;


                      //  startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);



                    }
                }


            }
        });


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String userId = firebaseAuth.getCurrentUser().getUid();

               progressBar.setVisibility(View.VISIBLE);


                StorageReference image_path = storageReference.child("profile_pics").child(userId + ".jpg");

                image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful())
                        {



                            image_path.getDownloadUrl().addOnSuccessListener(
                                    new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            //Toast.makeText(getActivity(), "fuck", Toast.LENGTH_SHORT).show();

                                            Uri downloadurl = uri;

                                            DatabaseReference newdatabaseReference = FirebaseDatabase.getInstance().getReference().child("User_profilePic").child(userId);

                                            PassProfilePic passProfilePic = new PassProfilePic(downloadurl.toString());
                                            newdatabaseReference.setValue(passProfilePic).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    upload.setClickable(false);
                                                    upload.setVisibility(View.INVISIBLE);



                                                    progressBar.setVisibility(View.INVISIBLE);

                                                    Intent i = new Intent(UploadImage.this, Home.class);
                                                    finish();
                                                    startActivity(i);

                                                }
                                            });





                                        }
                                    }
                            );











                        }



                    }
                });


            }
        });



    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                profilePic.setImageURI(mainImageURI);

                upload.setVisibility(View.VISIBLE);
                upload.setClickable(true);

                final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);

                // Use bounce interpolator with amplitude 0.2 and frequency 20
                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                myAnim.setInterpolator(interpolator);


                upload.startAnimation(myAnim);



            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }




}
