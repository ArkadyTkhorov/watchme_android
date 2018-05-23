package com.androidapp.watchme.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidapp.watchme.R;
import com.androidapp.watchme.adapter.BuddyViewAdapter;
import com.androidapp.watchme.model.Screenshot;
import com.androidapp.watchme.util.AdminReciver;
import com.androidapp.watchme.util.ConvertDpPx;
import com.androidapp.watchme.util.LoadingDialog;
import com.androidapp.watchme.util.ScreenShotManger;
import com.androidapp.watchme.util.ScreenShotService;
import com.androidapp.watchme.util.Utils;
import com.androidapp.watchme.util.reciver;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import static android.view.Gravity.CENTER;
import static com.androidapp.watchme.util.MyApplication.firebaseAuth;
import static com.androidapp.watchme.util.MyApplication.mFirebaseDatabaseScreenshots;
import static com.androidapp.watchme.util.MyApplication.mFirebaseDatabaseUsers;
import static com.androidapp.watchme.util.MyApplication.mFirebaseStorageReference;
import static com.androidapp.watchme.util.MyApplication.utils;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar mToolbar;
    private ImageView toggleImageView;
    private TextView settingsTextView, helpTextView, logoutTextView;
    private LinearLayout userLayout, buddyLayout, userNameLayout;
    private ArrayList<TextView> userNameTextViewList = new ArrayList<>();
    private ViewPager viewPager;
    private BuddyViewAdapter adapter;
    private Handler mJobHandler;

    private FirebaseAuth.AuthStateListener authListener;

    // Screen Capture
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE = 100;
    private static String STORE_DIRECTORY;
    private static final String SCREENCAP_NAME = "screencap";
    private static final int VIRTUAL_DISPLAY_FLAGS = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
    private static long LATEST_CAPTURED_TIME;
    private static final long CAPTURE_INTERVAL = 1000 * 60 * 3;

    private static MediaProjection sMediaProjection;
    private MediaProjectionManager mProjectionManager;
    private ImageReader mImageReader;
    private Handler mHandler;
    private Display mDisplay;
    private VirtualDisplay mVirtualDisplay;
    private int mDensity;
    private int mWidth;
    private int mHeight;
    private int mRotation;


    private DevicePolicyManager watchMeAdmin_PolicyManger ;
    private ComponentName watch_DeviceAdmin;
    private OrientationChangeCallback mOrientationChangeCallback;

    public static MainActivity _inst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        toggleImageView = (ImageView) findViewById(R.id.toggleImageView);
        settingsTextView = (TextView) findViewById(R.id.settingsTextView);
        helpTextView = (TextView) findViewById(R.id.helpTextView);
        logoutTextView = (TextView) findViewById(R.id.logoutTextView);
        userLayout = (LinearLayout) findViewById(R.id.userLayout);
        buddyLayout = (LinearLayout) findViewById(R.id.buddyLayout);
        userNameLayout = (LinearLayout) findViewById(R.id.userNameLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);



        watchMeAdmin_PolicyManger =(DevicePolicyManager)getSystemService(DEVICE_POLICY_SERVICE);

        watch_DeviceAdmin =  new ComponentName(this,
                AdminReciver.class);

        if(!watchMeAdmin_PolicyManger.isAdminActive(watch_DeviceAdmin))
        {
            startActivity(new Intent(this,AdminActivity.class));
        }else {


                setSupportActionBar(mToolbar);
                drawerToggle = new ActionBarDrawerToggle(
                        this, drawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                drawerLayout.setDrawerListener(drawerToggle);
                drawerToggle.syncState();

                toggleImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        drawerLayout.openDrawer(GravityCompat.START);
                    }
                });


                settingsTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        drawerLayout.closeDrawer(GravityCompat.START);

                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent);
                    }
                });

                helpTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        drawerLayout.closeDrawer(GravityCompat.START);

                        Intent i = new Intent(Intent.ACTION_SENDTO);
                        i.setData(Uri.parse("mailto:" + getString(R.string.contact_email)));
                        i.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
                        startActivity(Intent.createChooser(i, "Send email"));
                    }
                });

                logoutTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        drawerLayout.closeDrawer(GravityCompat.START);

                        StopScreenCapAtBack();
                        firebaseAuth.signOut();

                    }
                });


                // this listener will be called when there is change in firebase user session


                if (utils.getFromPreference(MainActivity.this, getString(R.string.user_type), "").equals(getString(R.string.user))) {
                    userLayout.setVisibility(View.VISIBLE);
                    buddyLayout.setVisibility(View.GONE);


                    StartScreenCapAtBackGround();


                    // call for the projection manager
                 mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

                    // start capture handling thread
                     new Thread() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            mHandler = new Handler();
                            Looper.loop();
                        }
                    }.start();
                    startProjection();

                }
        /*else {

            StopScreenCapAtBack();
            stopProjection();

            userLayout.setVisibility(View.GONE);
            buddyLayout.setVisibility(View.INVISIBLE);

            LoadingDialog.startLoading(MainActivity.this);
            setUserNameLayout();

        }
*/


        }
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    finish();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        };

        _inst = this;
    }

    private void StopScreenCapAtBack() {

      AlarmManager Amgr = (AlarmManager) getBaseContext().getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(this, reciver.class);

        PendingIntent operation = PendingIntent.getBroadcast(getBaseContext(), 1, i, PendingIntent.FLAG_CANCEL_CURRENT);

        Amgr.cancel(operation);



    }




    private void StartScreenCapAtBackGround() {




      AlarmManager Amgr = (AlarmManager) getBaseContext().getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(this,reciver.class);

        PendingIntent operation = null;

            operation = PendingIntent.getBroadcast(getBaseContext(),1,i,0);


        Amgr.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+(1*60*1000),1*60*1000,operation);


    }

    public void reloadSreenshot() {
        userNameTextViewList.clear();
        userNameLayout.removeAllViews();

        setUserNameLayout();
    }

    public void showBuddyLayout() {
        LoadingDialog.endLoading();
        buddyLayout.setVisibility(View.VISIBLE);
    }

    private void setUserNameLayout() {
        mFirebaseDatabaseUsers.orderByChild(getString(R.string.buddy)).equalTo(utils.getFromPreference(MainActivity.this, getString(R.string.email), ""))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null && dataSnapshot.exists()) {
                            ArrayList<String > userEmailList = new ArrayList<>();

                            for (DataSnapshot childData: dataSnapshot.getChildren()) {
                                TextView textView = new TextView(MainActivity.this);
                                textView.setText(String.valueOf(childData.child(getString(R.string.name)).getValue()));
                                textView.setTextSize(ConvertDpPx.dpToPx(8));
                                textView.setTextColor(getResources().getColor(R.color.colorUnselected));
                                textView.setPadding(ConvertDpPx.dpToPx(14), ConvertDpPx.dpToPx(4), ConvertDpPx.dpToPx(14), ConvertDpPx.dpToPx(4));
                                textView.setSingleLine();
                                textView.setGravity(CENTER);

                                textView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        int index = 0;
                                        for (int i = 0; i < userNameLayout.getChildCount(); i++) {
                                            ((TextView) userNameLayout.getChildAt(i)).setTextColor(getResources().getColor(R.color.colorUnselected));
                                            userNameLayout.getChildAt(i).setBackground(null);

                                            if (view.equals(userNameLayout.getChildAt(i))) {
                                                index = i;
                                            }
                                        }
                                        ((TextView) view).setTextColor(getResources().getColor(R.color.colorBlack));
                                        view.setBackgroundResource(R.drawable.edit_text_background);

                                        viewPager.setCurrentItem(index, false);
                                    }
                                });

                                userNameTextViewList.add(textView);

                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ConvertDpPx.dpToPx(130), ViewGroup.LayoutParams.WRAP_CONTENT);
                                params.setMargins(ConvertDpPx.dpToPx(0), ConvertDpPx.dpToPx(5), ConvertDpPx.dpToPx(0), ConvertDpPx.dpToPx(5));
                                textView.setLayoutParams(params);
                                userNameLayout.addView(textView);

                                if (textView.equals(userNameLayout.getChildAt(0))) {
                                    textView.setTextColor(getResources().getColor(R.color.colorBlack));
                                    textView.setBackgroundResource(R.drawable.edit_text_background);
                                }

                                userEmailList.add(String.valueOf(childData.child(getString(R.string.email)).getValue()));
                            }

                            adapter = new BuddyViewAdapter(MainActivity.this, getSupportFragmentManager(), (int) dataSnapshot.getChildrenCount(), userEmailList);
                            viewPager.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });




    }



    public void onResume() {
        super.onResume();


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {

         //   ScreenShotManger.INSTANCE.onActivityResult(resultCode,data);
           sMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);

            if (sMediaProjection != null) {
                File externalFilesDir = getExternalFilesDir(null);
                if (externalFilesDir != null) {
                    STORE_DIRECTORY = externalFilesDir.getAbsolutePath() + "/screenshots";
                    File storeDirectory = new File(STORE_DIRECTORY);
                    if (!storeDirectory.exists()) {
                        boolean success = storeDirectory.mkdirs();
                        if (!success) {
                            Log.e(TAG, "failed to create file storage directory.");
                            return;
                        }
                    }
                } else {
                    Log.e(TAG, "failed to create file storage directory, getExternalFilesDir is null.");
                    return;
                }

                // display metrics
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                mDensity = metrics.densityDpi;
                mDisplay = getWindowManager().getDefaultDisplay();

                // create virtual display depending on device width / height
                createVirtualDisplay();

                // register orientation change callback
                mOrientationChangeCallback = new OrientationChangeCallback(this);
                if (mOrientationChangeCallback.canDetectOrientation()) {
                    mOrientationChangeCallback.enable();
                }

                // register media projection stop callback
                sMediaProjection.registerCallback(new MediaProjectionStopCallback(), mHandler);
            }

        }

    }

    /****************************************** UI Widget Callbacks *******************************/
    private void startProjection() {
        startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
    }

    private void stopProjection() {
        if (mHandler != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (sMediaProjection != null) {
                        sMediaProjection.stop();
                    }
                }
            });
        }
    }

    /****************************************** Factoring Virtual Display creation ****************/
    private void createVirtualDisplay() {
        // get width and height
        Point size = new Point();
        mDisplay.getSize(size);
        mWidth = size.x;
        mHeight = size.y;

        // start capture reader
        mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);
        mVirtualDisplay = sMediaProjection.createVirtualDisplay(SCREENCAP_NAME, mWidth, mHeight, mDensity, VIRTUAL_DISPLAY_FLAGS, mImageReader.getSurface(), null, mHandler);
        mImageReader.setOnImageAvailableListener(new ImageAvailableListener(), mHandler);
    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            stopProjection();
          //System.exit(0);
            super.onBackPressed();
        }


    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            firebaseAuth.removeAuthStateListener(authListener);
        }


        stopProjection();
    }

    // Screen Capture
    private class ImageAvailableListener implements ImageReader.OnImageAvailableListener {
        @Override
        public void onImageAvailable(ImageReader reader) {

            Image image = null;
            FileOutputStream fos = null;
            Bitmap bitmap = null;

            try {
                image = reader.acquireLatestImage();
                if (image != null) {
                    Image.Plane[] planes = image.getPlanes();
                    ByteBuffer buffer = planes[0].getBuffer();
                    int pixelStride = planes[0].getPixelStride();
                    int rowStride = planes[0].getRowStride();
                    int rowPadding = rowStride - pixelStride * mWidth;

                    // create bitmap
                    bitmap = Bitmap.createBitmap(mWidth + rowPadding / pixelStride, mHeight, Bitmap.Config.ARGB_8888);
                    bitmap.copyPixelsFromBuffer(buffer);

                    if ((System.currentTimeMillis() - LATEST_CAPTURED_TIME) == CAPTURE_INTERVAL) {

                        // write bitmap to a file
                        String fileName = STORE_DIRECTORY + "/" + System.currentTimeMillis() + ".webp";
                        fos = new FileOutputStream(fileName);
                        bitmap.compress(Bitmap.CompressFormat.WEBP, 10, fos);

                        Log.e(TAG, "captured image: " + fileName);

                        LATEST_CAPTURED_TIME = System.currentTimeMillis();


                        File file = new File(STORE_DIRECTORY);
                        File list[] = file.listFiles();

                        for (int i = 0; i < list.length; i++) {
                            if (list[i].length() != 0) {
                                uploadScreenshot(list[i].getAbsolutePath());
                            }
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }

                if (bitmap != null) {
                    bitmap.recycle();
                }

                if (image != null) {
                    image.close();
                }

            }
        }
    }

    private class OrientationChangeCallback extends OrientationEventListener {

        OrientationChangeCallback(Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            final int rotation = mDisplay.getRotation();
            if (rotation != mRotation) {
                mRotation = rotation;
                try {
                    // clean up
                    if (mVirtualDisplay != null) mVirtualDisplay.release();
                    if (mImageReader != null) mImageReader.setOnImageAvailableListener(null, null);

                    // re-create virtual display depending on device width / height
                    createVirtualDisplay();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class MediaProjectionStopCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            Log.e("ScreenCapture", "stopping projection.");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mVirtualDisplay != null) mVirtualDisplay.release();
                    if (mImageReader != null) mImageReader.setOnImageAvailableListener(null, null);
                    if (mOrientationChangeCallback != null) mOrientationChangeCallback.disable();
                    sMediaProjection.unregisterCallback(MediaProjectionStopCallback.this);
                }
            });
        }
    }

    private void uploadScreenshot(final String fileName) {

        try {

            InputStream stream;
            stream = new FileInputStream(fileName);

            String[] fn = fileName.split("/");
            final String email = utils.getFromPreference(MainActivity.this, getString(R.string.email), "");
            final String date = Utils.getCurrentDate();
            final String name = fn[fn.length - 1];

            StorageReference riversRef = mFirebaseStorageReference.child("screenshots")
                    .child(email)
                    .child(date)
                    .child(name)
                   ;
            riversRef.putStream(stream)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                            //and displaying a success toast

                            File file = new File(fileName);
                            if (file.exists()) {
                                file.delete();
                            }

                            updateDB(email, date, name);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            //and displaying error message
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateDB(String email, String date, String name) {
      //  Screenshot screenshot = new Screenshot(email, date, name,ServerValue.TIMESTAMP);
       // mFirebaseDatabaseScreenshots.push().setValue(screenshot);
    }

}
