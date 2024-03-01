package com.leestream.artgallery.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class CartNotificationVPAdapter extends FragmentPagerAdapter {
    private final ArrayList<Fragment> fragmentArrayList=new ArrayList<>();
    private final ArrayList<String>fragmentTittle=new ArrayList<>();

    public CartNotificationVPAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentArrayList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentArrayList.size();
    }
    public void addFragment(Fragment fragment,String tittle){
        fragmentArrayList.add(fragment);
        fragmentTittle.add(tittle);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTittle.get(position);
    }
}
