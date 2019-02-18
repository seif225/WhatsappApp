package com.example.whatsappapp;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<Messages> userMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private String picture;
    public MessageAdapter(List<Messages> userMessagesList,String picture){

        this.userMessagesList=userMessagesList;
        this.picture=picture;
    }


    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_messages_layout,viewGroup,false);
        mAuth = FirebaseAuth.getInstance();


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder viewHolder, int i) {

            String messageUerId=mAuth.getCurrentUser().getUid();
            Messages messages = userMessagesList.get(i);
            String fromUserId=messages.getFrom();
            String fromMessageType = messages.getType();
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(fromUserId);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("image")){

                        String receiverImages= dataSnapshot.child("image").getValue().toString();
                        Picasso.with(viewHolder.itemView.getContext()).load(picture).placeholder(R.drawable.profile_image)
                                .into(viewHolder.receiveProfileImage);

                    }


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if (fromMessageType.equals("text")){


            viewHolder.ReceiverMessageText.setVisibility(View.INVISIBLE);
            viewHolder.receiveProfileImage.setVisibility(View.INVISIBLE);
            viewHolder.senderMessageText.setVisibility(View.INVISIBLE);


            if(fromUserId.equals(messageUerId)){


                viewHolder.ReceiverMessageText.setVisibility(View.VISIBLE);
                viewHolder.receiveProfileImage.setVisibility(View.VISIBLE);
                viewHolder.ReceiverMessageText.setBackgroundResource(R.drawable.sender_messages_layout);
                viewHolder.ReceiverMessageText.setText(messages.getMessage());
                Log.e("get sender MEssage",messages.getMessage());


            }

            else{
                viewHolder.senderMessageText.setVisibility(View.VISIBLE);
                viewHolder.senderMessageText.setBackgroundResource(R.drawable.receiver_messages_layout);
                viewHolder.senderMessageText.setText(messages.getMessage());
                Log.e("get reciver MEssage",messages.getMessage());


            }


        }




    }

    @Override
    public int getItemCount() {
        if (userMessagesList == null){

            return 0;
        }
        return userMessagesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView senderMessageText,ReceiverMessageText;
        public CircleImageView receiveProfileImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMessageText = itemView.findViewById(R.id.sender_message_text);
            receiveProfileImage=itemView.findViewById(R.id.message_profile_image_custom);
            ReceiverMessageText=itemView.findViewById(R.id.receiver_message_text);


        }
    }
}
