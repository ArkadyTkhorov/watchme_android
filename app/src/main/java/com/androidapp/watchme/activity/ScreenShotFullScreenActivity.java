package com.androidapp.watchme.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.androidapp.watchme.R;
import com.androidapp.watchme.adapter.ScreenShotDetailAdapter;

import java.util.ArrayList;

public class ScreenShotFullScreenActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ScreenShotDetailAdapter screenShotDetailAdapter;
    private ArrayList<String> screenshotUrlList;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_shot_full_screen);

        index = getIntent().getIntExtra(getString(R.string.index), 0);
        screenshotUrlList = getIntent().getStringArrayListExtra(getString(R.string.screen_shot_list));

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        screenShotDetailAdapter = new ScreenShotDetailAdapter(this, screenshotUrlList);
        viewPager.setAdapter(screenShotDetailAdapter);
        viewPager.setOffscreenPageLimit(10);
        screenShotDetailAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(index);
    }
}
