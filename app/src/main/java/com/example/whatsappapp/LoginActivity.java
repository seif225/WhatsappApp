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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

private Button loginButton,phoneLogin,createAccount;
private EditText userEmail,userPassword;
private TextView forgetPassword;
private FirebaseAuth mAuth;
private ProgressDialog loadingbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //init
        FirebaseApp.initializeApp(this);
        mAuth=FirebaseAuth.getInstance();
        initializeFields();

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToRegActivity();
            }
        });

loginButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        allowUserToLogin();
    }
});
phoneLogin.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        startActivity(new Intent(LoginActivity.this , PhoneLoginActicity.class));
    }
});


    }

    private void allowUserToLogin() {
        String email = this.userEmail.getText().toString();
        String password = this.userPassword.getText().toString();
        if(TextUtils.isEmpty(email)){
            this.userEmail.setError("please enter a valid mail");
            this.userEmail.requestFocus();
        }

        if(TextUtils.isEmpty(password)){
            this.userPassword.setError("please enter a valid password");
            this.userPassword.requestFocus();

        }
        else{
            loadingbar.setTitle("signing in");
            loadingbar.setMessage("please wait...");
            loadingbar.setCanceledOnTouchOutside(true);
            loadingbar.show();
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){

                        sendUserToMainActivity();
                        Toast.makeText(getApplicationContext(),"Loggeed in Successfully",Toast.LENGTH_LONG).show();
                        loadingbar.dismiss();

                    }
                    else{



                        String failureMessage=task.getException().toString();
                        Toast.makeText(getApplicationContext(),failureMessage,Toast.LENGTH_LONG).show();
                        loadingbar.dismiss();


                    }



                }
            });


        }


    }

    private void sendUserToRegActivity() {
        startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
    }

    private void initializeFields() {
        loginButton=findViewById(R.id.login_btn);
        phoneLogin=findViewById(R.id.phone_login);
        createAccount=findViewById(R.id.create_account);
        userEmail=findViewById(R.id.login_email);
        userPassword=findViewById(R.id.login_pass_word);
        forgetPassword=findViewById(R.id.forget_password_tv);
        loadingbar=new ProgressDialog(this);

    }



    private void sendUserToMainActivity() {
        Intent mainIntent=new Intent(getApplicationContext() , MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();


    }
}
