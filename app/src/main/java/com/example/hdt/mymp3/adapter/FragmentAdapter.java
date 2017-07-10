package com.example.hdt.mymp3.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.hdt.mymp3.fragment.FavoriteFragment;
import com.example.hdt.mymp3.fragment.OfflineFragment;
import com.example.hdt.mymp3.fragment.OnlineFragment;

import java.util.ArrayList;
import java.util.List;

public class FragmentAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragments;

    public FragmentAdapter(Context context, FragmentManager fm) {
        super(fm);
        mFragments = new ArrayList<>();
        mFragments.add(new OfflineFragment(context));
        mFragments.add(new OnlineFragment());
        mFragments.add(new FavoriteFragment());
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}