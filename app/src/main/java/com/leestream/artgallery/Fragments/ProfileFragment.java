package com.leestream.artgallery.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.leestream.artgallery.Adapters.PostsAdapter;
import com.leestream.artgallery.Adapters.ProfileAdapter;
import com.leestream.artgallery.Adapters.UserAdapter;
import com.leestream.artgallery.EditProfileActivity;
import com.leestream.artgallery.Models.Posts;
import com.leestream.artgallery.Models.User;
import com.leestream.artgallery.R;
import com.leestream.artgallery.SplashScreen;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private ColorStateList def;
    private TextView item1, item2, fullNames, txtUserName1, txtAbout, select;
    private RecyclerView RCviewPictures;
    private ImageView profileFragmentImg;
    private String firstName, lastName, userName, userType,bio;
    private boolean isArtist = false;
    private ProfileAdapter profileAdapter;
    private List<Posts> profilePost = new ArrayList<>();
    private LottieAnimationView loadinglottie;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        item1 = view.findViewById(R.id.item1);
        item2 = view.findViewById(R.id.item2);
        select = view.findViewById(R.id.select);
        fullNames = view.findViewById(R.id.fullNames);
        txtUserName1 = view.findViewById(R.id.txtUserName1);
        txtAbout = view.findViewById(R.id.txtAbout);

        profileFragmentImg = view.findViewById(R.id.profileFragmentImg);
        RCviewPictures = view.findViewById(R.id.RCviewPictures);
        loadinglottie = view.findViewById(R.id.loadinglottie1);

        loadinglottie.setVisibility(View.VISIBLE);

        userType = getContext().getSharedPreferences("UserType", Context.MODE_PRIVATE).getString("UserType", null);

        getUserInfo();

        if (userType.equals("Artist")) {
            item1.setText("MyPosts");
            item2.setText("Saved");
            isArtist = true;

        } else {
            item1.setText("Liked");
            item2.setText("Saved");
            isArtist = false;
        }

        RCviewPictures.hasFixedSize();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        RCviewPictures.setLayoutManager(new GridLayoutManager(getContext(), 3));
        profileAdapter = new ProfileAdapter(requireContext(), profilePost);
        RCviewPictures.setAdapter(profileAdapter);

        FirebaseUser fUser= FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDatabase.getInstance().getReference().child("USERS").child(fUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user =snapshot.getValue(User.class);
                        if (user.getImageUrl().equals("default")){
                            profileFragmentImg.setImageResource(R.mipmap.profile_foreground);
                        }else if (user.getImageUrl().equals("")){
                            profileFragmentImg.setImageResource(R.mipmap.profile_foreground);
                        }else {
                            Picasso.get().load(user.getImageUrl()).into(profileFragmentImg);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        def = item2.getTextColors();
        item1.setOnClickListener(this);
        item2.setOnClickListener(this);

        view.findViewById(R.id.btnLogout).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(requireContext(), SplashScreen.class).
                    setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            getActivity().finish();
        });
        view.findViewById(R.id.btnEdit).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EditProfileActivity.class);
            intent.putExtra("fName", firstName);
            intent.putExtra("lName", lastName);
            intent.putExtra("uName", userName);
            intent.putExtra("Bio", bio);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.item1) {
            select.animate().x(0).setDuration(100);
            item1.setTextColor(Color.WHITE);
            item2.setTextColor(def);
            if (isArtist) {
                readMyPost();
            } else {
                readLiked();
            }
        } else if (v.getId() == R.id.item2) {
            item1.setTextColor(def);
            item2.setTextColor(Color.WHITE);
            int size = item2.getWidth();
            select.animate().x(size).setDuration(100);
            readSaved();
        }
    }

    private void getUserInfo() {
        String UID = getContext().getSharedPreferences("UID", Context.MODE_PRIVATE).getString("myUID", null);
        if (UID.isEmpty()) {
            UID = String.valueOf(FirebaseAuth.getInstance().getCurrentUser());
        }

        FirebaseDatabase.getInstance().getReference().child("USERS").child(UID).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (user.getImageUrl().equals("default")) {
                            profileFragmentImg.setImageResource(R.mipmap.profile_foreground);
                        } else if (user.getImageUrl().equals("")) {
                            profileFragmentImg.setImageResource(R.mipmap.profile_foreground);
                        } else {
                            Picasso.get().load(user.getImageUrl()).into(profileFragmentImg);
                        }

                        firstName = user.getFirstName();
                        lastName = user.getLastName();
                        userName = user.getUserName();
                        bio = user.getBio();

                        fullNames.setText(firstName + " " + lastName);
                        txtUserName1.setText("@" + userName);
                        txtAbout.setText(bio);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void readSaved() {
        FirebaseDatabase.getInstance().getReference().child("SAVED").
                child(FirebaseAuth.getInstance().getUid()).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Posts myPosts = dataSnapshot.getValue(Posts.class);
                                profilePost.add(myPosts);
                            }
                        } else {
                            loadinglottie.setVisibility(View.GONE);
                        }
                        profileAdapter.notifyDataSetChanged();
                        loadinglottie.setVisibility(View.GONE);
                        RCviewPictures.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void readLiked() {
        FirebaseDatabase.getInstance().getReference().child("LIKED").
                child(FirebaseAuth.getInstance().getUid()).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Posts myPosts = dataSnapshot.getValue(Posts.class);
                                profilePost.add(myPosts);
                            }
                        } else {
                            loadinglottie.setVisibility(View.GONE);
                        }
                        profileAdapter.notifyDataSetChanged();
                        loadinglottie.setVisibility(View.GONE);
                        RCviewPictures.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void readMyPost() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference().child("POSTS");
        Query query = postsRef.orderByChild("PublisherID").equalTo(String.valueOf(firebaseUser));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Posts> filteredPosts = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        // Retrieve each post and add to the filtered list
                        Posts post = postSnapshot.getValue(Posts.class);
                        filteredPosts.add(post);
                    }
                } else {
                    loadinglottie.setVisibility(View.GONE);
                }
                loadinglottie.setVisibility(View.GONE);
                RCviewPictures.setVisibility(View.VISIBLE);
                profilePost.clear();
                profilePost.addAll(filteredPosts);
                profileAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors
                Log.e("Firebase", "Error fetching posts: " + databaseError.getMessage());
            }
        });
    }
}