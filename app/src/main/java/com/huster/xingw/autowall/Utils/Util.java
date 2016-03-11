package com.huster.xingw.autowall.Utils;


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
        SimpleDateFormat    sDateFormat    =   new SimpleDateFormat("yyyy-MM-dd");
        String date=sDateFormat.format(new Date());
        return wall.getDate().equals(date);
    }
}
