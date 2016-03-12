package com.huster.xingw.autowall.Utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.telephony.TelephonyManager;

import com.huster.xingw.autowall.Model.Wall;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Xingw on 2016/3/10.
 */
public class Util {
    public static boolean isnewday(Realm realm){
        Wall wall = realm.where(Wall.class)
                .findFirst();
        if (null == wall) return true;
        return wall.getPublishedAt().equals(getTimeNow());
    }

    public static String getTimeNow(){
        SimpleDateFormat    sDateFormat    =   new SimpleDateFormat("yyyy-MM-dd");
        String date=sDateFormat.format(new Date());
        return date;
    }


    public static String getTimeNowbyHours(){
        SimpleDateFormat    sDateFormat    =   new SimpleDateFormat("HH:mm");
        String date=sDateFormat.format(new Date());
        return date;
    }

    public static boolean isWifiEnabled(Context context) {
        ConnectivityManager mgrConn = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager mgrTel = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return ((mgrConn.getActiveNetworkInfo() != null && mgrConn
                .getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel
                .getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
    }
}
