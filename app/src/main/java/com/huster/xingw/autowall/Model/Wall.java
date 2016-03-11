package com.huster.xingw.autowall.Model;

import io.realm.RealmObject;

/**
 * Created by Xingw on 2016/3/11.
 */
public class Wall extends RealmObject{
    String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
