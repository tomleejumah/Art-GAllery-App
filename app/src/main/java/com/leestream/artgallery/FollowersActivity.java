package com.leestream.artgallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.leestream.artgallery.Adapters.UserAdapter;
import com.leestream.artgallery.Models.User;

import java.util.ArrayList;
import java.util.List;

public class FollowersActivity extends AppCompatActivity {
    private String ID;
    private String tittle;
    private List<String> idList;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User>mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);

        Intent intent=getIntent();
        ID=intent.getStringExtra("ID");
        tittle=intent.getStringExtra("tittle");

        Toolbar toolbar=findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(tittle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        recyclerView=findViewById(R.id.RcFollowers);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUser=new ArrayList<>();
        userAdapter=new UserAdapter(this,mUser,false);
        recyclerView.setAdapter(userAdapter);

        idList=new ArrayList<>();

        switch (tittle){
            case "followers":
                getFollowers();
                break;
            case "following":
                getFollowing();
                break;
            case "Likes":
                getLikes();
                break;
        }

    }

    private void getFollowers() {
        FirebaseDatabase.getInstance().getReference().child("follow").child(ID).
                child("followers").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        idList.clear();
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()
                        ) {idList.add(dataSnapshot.getKey());
                        }
                        showUsers();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void showUsers() {
        FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUser.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()
                ) {User user=dataSnapshot.getValue(User.class);
                    for (String ID:idList){
                        if (user.getID().equals(ID)) {
                            mUser.add(user);
                        }
                    }
                }userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowing() {
        FirebaseDatabase.getInstance().getReference().child("follow").child(ID).
                child("following").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        idList.clear();
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()
                        ) {idList.add(dataSnapshot.getKey());
                        }
                        showUsers();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getLikes() {
        FirebaseDatabase.getInstance().getReference().child("Likes").child(ID).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        idList.clear();
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()
                        ) {idList.add(dataSnapshot.getKey());
                        }
                        showUsers();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}