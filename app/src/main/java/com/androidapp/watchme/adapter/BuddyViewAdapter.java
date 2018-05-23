package com.androidapp.watchme.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.androidapp.watchme.R;
import com.androidapp.watchme.fragment.BuddyFragment;

import java.util.ArrayList;


public class BuddyViewAdapter extends FragmentPagerAdapter {
    private Context _context;
    private int _count;
    private ArrayList<Fragment> fragmentArrayList;

    public BuddyViewAdapter(Context context, FragmentManager fragmentManager, int count, ArrayList<String> userEmailList) {
        super(fragmentManager);
        _context = context;
        _count = count;
        fragmentArrayList = new ArrayList<>();
        for (int i = 0; i < _count; i ++) {
            BuddyFragment buddyFragment = new BuddyFragment();
            Bundle bundle = new Bundle();
            bundle.putString(_context.getString(R.string.email), userEmailList.get(i));
            buddyFragment.setArguments(bundle);
            fragmentArrayList.add(buddyFragment);
        }
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new Fragment();
        fragment = fragmentArrayList.get(position);

        return fragment;
    }

    @Override
    public int getCount() {
        return _count;
    }

}
