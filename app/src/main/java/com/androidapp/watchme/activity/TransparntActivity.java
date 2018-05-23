package com.androidapp.watchme.activity;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.icu.util.Calendar;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.androidapp.watchme.R;
import com.androidapp.watchme.model.Screenshot;
import com.androidapp.watchme.util.ScreenShotManger;
import com.androidapp.watchme.util.ScreenShotService;
import com.androidapp.watchme.util.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.androidapp.watchme.util.MyApplication.firebaseAuth;
import static com.androidapp.watchme.util.MyApplication.mFirebaseDatabaseScreenshots;
import static com.androidapp.watchme.util.MyApplication.mFirebaseStorageReference;
import static com.androidapp.watchme.util.MyApplication.utils;

public class TransparntActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE = 120;
    private static String STORE_DIRECTORY;
    private static final String SCREENCAP_NAME = "screencap";
    private static final int VIRTUAL_DISPLAY_FLAGS = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
    private static long LATEST_CAPTURED_TIME;
    private static final long CAPTURE_INTERVAL = 1*60*1000;

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
    private OrientationChangeCallback mOrientationChangeCallback;
    private Image finalImage = null;
    private boolean shouldCaptur = true;
    PowerManager pm ;
    boolean isScreenOn;
    private FirebaseAuth.AuthStateListener authListener;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transparnt);





        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if( !keyguardManager.isKeyguardLocked ()) {


            try {
                int curBrightnessValue=android.provider.Settings.System.getInt(
                        getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);

                if(curBrightnessValue > 0)
                {

                  //  ScreenShotManger.INSTANCE.takeScreenshot(getBaseContext());
                mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

                    mProjectionManager.createScreenCaptureIntent().setFlags(0);
                    startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
                    shouldCaptur = false ;


                    new Thread() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            mHandler = new Handler();
                            Looper.loop();
                        }
                    }.start();


                }else
                {
                    finish();
                }
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

        }else
        {

            finish();
        }


        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    finish();
                 stopCap();
                }


            }
        };


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

        stopCap();
    }


    private void stopCap() {


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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        Intent i = new Intent(this, ScreenShotService.class);

        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        isScreenOn = pm.isInteractive();

        if (requestCode == REQUEST_CODE) {




            finish();
            sMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);

            if (isScreenOn) {


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
                    mOrientationChangeCallback = new TransparntActivity.OrientationChangeCallback(this);
                    if (mOrientationChangeCallback.canDetectOrientation()) {
                        mOrientationChangeCallback.enable();
                    }


                    // register media projection stop callback
                    sMediaProjection.registerCallback(new TransparntActivity.MediaProjectionStopCallback(), mHandler);
                }
            }
        }
    }

    private class ImageAvailableListener  implements ImageReader.OnImageAvailableListener {



        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onImageAvailable(ImageReader imageReader) {

            Image image = null;
            FileOutputStream fos = null;
            Bitmap bitmap = null;

            try {
                image = imageReader.acquireLatestImage();
                finalImage = image ;
                if (image != null) {
                    Image.Plane[] planes = image.getPlanes();
                    ByteBuffer buffer = planes[0].getBuffer();
                    int pixelStride = planes[0].getPixelStride();
                    int rowStride = planes[0].getRowStride();
                    int imageWidth = image.getWidth();
                    int imageHight = image.getHeight();

                int rowPadding ;


                    if(imageWidth> imageHight)
                    {
                        // horizantal
                         rowPadding = rowStride - pixelStride * mHeight;
                    }else
                    {
                         rowPadding = rowStride - pixelStride * mWidth;
                    }
                    //create bitmap
                  bitmap = Bitmap.createBitmap(mWidth + rowPadding / pixelStride, mHeight, Bitmap.Config.ARGB_8888);
                    bitmap.copyPixelsFromBuffer(buffer);

                 if ((System.currentTimeMillis() - LATEST_CAPTURED_TIME) > CAPTURE_INTERVAL) {

                         String fileName = STORE_DIRECTORY + "/" + System.currentTimeMillis() + ".webp";
                         fos = new FileOutputStream(fileName);
                         bitmap.compress(Bitmap.CompressFormat.WEBP, 10, fos);

                         Log.e("tag", "captured image: " + fileName);

                         LATEST_CAPTURED_TIME = System.currentTimeMillis();


                         File file = new File(STORE_DIRECTORY);
                         File list[] = file.listFiles();

                         stopService(new Intent(TransparntActivity.this,ScreenShotService.class));
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

        @RequiresApi(api = Build.VERSION_CODES.N)
        private void uploadScreenshot(final String fileName) {

            try {

                SimpleDateFormat df = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
                InputStream stream;
                stream = new FileInputStream(fileName);

                String[] fn = fileName.split("/");
                final String email = utils.getFromPreference(getApplicationContext(),getApplicationContext().getString(R.string.email), "");
                final String dateS = df.format(Calendar.getInstance().getTime());
                final String name = fn[fn.length - 1];
                final Map<String, Object> timestamp = new HashMap<>();
               timestamp.put("timestamp", ServerValue.TIMESTAMP);


                Log.d("Date",dateS);
                StorageReference riversRef = mFirebaseStorageReference.child("screenshots")
                        .child(email)
                        .child(dateS)
                        .child(name)
                       .child("timeStamp");
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

                                updateDB(email, dateS, name,timestamp);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                //if the upload is not successfull
                                //hiding the progress dialog
                                //and displaying error message

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

        private void updateDB(String email, String date, String name, Map<String, Object> s) {
            Screenshot screenshot = new Screenshot(email, date, name,s);
            mFirebaseDatabaseScreenshots.push().setValue(screenshot);
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

    private void createVirtualDisplay() {
        // get width and height
        Point size = new Point();

        final Point windowSize = new Point();
        WindowManager windowManager = (WindowManager)getBaseContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getRealSize(windowSize);

        mDisplay.getSize(windowSize);
        mWidth = windowSize.x;
        mHeight = windowSize.y;

        // start capture reader
        mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);
        mVirtualDisplay = sMediaProjection.createVirtualDisplay(SCREENCAP_NAME, mWidth, mHeight, mDensity, VIRTUAL_DISPLAY_FLAGS, mImageReader.getSurface(), null, mHandler);
        mImageReader.setOnImageAvailableListener(new ImageAvailableListener(), mHandler);
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
                    sMediaProjection.unregisterCallback(new MediaProjectionStopCallback());
                }
            });
        }
    }

}
