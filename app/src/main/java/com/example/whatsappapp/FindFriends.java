package com.example.whatsappapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriends extends AppCompatActivity {
    private Toolbar findFriendsToolBar;
    private RecyclerView findFriendsRecyclerView;
    private DatabaseReference userRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);


            findFriendsRecyclerView=findViewById(R.id.find_friends_recycler_view);
            findFriendsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            findFriendsToolBar = findViewById(R.id.find_friends_tool_bar);
            setSupportActionBar(findFriendsToolBar);


            //getSupportActionBar().setTitle("find friends");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("find friends");

                userRef= FirebaseDatabase.getInstance().getReference().child("users");




    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(userRef,Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts,FindFriendsViewHolder> adapter
                = new FirebaseRecyclerAdapter<Contacts, FindFriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendsViewHolder holder, final int position, @NonNull Contacts model) {
                holder.parent.setVisibility(View.VISIBLE);
                holder.userName.setText(model.getName());
                holder.userStatus.setText(model.getStatus());
                Picasso.with(getBaseContext()).load(model.getImage()).into(holder.profileImage);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visit_user_id=getRef(position).getKey();
                        Intent profileIntent = new Intent(FindFriends.this,ProfileActivity.class);
                        profileIntent.putExtra("visit_user_id",visit_user_id);
                        startActivity(profileIntent);
                    }
                });


            }

            @NonNull
            @Override
            public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout,viewGroup,false);

                return new FindFriendsViewHolder(view);
            }
        };

                findFriendsRecyclerView.setAdapter(adapter);
                adapter.startListening();

    }

    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder{
            TextView userName,userStatus;
            CircleImageView profileImage;
            View parent;


        public FindFriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            userName=itemView.findViewById(R.id.user_name_tv);
            userStatus=itemView.findViewById(R.id.user_status);
            profileImage=itemView.findViewById(R.id.user_profile_picture_inFind);
            parent = itemView;


        }
    }



}
