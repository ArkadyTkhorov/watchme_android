package com.androidapp.watchme.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.androidapp.watchme.R;
import com.androidapp.watchme.activity.ScreenShotFullScreenActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ScreenshotDataAdapter extends RecyclerView.Adapter<ScreenshotDataAdapter.CustomViewHolder> {
    private LayoutInflater mInflater;
    private ArrayList<String> mItems;
    private Activity mActivity;

    public ScreenshotDataAdapter(Activity activity, ArrayList<String> data) {
        mInflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mItems = data;
        mActivity = activity;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final View v = layoutInflater.inflate(R.layout.list_item_screeenshot, parent, false);
        return new CustomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        if (mItems.get(position) != null && !mItems.get(position).isEmpty()) {
            Picasso.with(mActivity).load(mItems.get(position)).placeholder(R.drawable.unselected_background).fit().into(holder.screenshotImageView);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView screenshotImageView;

        CustomViewHolder(View itemView) {
            super(itemView);
            screenshotImageView = (ImageView) itemView.findViewById(R.id.screenshotImageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mActivity, ScreenShotFullScreenActivity.class);
                    intent.putExtra(mActivity.getString(R.string.screen_shot_list), mItems);
                    intent.putExtra(mActivity.getString(R.string.index), getAdapterPosition());
                    mActivity.startActivity(intent);
                }
            });
        }
    }

}
