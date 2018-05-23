package com.androidapp.watchme.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.icu.util.Calendar;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.UiThread;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.androidapp.watchme.R;
import com.androidapp.watchme.model.Screenshot;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Handler;

import static com.androidapp.watchme.util.MyApplication.mContext;
import static com.androidapp.watchme.util.MyApplication.mFirebaseDatabaseScreenshots;
import static com.androidapp.watchme.util.MyApplication.mFirebaseStorageReference;
import static com.androidapp.watchme.util.MyApplication.utils;

/**
 * Created by maiAjam on 2/13/2018.
 */

public class ScreenShotManger {

    private static final String SCREENCAP_NAME = "screencap";
    private static final int VIRTUAL_DISPLAY_FLAGS = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
    public static final ScreenShotManger INSTANCE = new ScreenShotManger();
    private Intent mIntent;

    private static long LATEST_CAPTURED_TIME;
    private static final long CAPTURE_INTERVAL = 1 * 60 * 1000;
    private static String STORE_DIRECTORY;

    private ScreenShotManger() {
    }

    public void requestScreenshotPermission(@NonNull Activity activity, int requestId) {
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        activity.startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), requestId);
    }


    public void onActivityResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null)
            mIntent = data;
        else mIntent = null;
    }

    @UiThread
    public boolean takeScreenshot(@NonNull Context context) {
        if (mIntent == null)
            return false;
        final MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        final MediaProjection mediaProjection = mediaProjectionManager.getMediaProjection(Activity.RESULT_OK, mIntent);
        if (mediaProjection == null)
            return false;
        final int density = context.getResources().getDisplayMetrics().densityDpi;
        final Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        final Point windowSize = new Point();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getRealSize(windowSize);


        // start capture reader
        final ImageReader imageReader = ImageReader.newInstance(windowSize.x, windowSize.y, PixelFormat.RGBA_8888, 1);
        final VirtualDisplay virtualDisplay = mediaProjection.createVirtualDisplay(SCREENCAP_NAME, windowSize.x, windowSize.y, density, VIRTUAL_DISPLAY_FLAGS, imageReader.getSurface(), null, null);
        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onImageAvailable(ImageReader reader) {

                Image image = null;
                FileOutputStream fos = null;
                Bitmap bitmap = null;

                try {
                    image = imageReader.acquireLatestImage();

                    if (image != null) {
                        Image.Plane[] planes = image.getPlanes();
                        ByteBuffer buffer = planes[0].getBuffer();
                        int pixelStride = planes[0].getPixelStride();
                        int rowStride = planes[0].getRowStride();
                        int imageWidth = image.getWidth();
                        int imageHight = image.getHeight();

                        int rowPadding;


                        if (imageWidth > imageHight) {
                            // horizantal
                            rowPadding = rowStride - pixelStride * windowSize.y;
                        } else {
                            rowPadding = rowStride - pixelStride * windowSize.x;
                        }
                        //create bitmap
                        bitmap = Bitmap.createBitmap(windowSize.x + rowPadding / pixelStride, windowSize.y, Bitmap.Config.ARGB_8888);
                        bitmap.copyPixelsFromBuffer(buffer);

                        if ((System.currentTimeMillis() - LATEST_CAPTURED_TIME) > CAPTURE_INTERVAL) {

                            String fileName = STORE_DIRECTORY + "/" + System.currentTimeMillis() + ".webp";
                            fos = new FileOutputStream(fileName);
                            bitmap.compress(Bitmap.CompressFormat.WEBP, 10, fos);

                            Log.e("tag", "captured image: " + fileName);

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
        }, null);

        mediaProjection.registerCallback(new MediaProjection.Callback() {
            @Override
            public void onStop() {
                super.onStop();
                if (virtualDisplay != null)
                    virtualDisplay.release();
                imageReader.setOnImageAvailableListener(null, null);
                mediaProjection.unregisterCallback(this);
            }
        }, null);
        return true;

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void uploadScreenshot(final String fileName) {

        try {

            SimpleDateFormat df = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
            InputStream stream;
            stream = new FileInputStream(fileName);

            String[] fn = fileName.split("/");
            final String email = utils.getFromPreference(mContext, mContext.getString(R.string.email), "");
            final String dateS = df.format(Calendar.getInstance().getTime());
            final String name = fn[fn.length - 1];
            final Map<String, Object> timestamp = new HashMap<>();
            timestamp.put("timestamp", ServerValue.TIMESTAMP);


            Log.d("Date", dateS);
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

                            updateDB(email, dateS, name, timestamp);
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
        Screenshot screenshot = new Screenshot(email, date, name, s);
        mFirebaseDatabaseScreenshots.push().setValue(screenshot);
    }

}



