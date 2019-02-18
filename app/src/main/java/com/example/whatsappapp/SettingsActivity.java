package com.example.whatsappapp;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    //declarations
    private Button updateAccountSettings;
    EditText userName, status;
    private CircleImageView profilePicture;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private DatabaseReference rootRef;
    private static final int GALLERY_PICK=1;
    private StorageReference userProfileImageRef;
    private String link;
    private Toolbar settingsToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //init

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        userProfileImageRef= FirebaseStorage.getInstance().getReference().child("profile images");
        initializeFields();
        updateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updataSettings();
            }
        });


                retriveUserInformation();

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent=new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_PICK);


            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode==RESULT_OK && data!=null){

            Uri ImageUri=data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);


        }

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                if (resultCode == RESULT_OK)
                {
                    final Uri resultUri = result.getUri();
                    final StorageReference filePath= userProfileImageRef.child(currentUserID + ".jpg");
                    filePath.putFile(resultUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                                link=uri.toString();
                                            rootRef.child("users").child(currentUserID).child("image").setValue(link);

                                        }
                                    });
                                }
                            })

                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if (task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"your picture has been updated successfully",Toast.LENGTH_LONG).show();





                            }
                                else
                                    {
                                        String errorMessage = task.getException().toString();
                                        Toast.makeText(SettingsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

                                    }

                        }

                    })
                    ;



                }
            }
        }



    private void retriveUserInformation() {

        rootRef.child("users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")) && dataSnapshot.hasChild("image") )
           {
                    String retriveUserName=dataSnapshot.child("name").getValue().toString();
                    String retriveStatus=dataSnapshot.child("status").getValue().toString();
                    String retriveImage=dataSnapshot.child("image").getValue().toString();
               Picasso.with(getBaseContext()).load(retriveImage).into(profilePicture);
               userName.setText(retriveUserName);
                     status.setText(retriveStatus);


           }
            else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")))
                    {

                        String retriveUserName=dataSnapshot.child("name").getValue().toString();
                        String retriveStatus=dataSnapshot.child("status").getValue().toString();
                        //String retriveImage=dataSnapshot.child("image").getValue().toString();

                        Log.e("sec if",retriveUserName);
                        Log.e("sec if ",retriveStatus);

                        userName.setText(retriveUserName);
                        status.setText(retriveStatus);



                     }
            else {
                    Toast.makeText(getApplicationContext(),"please set and update your profile",Toast.LENGTH_LONG).show();



           }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void updataSettings() {
        String setUserName = userName.getText().toString();
        String setStatus = status.getText().toString();

        if (TextUtils.isEmpty(setUserName)) {
            userName.setError("you can't leave this field empty");
            userName.requestFocus();

        }

        if (TextUtils.isEmpty(setStatus)) {
            status.setError("you can't leave this field empty");
            status.requestFocus();

        } else {
            HashMap<String, Object> profile = new HashMap<>();
            profile.put("uid", currentUserID);
            profile.put("name", setUserName);
            profile.put("status", setStatus);
            rootRef.child("users").child(currentUserID).updateChildren(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {

                        Toast.makeText(getApplicationContext(), "the profile has been updated ..", Toast.LENGTH_LONG).show();
                        sendUserToMainActivity();
                    } else {
                        String errorMessage = task.getException().toString();
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();

                    }

                }
            });

        }

    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();


    }

    private void initializeFields() {
        updateAccountSettings = findViewById(R.id.save_settings_btn);
        userName = findViewById(R.id.set_user_name);
        status = findViewById(R.id.set_user_status);
        profilePicture = findViewById(R.id.set_profile_image);
        settingsToolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(settingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Account Settings");


    }
}