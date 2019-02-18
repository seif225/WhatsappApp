package com.example.whatsappapp;

import android.content.Context;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String messageReceiverId,messageRecieverName,messageReceiverPicture,currentUserId;
    private TextView userName,userLastSeen;
    private CircleImageView userImage;
    private Toolbar chatToolbar;
    private ImageButton sendButton;
    private EditText messageInputText;
    private FirebaseAuth mAuth;
    private DatabaseReference rootref;
    private final List<Messages> messagesList = new ArrayList<Messages>();
    private MessageAdapter adapter;
    private RecyclerView recyclerView;
private RelativeLayout relative;
private int flag=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        rootref= FirebaseDatabase.getInstance().getReference();
        messageReceiverId = getIntent().getExtras().get("visit_user_id").toString();
        messageRecieverName=getIntent().getExtras().get("visit_user_name").toString();
        messageReceiverPicture = getIntent().getExtras().get("visit_user_image").toString();

        InitializeControllers();
        userName.setText(messageRecieverName);
        Picasso.with(this).load(messageReceiverPicture).placeholder(R.drawable.profile_image).into(userImage);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

    }

    private void sendMessage() {
        String message = messageInputText.getText().toString();
        if (TextUtils.isEmpty(message))
        {
            messageInputText.setError("you can't send an empty message");
            messageInputText.requestFocus();
        }

        else
            {
                String messageSenderRef = "Messages/"+currentUserId+"/" +messageReceiverId;
                String messageRecieverRef = "Messages/"+messageReceiverId+"/" +currentUserId;
                DatabaseReference userMessageKeyRef = rootref.child("messages").child(currentUserId).child(messageReceiverId).push();
                String messagePushId= userMessageKeyRef.getKey();
                Map messageBody= new HashMap();
                messageBody.put("message",message);
                messageBody.put("type","text");
                messageBody.put("from",messageReceiverId);
                messageBody.put("to",currentUserId);

                Map messageBodyDetails = new HashMap();
                messageBodyDetails.put(messageSenderRef + "/" + messagePushId,messageBody);
                messageBodyDetails.put(messageRecieverRef + "/" + messagePushId,messageBody);

                rootref.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        if(task.isSuccessful())
                        {


                        }

                        else

                            {
                                Toast.makeText(ChatActivity.this, "Error , MessageAdapter is not sent , try again", Toast.LENGTH_SHORT).show();


                            }
                        messageInputText.setText("");
                    }
                });




            }
    }

    private void InitializeControllers() {

        chatToolbar = findViewById(R.id.chat_toolbar_bar);
        setSupportActionBar(chatToolbar);
        ActionBar actionBar= getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView =layoutInflater.inflate(R.layout.custom_layout_bar ,null);
        actionBar.setCustomView(actionBarView);
        userImage =findViewById(R.id.custom_profile_image);
        userName=findViewById(R.id.customr_profile_name);
        userLastSeen= findViewById(R.id.custom_user_last_seen);
        sendButton= findViewById(R.id.chat_activity_send_button);
        messageInputText = findViewById(R.id.chat_activity_et);
        adapter=new MessageAdapter(messagesList,messageReceiverPicture);
        recyclerView = findViewById(R.id.chat_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
//flag = 1;

Log.e("onCreate",flag + "");
    relative=findViewById(R.id.relativelol);

    }

    @Override
    protected void onStart() {
        super.onStart();

        rootref.child("Messages").child(currentUserId).child(messageReceiverId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages messages = dataSnapshot.getValue(Messages.class);
                messagesList.add(messages);
                adapter.notifyDataSetChanged();
               // recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());

                //flag = 1;
                        test();
               // Log.e("Onstart",flag+"");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {



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

    private void test() {
        relative.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

                recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
                Log.e("OnstartWTF", flag + "");
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("OnResume1",flag+"");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("OnDestroy1",flag+"");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("OnStop1",flag+"");
        flag = 0;
        Log.e("OnStop",flag+"");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("OnRestart1",flag+"");
        flag=0;
        Log.e("OnRestart1",flag+"");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("OnPause1",flag+"");
        flag=0;
        Log.e("OnPause",flag+"");
    }
}
