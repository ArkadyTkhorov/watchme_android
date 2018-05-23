package com.androidapp.watchme.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidapp.watchme.R;
import com.androidapp.watchme.activity.MainActivity;
import com.androidapp.watchme.adapter.ScreenshotDataAdapter;
import com.androidapp.watchme.util.ConvertDpPx;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.androidapp.watchme.util.MyApplication.mContext;
import static com.androidapp.watchme.util.MyApplication.mFirebaseDatabaseScreenshots;
import static com.androidapp.watchme.util.MyApplication.mFirebaseStorageReference;


public class BuddyFragment extends Fragment {

    private BuddyFragment buddyFragment;

    private LinearLayout screenshotLayout;
    private SwipeRefreshLayout swipe_container;

    private ArrayList<String> dateList = new ArrayList<>();

    public BuddyFragment() {
        buddyFragment = this;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_buddy, container, false);

        dateList.clear();
        screenshotLayout = (LinearLayout) rootView.findViewById(R.id.screenshotLayout);
        swipe_container = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);

        swipe_container.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MainActivity._inst.reloadSreenshot();
            }
        });

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            final String userEmail = bundle.getString(getString(R.string.email));
            mFirebaseDatabaseScreenshots.orderByChild(getString(R.string.email)).equalTo(userEmail)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot childData : dataSnapshot.getChildren()) {
                                String date = String.valueOf(childData.child(getString(R.string.date)).getValue());


                              if(!isDateAdded(date))
                              {

                              }


                            }

                            for (int i = 0; i < dateList.size(); i++) {
                                String Date = dateList.get(i);

                                    setDateAndScreen(Date);

                            }

                        }

                        private void setDateAndScreen(String date) {


                            if (isDateAdded(date)) {
                                TextView textView = new TextView(mContext);
                                textView.setText(date);
                                textView.setTextSize(ConvertDpPx.dpToPx(8));
                                textView.setTextColor(getResources().getColor(R.color.colorWhite));

                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                params.gravity = Gravity.END;
                                params.setMargins(0, ConvertDpPx.dpToPx(5), 0, 0);
                                textView.setLayoutParams(params);

                                screenshotLayout.addView(textView, 0);

                                addScreenshots(inflater, userEmail, date);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }





        return rootView;
    }


    private boolean isDateAdded(String date) {

        for (int i = 0; i < dateList.size(); i++) {
            if (date.equals(dateList.get(i))) {
                return true;
            }
        }

        dateList.add(date);
        return false;
    }

    private void addScreenshots(LayoutInflater inflater, final String email, final String date) {
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.day_screenshots, null);
        final RecyclerView screenshotRecyclerView = linearLayout.findViewById(R.id.screenshotRecyclerView);
        final ArrayList<String> screenshotUrlArrayList;
        final ScreenshotDataAdapter screenshotDataAdapter;
        final ArrayList<String> UrlList = new ArrayList<>();

        screenshotUrlArrayList = new ArrayList<>();
        screenshotDataAdapter = new ScreenshotDataAdapter(getActivity(), screenshotUrlArrayList);
        screenshotRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        screenshotRecyclerView.setAdapter(screenshotDataAdapter);
        screenshotRecyclerView.setHasFixedSize(true);


        UrlList.clear();
        screenshotUrlArrayList.clear();

        mFirebaseDatabaseScreenshots.orderByChild(getString(R.string.email)).equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot childData : dataSnapshot.getChildren()) {
                            String dt = String.valueOf(childData.child(getString(R.string.date)).getValue());
                            String name = String.valueOf(childData.child(getString(R.string.name)).getValue());
                            if (dt.equals(date)) {


                                mFirebaseStorageReference.child("screenshots").child(email).child(date).child(name).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        if (!uri.toString().isEmpty()) {

                                            screenshotUrlArrayList.add(uri.toString());
                                            screenshotDataAdapter.notifyDataSetChanged();

                                            if (swipe_container.isRefreshing()) {
                                                swipe_container.setRefreshing(false);
                                            }

                                            MainActivity._inst.showBuddyLayout();
                                        }
                                    }
                                });

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        screenshotLayout.addView(linearLayout, 1);
    }
}
