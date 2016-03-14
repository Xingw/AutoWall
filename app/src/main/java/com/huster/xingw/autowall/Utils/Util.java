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
    //"publishedAt": "2016-03-11T12:37:20.4Z",
    public static boolean isnewday(Realm realm){
        if(getWeekNow().equals("星期六") || getWeekNow().equals("星期日")){
            //周末不更新
            return false;
        }
        Wall wall = realm.where(Wall.class)
                .findFirst();
        if (null == wall) return true;
        return wall.getPublishedAt().substring(0,10).equals(getTimeNow());
    }
    public static String getWeekNow(){
        SimpleDateFormat dateFm = new SimpleDateFormat("EEEE");
        String data = dateFm.format(new Date());
        return data;
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

    public static boolean isWifiConnected(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(wifiNetworkInfo.isConnected())
        {
            return true ;
        }
        return false ;
    }
}
