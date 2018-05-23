package com.androidapp.watchme.util;

import android.util.DisplayMetrics;



public class ConvertDpPx {

    public static int dpToPx(int dp) {
        DisplayMetrics displayMetrics = MyApplication.mContext.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int pxToDp(int px) {
        DisplayMetrics displayMetrics = MyApplication.mContext.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
