package com.androidapp.watchme.util;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.androidapp.watchme.R;
import com.androidapp.watchme.activity.AdminActivity;
import com.androidapp.watchme.activity.LoginActivity;
import com.androidapp.watchme.activity.MainActivity;
import com.androidapp.watchme.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.DEVICE_POLICY_SERVICE;
import static com.androidapp.watchme.util.MyApplication.firebaseAuth;
import static com.androidapp.watchme.util.MyApplication.mFirebaseDatabaseUsers;
import static com.androidapp.watchme.util.MyApplication.utils;


public class Utils {

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static void signUpUser(final Activity activity, final String name, final String email, final String password, final String type, final String buddyEmail) {

        LoadingDialog.startLoading(activity);

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        LoadingDialog.endLoading();

                        //checking if success
                        if (task.isSuccessful()) {
                            User user = new User(name, email, type, buddyEmail);



                            DevicePolicyManager watchMeAdmin_PolicyManger  =(DevicePolicyManager)activity.getSystemService(DEVICE_POLICY_SERVICE);

                            ComponentName watch_DeviceAdmin =  new ComponentName(activity, AdminReciver.class);

                            mFirebaseDatabaseUsers.push().setValue(user);

                            utils.saveToPreference(activity, activity.getString(R.string.email), email);
                            utils.saveToPreference(activity, activity.getString(R.string.password), password);
                            utils.saveToPreference(activity, activity.getString(R.string.user_type), type);
                            utils.saveToPreference(activity, activity.getString(R.string.name), name);
                            utils.saveToPreference(activity, activity.getString(R.string.buddy), buddyEmail);

                            if(watchMeAdmin_PolicyManger.isAdminActive(watch_DeviceAdmin))
                            {
                                Intent intent = new Intent(activity,AdminActivity.class);
                                activity.startActivity(intent);
                                activity.finish();
                            }else
                            {
                                Intent intent = new Intent(activity,MainActivity.class);
                                activity.startActivity(intent);
                                activity.finish();
                            }

                        } else {
                            Toast.makeText(activity, activity.getString(R.string.signup_failed), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public static void loginUser(final Activity activity, final String email, final String password) {

        LoadingDialog.startLoading(activity);

        final DevicePolicyManager watchMeAdmin_PolicyManger  =(DevicePolicyManager)activity.getSystemService(DEVICE_POLICY_SERVICE);

        final ComponentName watch_DeviceAdmin =  new ComponentName(activity, AdminReciver.class);
        //authenticate user
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        LoadingDialog.endLoading();
                        if (!task.isSuccessful()) {
                            // there was an error
                            Toast.makeText(activity, activity.getString(R.string.login_failed), Toast.LENGTH_LONG).show();
                        } else {

                            mFirebaseDatabaseUsers.orderByChild(activity.getString(R.string.email)).equalTo(email).addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    utils.saveToPreference(activity, activity.getString(R.string.email), email);
                                    utils.saveToPreference(activity, activity.getString(R.string.password), password);
                                    utils.saveToPreference(activity, activity.getString(R.string.user_type), String.valueOf(dataSnapshot.child(activity.getString(R.string.user_type)).getValue()));
                                    utils.saveToPreference(activity, activity.getString(R.string.name), String.valueOf(dataSnapshot.child(activity.getString(R.string.name)).getValue()));
                                    utils.saveToPreference(activity, activity.getString(R.string.buddy), String.valueOf(dataSnapshot.child(activity.getString(R.string.buddy)).getValue()));
                                    if(!watchMeAdmin_PolicyManger.isAdminActive(watch_DeviceAdmin))
                                    {
                                        Intent intent = new Intent(activity,AdminActivity.class);
                                        activity.startActivity(intent);
                                        activity.finish();
                                    }else
                                    {
                                        Intent intent = new Intent(activity,MainActivity.class);
                                        activity.startActivity(intent);
                                        activity.finish();
                                    }

                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                }

                                @Override
                                public void onChildRemoved(DataSnapshot dataSnapshot) {

                                }

                                @Override
                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    }
                });
    }

    public static void forgotPassword(final Activity activity, String email) {

        LoadingDialog.startLoading(activity);
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        LoadingDialog.endLoading();
                        if (task.isSuccessful()) {
                            Toast.makeText(activity, activity.getString(R.string.sent_email), Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(activity, LoginActivity.class);
                            activity.startActivity(intent);
                            activity.finish();
                        } else {
                            Toast.makeText(activity, activity.getString(R.string.failed_to_send_email), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public static String getCurrentDate() {
        Date curDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd MMM");
        String DateToStr = format.format(curDate);
        return DateToStr;
    }

}
