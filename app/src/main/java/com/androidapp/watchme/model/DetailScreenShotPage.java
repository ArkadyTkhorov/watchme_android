package com.androidapp.watchme.model;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.androidapp.watchme.R;
import com.squareup.picasso.Picasso;


public class DetailScreenShotPage extends LinearLayout {
    Context mContext;

    ImageView screenshotImage;

    public DetailScreenShotPage(Context context) {
        super(context);

        init(context);
    }

    public DetailScreenShotPage(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init(Context context) {
        mContext = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.detail_screenshot_page, this, true);

        screenshotImage = (ImageView) findViewById(R.id.screenshotImageView);

    }

    public void setScreenshotImage(String url) {
        Picasso.with(mContext).load(url).placeholder(R.drawable.unselected_background).fit().into(screenshotImage);
    }

}
