package com.androidapp.watchme.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class PreferenceUtil {

    public void saveToPreference(Context context, String key, String value){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getFromPreference(Context context, String key, String defaultValue){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        try{
            return prefs.getString(key, defaultValue);
        }catch (Exception e){
            e.printStackTrace();
            return defaultValue;
        }
    }

    public void saveBooleanToPreference(Context context, String key, boolean value){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getBooleanFromPreference(Context context, String key, boolean defaultValue){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        try{
            return prefs.getBoolean(key, defaultValue);
        }catch (Exception e){
            e.printStackTrace();
            return defaultValue;
        }
    }

    public void saveIntToPreference(Context context, String key, int value){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public int getIntFromPreference(Context context, String key, int defaultValue){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        try{
            return prefs.getInt(key, defaultValue);
        }catch (Exception e){
            e.printStackTrace();
            return defaultValue;
        }
    }

    public void saveLongToPreference(Context context, String key, long value){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public long getLongFromPreference(Context context, String key, long defaultValue){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        try{
            return prefs.getLong(key, defaultValue);
        }catch (Exception e){
            e.printStackTrace();
            return defaultValue;
        }
    }
}
