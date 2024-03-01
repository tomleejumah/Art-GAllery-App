package com.leestream.artgallery.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.leestream.artgallery.Adapters.CartAdapter;
import com.leestream.artgallery.Adapters.CartNotificationVPAdapter;
import com.leestream.artgallery.MainActivity;
import com.leestream.artgallery.Models.Cart;
import com.leestream.artgallery.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CartHostFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_host_cart, container, false);
        ViewPager myViewPager = view.findViewById(R.id.viewPager);
        TabLayout myTabLayout = view.findViewById(R.id.tabLayout);

        myTabLayout.setupWithViewPager(myViewPager);
        CartNotificationVPAdapter viewPagerAdapter = new CartNotificationVPAdapter(getChildFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPagerAdapter.addFragment(new CartFragment(), "Cart");
        viewPagerAdapter.addFragment(new NotificationFragment(), "Notification");
        myViewPager.setAdapter(viewPagerAdapter);
        return view;
    }
}