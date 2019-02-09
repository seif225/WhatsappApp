package com.example.whatsappapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
private EditText email,password;
private Button register;
private FirebaseAuth mAuth;
private ProgressDialog loadingbar;
private DatabaseReference rootRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //init
        FirebaseApp.initializeApp(this);
        mAuth=FirebaseAuth.getInstance();
        rootRef= FirebaseDatabase.getInstance().getReference();
        initializeFields();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
            }
        });




    }

    private void createNewAccount() {
        String email = this.email.getText().toString();
        String password = this.password.getText().toString();
        if(TextUtils.isEmpty(email)){
            this.email.setError("please enter a valid mail");
            this.email.requestFocus();
        }

        if(TextUtils.isEmpty(password)){
            this.password.setError("please enter a valid password");
            this.password.requestFocus();

        }

        else
        {
            loadingbar.setTitle("making a new Account");
            loadingbar.setMessage("please wait, while we're making a new account");
            loadingbar.setCanceledOnTouchOutside(true);
            loadingbar.show();
            Log.e("comple teTask","app stopped before email creation");

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){
                        String userId=mAuth.getCurrentUser().getUid();
                        Log.e("completeTask",userId);
                        rootRef.child("users").child(userId).setValue("");
                        Log.e("completeTask","el app byo2f b3d el rooref");

                        Toast.makeText(getApplicationContext(),"the account has been made successfully",Toast.LENGTH_LONG).show();
                        loadingbar.dismiss();
                        sendUserToMainActivity();
                    }
else {
                        Log.e("completeTask","failed");

                        String failureMessage=task.getException().toString();
                        Toast.makeText(getApplicationContext(),failureMessage,Toast.LENGTH_LONG).show();
                        loadingbar.dismiss();

                    }


                }
            });
        }
    }

    private void sendUserToMainActivity() {
                Intent mainIntent=new Intent(getApplicationContext() , LoginActivity.class);
               // mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                 startActivity(mainIntent);
               // finish();


    }

    private void sendUserToLoginActivity() {
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));

    }

    private void initializeFields() {
        email=findViewById(R.id.reg_email);
        password=findViewById(R.id.reg_pass_word);
        register=findViewById(R.id.reg_btn);
        loadingbar=new ProgressDialog(this);

    }
}
