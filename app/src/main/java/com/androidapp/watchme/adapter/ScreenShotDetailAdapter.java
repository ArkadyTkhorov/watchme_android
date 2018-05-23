package com.androidapp.watchme.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.androidapp.watchme.model.DetailScreenShotPage;

import java.util.ArrayList;

public class ScreenShotDetailAdapter extends PagerAdapter {

    private Context mContext;
    private ArrayList<String> mItems;

    /**
     * @param context
     */
    public ScreenShotDetailAdapter(Context context, ArrayList<String> items ) {
        mContext = context;
        mItems = items;
    }

    public int getCount() {
        return mItems.size();
    }

    public Object instantiateItem(ViewGroup container, int position) {
        // create a instance of the page and set data
        DetailScreenShotPage page = new DetailScreenShotPage(mContext);
        page.setScreenshotImage(mItems.get(position));

//        container.addView(page, position);
        container.addView(page, 0);

        return page;
    }

    public void destroyItem(ViewGroup container, int position, Object view) {
//        container.removeView((View)view);
    }

    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

}