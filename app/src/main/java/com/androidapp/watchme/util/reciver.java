package com.androidapp.watchme.util;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.JobIntentService;

/**
 * Created by maiAjam on 1/30/2018.
 */

public class reciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {


      //  Intent Service_SecreenShot = new Intent(context,ScreenShotService.class);
        intent.setClass(context,ScreenShotService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           context.startForegroundService(intent);
       }else {
            context.startService(intent);
        }

    }
}
