package com.huster.xingw.autowall.service;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Service;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.huster.xingw.autowall.Model.Goods;
import com.huster.xingw.autowall.Model.GoodsResult;
import com.huster.xingw.autowall.Model.Wall;
import com.huster.xingw.autowall.Net.GankCloudApi;
import com.huster.xingw.autowall.Receiver.BootBroadcastReceiver;
import com.huster.xingw.autowall.Utils.PictUtil;
import com.huster.xingw.autowall.Utils.RealmHelper;
import com.huster.xingw.autowall.Utils.Util;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.IOException;

import io.realm.Realm;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Xingw on 2016/3/12.
 */
public class service extends IntentService {
    Realm mRealm;

    public service() {
        super("AutoWall");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Logger.d("启动了服务");
        mRealm = Realm.getInstance(getBaseContext() );
        new Thread() {
            @Override
            public void run() {
                super.run();
                    if (Util.isnewday(mRealm)) {
                        while (true) {
                            //检查是否联网
                            if (!Util.isWifiConnected(getBaseContext())) {
                                try {
                                    Logger.d("没联网，等待1分钟");
                                    Thread.sleep(30000);//等待1分钟
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Logger.d("开始获取数据");
                                //从网络获取数据
                                refreshGoods();
                                return;
                            }
                        }
                    }
            }
        }.run();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BootBroadcastReceiver.setServiceRunning(false);
    }

    public void refreshGoods() {
        GankCloudApi.getIns()
                .getBenefitsGoods(1, 1)
                .cache()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getImageGoodsObserver);
    }

    private Observer<GoodsResult> getImageGoodsObserver = new Observer<GoodsResult>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(final GoodsResult goodsResult) {
            Realm ThreadRealm = Realm.getInstance(getBaseContext() );
            if (null != goodsResult && null != goodsResult.getResults()) {
                ThreadRealm.beginTransaction();
                for (Goods goods : goodsResult.getResults()) {
                    Wall wall = Wall.queryImageById(ThreadRealm, goods.get_id());
                    if (null == wall) wall = ThreadRealm.createObject(Wall.class);
                    Wall.updateDbGoods(wall, goods);
                    setImageToWallpaper(wall);
                }
                ThreadRealm.commitTransaction();
                return;
            }
            ThreadRealm.cancelTransaction();
        }
    };

    boolean setImageToWallpaper(final Wall wall) {
        if (!PictUtil.hasSDCard()) {
            return false;
        }
        Glide.with(this).load(wall.getUrl()).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                Bitmap mBitmap = resource;
                SetWallPaper(mBitmap);
                Logger.d("更新了壁纸");
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);
            }
        });
        return false;
    }

    private void saveImgFileToLocal(Wall wall, Bitmap mBitmap) {
        if (null != mBitmap) {
            //create a temporary directory within the cache folder
            File dir = new File(getBaseContext().getCacheDir() + "/images");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            //create the file
            File file = new File(dir, PictUtil.getImageFileName(wall.getUrl()));
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                PictUtil.saveToFile(file, mBitmap);
            } catch (IOException e) {
                Logger.e(e, "下载图片失败");
            }
        } else {
        }
    }

    //设置壁纸
    public void SetWallPaper(Bitmap bitmap) {
        WallpaperManager mWallManager = WallpaperManager.getInstance(this);
        try {
            mWallManager.setBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}