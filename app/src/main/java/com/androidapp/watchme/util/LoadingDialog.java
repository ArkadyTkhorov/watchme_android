package com.androidapp.watchme.util;

import android.app.ProgressDialog;
import android.content.Context;

import com.androidapp.watchme.R;


public class LoadingDialog {

    public static ProgressDialog loadingDialog;

    public static void startLoading(Context context){
        if (loadingDialog == null) {
            loadingDialog = new ProgressDialog(context, R.style.ProgressTheme);
            loadingDialog.setCancelable(false);
            loadingDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            loadingDialog.show();
        }
    }

    public static void endLoading(){
        if (loadingDialog != null){
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    public static void startLoading(Context context, String message){
        if (loadingDialog == null) {
            loadingDialog = new ProgressDialog(context, R.style.ProgressTheme);
            loadingDialog.setCancelable(false);
            loadingDialog.setMessage(message);
            loadingDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            loadingDialog.show();
        }
    }

    public static void hideDialog(){
        if (loadingDialog != null) {
            loadingDialog.hide();
        }
    }

    public static void showDialog(){
        if (loadingDialog != null) {
            loadingDialog.show();
        }
    }
}
