package com.huster.xingw.autowall.Receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.huster.xingw.autowall.service.service;
import com.orhanobut.logger.Logger;

/**
 * Created by Xingw on 2016/3/14.
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    private static boolean isServiceRunning = false;

    public static boolean isServiceRunning() {
        return isServiceRunning;
    }

    public static void setServiceRunning(boolean serviceRunning) {
        isServiceRunning = serviceRunning;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_TIME_TICK) ||intent.getAction().equals(Intent
                .ACTION_BOOT_COMPLETED) || intent.getAction().equals(Intent
                .ACTION_USER_PRESENT)) {
            Logger.d("接收到了广播 %s",intent.getAction());
            //检查Service状态
            ActivityManager manager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if ("com.huster.xingw.autowall".equals(service.service.getClassName())) {
                    isServiceRunning = true;
                }
            }
            if (!isServiceRunning) {
                Intent i = new Intent(context, service.class);
                context.startService(i);
            }
        }
    }
}