package com.example.whatsappapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;
    FMAdapter fmAdapter;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private ProgressDialog groupebar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init
        //firebase
        FirebaseApp.initializeApp(this);
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        rootRef=FirebaseDatabase.getInstance().getReference();
        //non-firebase
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.include);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("whatsappApp");
        viewPager=findViewById(R.id.main_tabs_pager);
        tabLayout=findViewById(R.id.main_tabs);
        fmAdapter=new FMAdapter(getSupportFragmentManager());
        viewPager.setAdapter(fmAdapter);
        tabLayout.setupWithViewPager(viewPager);
        groupebar=new ProgressDialog(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser == null){
            sendUserToLoginActivity();
        }
        else{

            verifyUserExistence();
        }


    }

    private void verifyUserExistence() {
         String cureentUserId= mAuth.getCurrentUser().getUid();
            rootRef.child("users").child(cureentUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if((dataSnapshot.child("name").exists())){
                        Toast.makeText(getApplicationContext(),"welcome baby",Toast.LENGTH_LONG).show();

                    }
                    else{
                        sendUserToSettingsActivity();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

    }

    private void sendUserToSettingsActivity() {

        Intent mainIntent=new Intent(MainActivity.this , SettingsActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }

    private void sendUserToLoginActivity() {
        Intent mainIntent=new Intent(MainActivity.this , LoginActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);
        if(item.getItemId()== R.id.main_sign_out_option){
            mAuth.signOut();
            sendUserToLoginActivity();
        }
        if(item.getItemId()== R.id.main_settings_option){
            startActivity(new Intent (this,SettingsActivity.class));

        }

        if(item.getItemId()== R.id.main_create_group_option){

          requestNewGroup();

        }


        if(item.getItemId()== R.id.main_find_friends_option){

        }
            return true;
    }

    private void requestNewGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        builder
                .setTitle("enter group name");
        final EditText groupNameField = new EditText(MainActivity.this);
        groupNameField.setHint("e.g group seif w so7abo");

        builder.setView(groupNameField);
        builder.setPositiveButton("Create ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName= groupNameField.getText().toString();
                if(TextUtils.isEmpty(groupName)){
                   // Toast.makeText(getApplicationContext(),"please enter a group name",Toast.LENGTH_LONG).show();
                    groupNameField.setError("please enter a group");
                    groupNameField.requestFocus();
                    groupebar.setTitle("making a new group");
                    groupebar.setMessage("please wait untill the group is made ..");
                    groupebar.show();

                }
                    else{


                            createNewGroup(groupName);

                }

            }
        });

        builder.setNegativeButton("cancel ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName= groupNameField.getText().toString();

               dialog.cancel();

            }
        });

            builder.show();
    }

    private void createNewGroup(final String grroupName ){

        rootRef.child("groups").child(grroupName).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    Toast.makeText(getApplicationContext(),grroupName+"group is created",Toast.LENGTH_LONG).show();
                    groupebar.dismiss();
                }

            }
        });


    }
}
