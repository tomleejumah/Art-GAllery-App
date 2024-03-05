package com.leestream.artgallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.leestream.artgallery.Adapters.PostsAdapter;
import com.leestream.artgallery.Models.Posts;
import com.leestream.artgallery.Models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserPostActivity extends AppCompatActivity {
    private CircleImageView userImage;
    private RecyclerView UserPostItems;
    private MaterialToolbar topAppBar;
    private List<Posts> posts;
    private PostsAdapter postAdapter;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_post);

        topAppBar = findViewById(R.id.topAppBar);
        UserPostItems = findViewById(R.id.UserPostItems);
        userImage = findViewById(R.id.userImage);

         userId= getSharedPreferences("USER",MODE_PRIVATE).getString("userID","null");

        FirebaseDatabase.getInstance().getReference().child("USERS").child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user =snapshot.getValue(User.class);
                        if (user.getImageUrl().equals("default")){
                            userImage.setImageResource(R.mipmap.profile_foreground);
                        }else if (user.getImageUrl().equals("")){
                            userImage.setImageResource(R.mipmap.profile_foreground);
                        }else {
                            Picasso.get().load(user.getImageUrl()).into(userImage);
                        }
                        topAppBar.setTitle(user.getUserName());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        UserPostItems.hasFixedSize();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        UserPostItems.setLayoutManager(linearLayoutManager);
        posts = new ArrayList<>();
        postAdapter = new PostsAdapter(this, posts);
        UserPostItems.setAdapter(postAdapter);

        loadItems();
    }

    private  void  loadItems(){
        FirebaseDatabase.getInstance().getReference().child("POSTS").orderByChild("PublisherID").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        posts = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Posts post = dataSnapshot.getValue(Posts.class);
                            if (post != null) {
                                posts.add(post);
                            }
                        }
                        postAdapter = new PostsAdapter(UserPostActivity.this, posts);
                        UserPostItems.setAdapter(postAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        // Set up RecyclerView
//        UserPostItems.setLayoutManager(new LinearLayoutManager(this));
    }

}