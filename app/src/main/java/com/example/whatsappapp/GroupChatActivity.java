package com.example.whatsappapp;

import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton sendMessageButton;
    private EditText input;
    private ScrollView mScrollView;
    private TextView displayTextMessages;
    private String currentGroupName, currentUserId,currentUserName,currentDate,currentTime;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef,groupNameRef,groupMessageKeyRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        currentGroupName=getIntent().getExtras().get("groupName").toString();
        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        userRef= FirebaseDatabase.getInstance().getReference().child("users");
        groupNameRef=FirebaseDatabase.getInstance().getReference().child("groups").child(currentGroupName);
        initializeFields();
        getUserInfo();
      //  Toast.makeText(this, currentGroupName, Toast.LENGTH_SHORT).show();

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMessageInfoToDatabase();
                input.setText("");
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        groupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.exists() ){

                    DisplayMessages(dataSnapshot);
                }


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.exists() ){

                        DisplayMessages(dataSnapshot);
                    }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void DisplayMessages(DataSnapshot dataSnapshot) {

        Iterator iterator = dataSnapshot.getChildren().iterator();

        while(iterator.hasNext()){

            String chatDate = ((DataSnapshot)iterator.next()).getValue().toString() ;
            String chatMessage = ((DataSnapshot)iterator.next()).getValue().toString() ;
            String chatName = ((DataSnapshot)iterator.next()).getValue().toString() ;
            String chatTime = ((DataSnapshot)iterator.next()).getValue().toString() ;

            displayTextMessages.append(chatName + ": \n" +chatMessage + "\n" + chatTime  + "\n" +"\n");
            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);


        }


    }

    private void saveMessageInfoToDatabase() {

            String message=input.getText().toString();
            String messageKey = groupNameRef.push().getKey();

            if(TextUtils.isEmpty(message)){

                input.setError("you can't send an empty message");

            }

            else{
                //getting date
                Calendar calForDate= Calendar.getInstance();
                SimpleDateFormat currentDateFormat=new SimpleDateFormat("MM dd,yyyy");
                currentDate=currentDateFormat.format(calForDate.getTime());
                //getting time
                Calendar callForTime = Calendar.getInstance();
                SimpleDateFormat CurrentTimeFormat = new SimpleDateFormat("hh:mm a");
                currentTime=CurrentTimeFormat.format(callForTime.getTime());

               /* HashMap<String , Object> groupMessageKey = new HashMap<>();
                    groupNameRef.updateChildren(groupMessageKey);*/

                    groupMessageKeyRef = groupNameRef.child(messageKey);
                    HashMap<String , Object> messageInfoMap = new HashMap<>();
                    messageInfoMap.put("name",currentUserName);
                    messageInfoMap.put("message",message);
                    messageInfoMap.put("date",currentDate);
                    messageInfoMap.put("time",currentTime);

                    groupMessageKeyRef.updateChildren(messageInfoMap);

            }

    }

    private void getUserInfo() {
        userRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    currentUserName=dataSnapshot.child("name").getValue().toString();

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void initializeFields() {

        mToolbar=findViewById(R.id.group_chat_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(currentGroupName);
        sendMessageButton=findViewById(R.id.send_message_btn);
        input=findViewById(R.id.input_group_message);
        displayTextMessages=findViewById(R.id.group_chat_message_display);
        mScrollView=findViewById(R.id.group_chat_scroll_view);


    }
}
