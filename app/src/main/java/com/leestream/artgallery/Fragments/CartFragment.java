package com.leestream.artgallery.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.leestream.artgallery.Adapters.CartAdapter;
import com.leestream.artgallery.MainActivity;
import com.leestream.artgallery.Models.Cart;
import com.leestream.artgallery.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CartFragment extends Fragment {
    private LottieAnimationView myLottie;
    private LinearLayout LLCart;

    private List<Cart> cartList;
    private CartAdapter cartAdapter;
    private RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

//        MaterialToolbar materialToolbar = view.findViewById(R.id.toolbar2);
//        ((AppCompatActivity) requireActivity()).setSupportActionBar(materialToolbar);
//        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("");
//        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        materialToolbar.setNavigationOnClickListener(v -> {
//            Intent intent = new Intent(requireContext(), MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            startActivity(intent);
//        });

        myLottie = view.findViewById(R.id.myLottie);
        LLCart = view.findViewById(R.id.LLCart);

        recyclerView = view.findViewById(R.id.rcCart);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartList = new ArrayList<>();
        cartAdapter = new CartAdapter(cartList, getContext());
        recyclerView.setAdapter(cartAdapter);

        DatabaseReference cartItemsRef = FirebaseDatabase.getInstance().
                getReference().child("CartItems").child(firebaseUser.getUid());

        cartItemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    readCartItems();
                    recyclerView.setVisibility(View.VISIBLE);
                    myLottie.setVisibility(View.GONE);
                    LLCart.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    myLottie.setVisibility(View.VISIBLE);
                    LLCart.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }
    private void readCartItems() {
        FirebaseDatabase.getInstance().getReference().child("CartItems")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot cartItemSnapshot : snapshot.getChildren()) {
                            String userName = cartItemSnapshot.child("UserName").getValue(String.class);
                            String desc = cartItemSnapshot.child("text").getValue(String.class);
                            String postID = cartItemSnapshot.child("postID").getValue(String.class);
                            String price = cartItemSnapshot.child("price").getValue(String.class);
                            String imageUrl = cartItemSnapshot.child("imageUrl").getValue(String.class);
                            String publisherID = cartItemSnapshot.child("PublisherID").getValue(String.class);

                            // Create a new CartItem object with the retrieved data
                            Cart cartItem = new Cart(imageUrl, postID, publisherID, desc, userName, price);

                            cartList.add(cartItem);

//                            cartList.add(dataSnapshot.getValue(Cart.class));
                        }
                        Collections.reverse(cartList);
                        cartAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(requireContext(), "Error while fetching Cart Items", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}