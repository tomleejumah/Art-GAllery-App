package com.leestream.artgallery.Fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leestream.artgallery.R;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    ColorStateList def;
    TextView item1,item2;
    TextView select;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        item1 = view.findViewById(R.id.item1);
        item2 = view.findViewById(R.id.item2);
        select = view.findViewById(R.id.select);
        def = item2.getTextColors();
        item1.setOnClickListener(this);
        item2.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.item1){
            select.animate().x(0).setDuration(100);
            item1.setTextColor(Color.WHITE);
            item2.setTextColor(def);
        } else if (v.getId() == R.id.item2) {
            item1.setTextColor(def);
            item2.setTextColor(Color.WHITE);
            int size = item2.getWidth();
            select.animate().x(size).setDuration(100);
        }
    }
}