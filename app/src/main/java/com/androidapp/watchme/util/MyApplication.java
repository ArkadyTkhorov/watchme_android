package com.androidapp.watchme.util;

import android.app.Application;
import android.content.Context;

import com.androidapp.watchme.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MyApplication extends Application {

    public static Context mContext;

    public static PreferenceUtil utils;

    public static FirebaseAuth firebaseAuth;
    public static FirebaseDatabase mFirebaseInstance;
    public static DatabaseReference mFirebaseDatabaseUsers;
    public static DatabaseReference mFirebaseDatabaseScreenshots;
    public static StorageReference mFirebaseStorageReference;

    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();
        utils = new PreferenceUtil();

        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabaseUsers = mFirebaseInstance.getReference(getString(R.string.users));
        mFirebaseDatabaseScreenshots = mFirebaseInstance.getReference(getString(R.string.screenshots));
        mFirebaseStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(getString(R.string.firebase_storage_url));

        instance = this;

    }

    public static MyApplication getInstance(){
        return instance;
    }

}
