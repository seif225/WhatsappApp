package com.example.whatsappapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {


    private String recieverUserId, currentState, currentUserId;
    private CircleImageView userProfileImage;
    private TextView userProfileName, userProfileStatus;
    private Button sendaMessageButton, declineMessageRequestButton;
    private DatabaseReference userRef, chatRequestRef, contactsRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        contactsRef = FirebaseDatabase.getInstance().getReference().child("contacts");
        userRef = FirebaseDatabase.getInstance().getReference().child("users");
        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("chat requests");
        recieverUserId = getIntent().getExtras().get("visit_user_id").toString();
        currentUserId = mAuth.getCurrentUser().getUid();

        //Toast.makeText(this, recieverUserId, Toast.LENGTH_SHORT).show();
        userProfileImage = findViewById(R.id.user_profile_picture_profile_activity);
        userProfileName = findViewById(R.id.user_name_profile_activity);
        userProfileStatus = findViewById(R.id.user_status_profile_activity);
        sendaMessageButton = findViewById(R.id.send_message_request_button);
        declineMessageRequestButton = findViewById(R.id.refuse_message_request_button);
        currentState = "new";
        retriveUserInfo();


    }

    private void retriveUserInfo() {
        userRef.child(recieverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if ((dataSnapshot.exists()) && dataSnapshot.hasChild("image")) {
                    String userImage = dataSnapshot.child("image").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userStatuts = dataSnapshot.child("status").getValue().toString();
                    Picasso.with(getBaseContext()).load(userImage).into(userProfileImage);
                    userProfileName.setText(userName);
                    userProfileStatus.setText(userStatuts);
                    ManageChatRequest();

                } else {
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userStatuts = dataSnapshot.child("status").getValue().toString();
                    userProfileName.setText(userName);
                    userProfileStatus.setText(userStatuts);
                    ManageChatRequest();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void ManageChatRequest() {

        chatRequestRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(recieverUserId)) {

                    String requestType = dataSnapshot.child(recieverUserId).child("request_type").getValue().toString();

                    if (requestType.equals("sent")) {

                        currentState = "request_sent";
                        sendaMessageButton.setText("cancel chat request");

                    } else if (requestType.equals("received")) {
                        currentState = "request_received";
                        sendaMessageButton.setText("accept request");
                        declineMessageRequestButton.setVisibility(View.VISIBLE);
                        declineMessageRequestButton.setEnabled(true);
                        declineMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancelChatRequest();


                            }
                        });


                    }


                } else {
                    contactsRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChild(recieverUserId)) {

                                currentState = "friends";

                                sendaMessageButton.setText("remove this contact");

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if (!currentUserId.equals(recieverUserId)) {

            sendaMessageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    sendaMessageButton.setEnabled(false);


                    if (currentState.equals("new")) {

                        sendChatRequest();
                    }
                    if (currentState.equals("request_sent")) {

                        cancelChatRequest();
                    }
                    if (currentState.equals("request_received")) {

                        AcceptChatRequest();
                    }
                    if(currentState.equals("friends")){

                            removeSpecificContact();
                    }


                }
            });

        } else {
            sendaMessageButton.setVisibility(View.INVISIBLE);


        }
    }

    private void removeSpecificContact() {

        contactsRef.child(currentUserId).child(recieverUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    contactsRef.child(recieverUserId).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                sendaMessageButton.setEnabled(true);
                                currentState = "new";
                                sendaMessageButton.setText("Send MessageAdapter");
                                declineMessageRequestButton.setVisibility(View.INVISIBLE);
                                declineMessageRequestButton.setEnabled(false);


                            }

                        }
                    });


                }


            }
        });

    }

    private void AcceptChatRequest() {

        contactsRef.child(currentUserId).child(recieverUserId).child("contacts").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    contactsRef.child(recieverUserId).child(currentUserId).child("contacts").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                chatRequestRef.child(currentUserId).child(recieverUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    chatRequestRef.child(recieverUserId).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            sendaMessageButton.setEnabled(true);
                                            currentState = "friends";
                                            sendaMessageButton.setText("remove contact");
                                            declineMessageRequestButton.setText(View.INVISIBLE);
                                            declineMessageRequestButton.setEnabled(false);

                                        }
                                    });


                                    }
                                });




                            }


                        }
                    });


                }


            }
        });


    }

    private void cancelChatRequest() {

        chatRequestRef.child(currentUserId).child(recieverUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    chatRequestRef.child(recieverUserId).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                sendaMessageButton.setEnabled(true);
                                currentState = "new";
                                sendaMessageButton.setText("Send MessageAdapter");
                                declineMessageRequestButton.setVisibility(View.INVISIBLE);
                                declineMessageRequestButton.setEnabled(false);


                            }

                        }
                    });


                }


            }
        });


    }

    private void sendChatRequest() {
        chatRequestRef.child(currentUserId).child(recieverUserId)
                .child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    chatRequestRef.child(recieverUserId).child(currentUserId)
                            .child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                sendaMessageButton.setEnabled(true);
                                currentState = "request_sent";
                                sendaMessageButton.setText("cancel chat request");


                            }


                        }
                    });


                }


            }
        });


    }
}
