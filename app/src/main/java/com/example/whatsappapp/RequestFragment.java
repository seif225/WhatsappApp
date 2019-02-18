package com.example.whatsappapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;


public class RequestFragment extends Fragment {
    private View requestFragmentView;
    private RecyclerView recyclerView;
    private DatabaseReference chatRequestRef, userRef, contactsRef;
    private FirebaseAuth mAuth;
    private String currentUserId;

    public RequestFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        requestFragmentView = inflater.inflate(R.layout.fragment_request, container, false);
        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("chat requests");
        userRef = FirebaseDatabase.getInstance().getReference().child("users");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("contacts");
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        recyclerView = requestFragmentView.findViewById(R.id.chat_request_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requestFragmentView.getContext()));

        return requestFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(chatRequestRef.child(currentUserId), Contacts.class).build();
        FirebaseRecyclerAdapter<Contacts, RequestVieHolder> adapter = new FirebaseRecyclerAdapter<Contacts, RequestVieHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RequestVieHolder holder, int position, @NonNull final Contacts model) {

                holder.acceptButton.setVisibility(View.VISIBLE);
                holder.refuseButton.setVisibility(View.VISIBLE);
                //holder.parent.setVisibility(View.GONE);
                final String list_user_id = getRef(position).getKey();
                final DatabaseReference getTypeRef = getRef(position).child("request_type").getRef();
                getTypeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            String type = dataSnapshot.getValue().toString();

                            if (type.equals("received")) {
                                holder.parent.setVisibility(View.VISIBLE);

                                userRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild("image")) {
                                            final String requestProfilePicture = dataSnapshot.child("image").getValue().toString();
                                            final String requestUserName = dataSnapshot.child("name").getValue().toString();
                                            final String requestUserStatus = dataSnapshot.child("status").getValue().toString();
                                            // holder.parent.setVisibility(View.VISIBLE);
                                            holder.userNameTv.setText(requestUserName);
                                            holder.userStatusTv.setText(requestUserStatus);
                                            Picasso.with(getContext()).load(requestProfilePicture).into(holder.userProfilePicture);
                                        } else {

                                            final String requestUserName = dataSnapshot.child("name").getValue().toString();
                                            final String requestUserStatus = dataSnapshot.child("status").getValue().toString();
                                            holder.userNameTv.setText(requestUserName);
                                            holder.userStatusTv.setText(requestUserStatus);

                                        }

                                        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                contactsRef.child(currentUserId).child(list_user_id).child("contact").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                   contactsRef.child(list_user_id).child(currentUserId).child("contact").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                       @Override
                                                       public void onComplete(@NonNull Task<Void> task) {

                                                           chatRequestRef.child(currentUserId).child(list_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                               @Override
                                                               public void onComplete(@NonNull Task<Void> task) {
                                                              chatRequestRef.child(list_user_id).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                  @Override
                                                                  public void onComplete(@NonNull Task<Void> task) {

                                                                      if (task.isSuccessful()){
                                                                          Toast.makeText(getContext(), "contact has been added", Toast.LENGTH_SHORT).show();

                                                                      }
                                                                  }
                                                              });



                                                               }
                                                           });



                                                       }
                                                   });


                                                    }
                                                });


                                            }
                                        });

                                        holder.refuseButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                chatRequestRef.child(currentUserId).child(list_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        chatRequestRef.child(list_user_id).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if (task.isSuccessful()){
                                                                    Toast.makeText(getContext(), "contact has been added", Toast.LENGTH_SHORT).show();

                                                                }
                                                            }
                                                        });



                                                    }
                                                });

                                            }
                                        });



                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @NonNull
            @Override
            public RequestVieHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
                RequestVieHolder holder = new RequestVieHolder(view);
                return holder;

            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();


    }

    public static class RequestVieHolder extends RecyclerView.ViewHolder {
        TextView userNameTv, userStatusTv;
        CircleImageView userProfilePicture;
        Button acceptButton, refuseButton;
        View parent;

        public RequestVieHolder(@NonNull View itemView) {
            super(itemView);
            userNameTv = itemView.findViewById(R.id.user_name_tv);
            userStatusTv = itemView.findViewById(R.id.user_status);
            userProfilePicture = itemView.findViewById(R.id.user_profile_picture_inFind);
            acceptButton = itemView.findViewById(R.id.accept);
            refuseButton = itemView.findViewById(R.id.refuse);
            parent = itemView;

        }


    }


}
