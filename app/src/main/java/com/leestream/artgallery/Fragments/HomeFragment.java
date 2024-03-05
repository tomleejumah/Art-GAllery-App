package com.leestream.artgallery.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.AnimationTypes;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.leestream.artgallery.Adapters.PostsAdapter;
import com.leestream.artgallery.Adapters.UserAdapter;
import com.leestream.artgallery.MainActivity;
import com.leestream.artgallery.Models.Posts;
import com.leestream.artgallery.Models.User;
import com.leestream.artgallery.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomeFragment extends Fragment implements View.OnClickListener {
    private RecyclerView RcPosts;
    private PostsAdapter postAdapter;
    private List<Posts> posts;
    private List<String> followingList;
    private ImageSlider imageSlider;
    private final ArrayList<SlideModel> slideModels = new ArrayList<>();
    private final List<String> firebaseImageUrls = new ArrayList<>();
    private TextView sculp, arch, lit, film, paint, music, all, select,txtUserN;
    private int defaultTextColor;
    private String category;
    private UserAdapter userAdapter;
    private MainActivity mainActivity;
    private static final String TAG = "HomeFragment";
    private LottieAnimationView loadinglottie;
    private CircleImageView profileCircleImageView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

         txtUserN = view.findViewById(R.id.txtUserN);
        profileCircleImageView = view.findViewById(R.id.profileCircleImageView);
        loadinglottie = view.findViewById(R.id.loadinglottie);

        loadinglottie.setVisibility(View.VISIBLE);

        sculp = view.findViewById(R.id.sculp);
        arch = view.findViewById(R.id.arch);
        lit = view.findViewById(R.id.lit);
        film = view.findViewById(R.id.film);
        paint = view.findViewById(R.id.paint);
        music = view.findViewById(R.id.music);
        all = view.findViewById(R.id.all);
        select = view.findViewById(R.id.select);

        FirebaseUser fUser= FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance().getReference().child("USERS").child(fUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user =snapshot.getValue(User.class);
                        if (user.getImageUrl().equals("default")){
                            profileCircleImageView.setImageResource(R.mipmap.profile_foreground);
                        }else if (user.getImageUrl().equals("")){
                            profileCircleImageView.setImageResource(R.mipmap.profile_foreground);
                        }else {
                            Picasso.get().load(user.getImageUrl()).into(profileCircleImageView);
                        }
                        txtUserN.setText("Hello " + user.getUserName());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        defaultTextColor = ContextCompat.getColor(requireContext(), android.R.color.black);

        imageSlider = view.findViewById(R.id.image_slider);

        ArrayList<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel(R.drawable.music_art, "Largest Online Art store", ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.africa_sketch, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.removebg, "Enjoy Up-to 60% Offers", ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.handdrawn, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.music_art, "All types of arts available", ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.portrait_man, "Capture your Arts and share", ScaleTypes.FIT));

        imageSlider.setImageList(slideModels, ScaleTypes.FIT);
        imageSlider.setOnClickListener(v -> {
            imageSlider.stopSliding();
        });

        imageSlider.setSlideAnimation(AnimationTypes.ZOOM_OUT);
//        imageSlider.setImageList(slideModels,ScaleTypes.FIT);

        RcPosts = view.findViewById(R.id.rlPosts);

        RcPosts.hasFixedSize();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        RcPosts.setLayoutManager(linearLayoutManager);
        posts = new ArrayList<>();
        postAdapter = new PostsAdapter(requireContext(), posts);
        RcPosts.setAdapter(postAdapter);

        followingList = new ArrayList<>();

        checkFollowingUsers();

        sculp.setOnClickListener(this);
        arch.setOnClickListener(this);
        lit.setOnClickListener(this);
        paint.setOnClickListener(this);
        film.setOnClickListener(this);
        music.setOnClickListener(this);
        all.setOnClickListener(this);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(requireContext())
                        .setMessage("Are you sure you want to exit?")
                        .setIcon(android.R.drawable.stat_notify_error)
                        .setTitle("Alert")
                        .setPositiveButton("YES", (dialog, which) -> {
                            // code to exit
                            requireActivity().finish();
                        })
                        .setNegativeButton("NO", (dialog, which) -> {

                        })
                        .show();
            }
        });
        return view;
    }

    private void checkFollowingUsers() {
        FirebaseDatabase.getInstance().getReference().child("follow").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        followingList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            followingList.add(dataSnapshot.getKey());
                        }
                        readPosts();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void readPosts() {
        FirebaseDatabase.getInstance().getReference().child("POSTS").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        posts.clear();
                        firebaseImageUrls.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Posts myPosts = dataSnapshot.getValue(Posts.class);
                            String images = myPosts.getImageUrl();
                            firebaseImageUrls.add(images);
                            posts.add(myPosts);
//                            for (String id:followingList) {
//                                if (myPosts.getPublisher().equals(id)){
//                                    posts.add(myPosts);
//                            Log.d(TAG, "onDataChange: "+ myPosts);
//                                }
//                            }
                        }
                        postAdapter.notifyDataSetChanged();
                        loadinglottie.setVisibility(View.GONE);
                        RcPosts.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sculp) {
            updateTextViewColors(sculp);
            int size = arch.getWidth() * 6;
            select.animate().x(size).setDuration(100);
            category = "Sculpture";
            filterData(category);
        } else if (v.getId() == R.id.arch) {
            updateTextViewColors(arch);
            int size = arch.getWidth() * 5;
            select.animate().x(size).setDuration(100);
            category = "Architecture";
            filterData(category);
        } else if (v.getId() == R.id.lit) {
            updateTextViewColors(lit);
            int size = arch.getWidth() * 4;
            select.animate().x(size).setDuration(100);
            category = "Literature";
            filterData(category);
        } else if (v.getId() == R.id.film) {
            updateTextViewColors(film);
            int size = arch.getWidth() * 3;
            select.animate().x(size).setDuration(100);
            category = "Film";
            filterData(category);
        } else if (v.getId() == R.id.paint) {
            updateTextViewColors(paint);
            int size = arch.getWidth() * 2;
            select.animate().x(size).setDuration(100);
            category = "Drawings";
            filterData(category);
        } else if (v.getId() == R.id.music) {
            updateTextViewColors(music);
            int size = arch.getWidth();
            select.animate().x(size).setDuration(100);
            category = "Music";
            filterData(category);
        } else if (v.getId() == R.id.all) {
            updateTextViewColors(all);
            select.animate().x(0).setDuration(100);
            readPosts();
        }
    }

    private void filterData(String category) {
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference().child("POSTS");
        Query query = postsRef.orderByChild("Category").equalTo(category);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Posts> filteredPosts = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    // Retrieve each post and add to the filtered list
                    Posts post = postSnapshot.getValue(Posts.class);
                    filteredPosts.add(post);
                }
                // Update the RecyclerView adapter with the filtered data
                posts.clear(); // Clear current dataset
                posts.addAll(filteredPosts); // Add filtered posts
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors
                Log.e("Firebase", "Error fetching posts: " + databaseError.getMessage());
            }
        });
    }

    private void updateTextViewColors(TextView clickedTextView) {
        sculp.setTextColor(defaultTextColor);
        arch.setTextColor(defaultTextColor);
        lit.setTextColor(defaultTextColor);
        film.setTextColor(defaultTextColor);
        paint.setTextColor(defaultTextColor);
        music.setTextColor(defaultTextColor);
        all.setTextColor(defaultTextColor);

        clickedTextView.setTextColor(Color.WHITE);
    }
}