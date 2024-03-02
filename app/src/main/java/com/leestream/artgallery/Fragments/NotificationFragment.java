package com.leestream.artgallery.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.leestream.artgallery.Adapters.CartAdapter;
import com.leestream.artgallery.Adapters.NotificationAdapter;
import com.leestream.artgallery.Models.Notification;
import com.leestream.artgallery.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationFragment extends Fragment {
    private ImageView myLottieNotif;
    private LinearLayout LLNotif;
    private List<Notification> notificationList;
    private NotificationAdapter notificationAdapter;
    private RecyclerView RcNotifications;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        myLottieNotif = view.findViewById(R.id.myLottieNotif);
        LLNotif = view.findViewById(R.id.LLNotif);

        RcNotifications = view.findViewById(R.id.rcNotif);
        RcNotifications.setHasFixedSize(true);
        RcNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(notificationList, getContext());
        RcNotifications.setAdapter(notificationAdapter);

        DatabaseReference cartItemsRef = FirebaseDatabase.getInstance().
                getReference().child("Notification").child(firebaseUser.getUid());

        cartItemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    readNotification();
                    RcNotifications.setVisibility(View.VISIBLE);
                    myLottieNotif.setVisibility(View.GONE);
                    LLNotif.setVisibility(View.GONE);
                } else {
                    RcNotifications.setVisibility(View.GONE);
                    myLottieNotif.setVisibility(View.VISIBLE);
                    LLNotif.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void readNotification() {
        FirebaseDatabase.getInstance().getReference().child("Notification")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            notificationList.add(dataSnapshot.getValue(Notification.class));
                        }
                        Collections.reverse(notificationList);
                        notificationAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Toast.makeText(requireContext(), "Error while fetching Notifications", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}