package com.huster.xingw.autowall.Model;

import io.realm.RealmObject;

/**
 * Created by Xingw on 2016/3/10.
 */
public class Goods  {

//    "error": false,
//            "results": [
//    {
//            "_id": "56e220ca67765966681b3a23",
//            "_ns": "ganhuo",
//            "createdAt": "2016-03-11T09:35:06.879Z",
//            "desc": "3.11--\u4e00\u5468\u5e74\u5feb\u4e50\uff01\uff01\uff01",
//            "publishedAt": "2016-03-11T12:37:20.4Z",
//            "source": "chrome",
//            "type": "\u798f\u5229",
//            "url": "http://ww4.sinaimg.cn/large/7a8aed7bjw1f1so7l2u60j20zk1cy7g9.jpg",
//            "used": true,
//            "who": "\u5f20\u6db5\u5b87"
//    }
//    ]
    private String who;
    private String publishedAt;
    private String desc;
    private String type;
    private String url;
    private boolean used;
    private String _id;
    private String createdAt;
//    private String updatedAt;

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
