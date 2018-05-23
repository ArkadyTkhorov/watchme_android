package com.androidapp.watchme.util;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;
import android.media.Image;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;


import com.androidapp.watchme.R;
import com.androidapp.watchme.activity.SignupActivity;
import com.androidapp.watchme.model.Screenshot;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.androidapp.watchme.util.MyApplication.firebaseAuth;
import static com.androidapp.watchme.util.MyApplication.mContext;
import static com.androidapp.watchme.util.MyApplication.mFirebaseDatabaseScreenshots;
import static com.androidapp.watchme.util.MyApplication.mFirebaseStorageReference;
import static com.androidapp.watchme.util.MyApplication.utils;
/**
 * Created by maiAjam on 2/10/2018.
 */

public class AdminReciver extends DeviceAdminReceiver {


    private static String STORE_DIRECTORY;
    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);

        //utils.saveIntToPreference(context,"Enable",1);
       context.startActivity(new Intent(context,SignupActivity.class));

    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
       // utils.saveIntToPreference(context,"Enable",0);
        sendBlackScreen(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sendBlackScreen(Context context) {


        String state = Environment.getExternalStorageState();


        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            //filesDir = getExternalFilesDir(null);

            File externalFilesDir = context.getExternalFilesDir(null);
            if (externalFilesDir != null) {
                STORE_DIRECTORY = externalFilesDir.getAbsolutePath() + "/screenshots";
                File storeDirectory = new File(STORE_DIRECTORY);
                if (!storeDirectory.exists()) {
                    boolean success = storeDirectory.mkdirs();
                    if (!success) {
                        Log.e("", "failed to create file storage directory.");
                        return;
                    }
                } else {
                    Log.e("", "failed to create file storage directory, getExternalFilesDir is null.");


                    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.black);
                    String fileName = STORE_DIRECTORY + "/" + System.currentTimeMillis() + "black" + ".webp";
                    try {
                        FileOutputStream fos = new FileOutputStream(fileName);

                        bitmap.compress(Bitmap.CompressFormat.WEBP, 10, fos);

                        Log.e("tag", "captured image: " + fileName);


                        File file = new File(STORE_DIRECTORY);
                        File list[] = file.listFiles();

                        for (int i = 0; i < list.length; i++) {
                            if (list[i].length() != 0) {
                                uploadScreenshot(list[i].getAbsolutePath(), context);
                            }
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    return;

                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {


        sendBlackScreen(context);
        return super.onDisableRequested(context, intent);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void uploadScreenshot(final String fileName, final Context context) {

        try {

            SimpleDateFormat df = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
            InputStream stream;
            stream = new FileInputStream(fileName);

            String[] fn = fileName.split("/");
            final String email = utils.getFromPreference(context,context.getString(R.string.email), "");
            final String dateS = df.format(Calendar.getInstance().getTime());
            final String name = fn[fn.length - 1];
            final Map<String, Object> timestamp = new HashMap<>();
            timestamp.put("timeStamp", ServerValue.TIMESTAMP);


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
                            Toast.makeText(context, exception.getMessage(), Toast.LENGTH_LONG).show();
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
