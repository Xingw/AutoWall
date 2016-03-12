package com.huster.xingw.autowall.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.huster.xingw.autowall.Model.Wall;
import com.huster.xingw.autowall.Utils.Util;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;

/**
 * Created by Xingw on 2016/3/12.
 */
public class service extends Service {
    boolean isServiceRunning = false;
    Realm realm;

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        BootBroadcastReceiver receiver = new BootBroadcastReceiver();
        registerReceiver(receiver, filter);
        realm = Realm.getInstance(this);
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (true) {
                    Wall wall = realm.where(Wall.class).findFirst();
                    if (!wall.isToday()) {
                        //检查是否联网
                        if (Util.isWifiEnabled(getBaseContext())) {
                            try {
                                sleep(60000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }else{
                            //从网络获取数据
                        }
                    } else {
                        try {
                            sleep(3600000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.run();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class BootBroadcastReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                //检查Service状态
                ActivityManager manager = (ActivityManager) getApplicationContext()
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        isServiceRunning = false;
    }
}