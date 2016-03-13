package com.huster.xingw.autowall.service;

import android.app.ActivityManager;
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
import com.huster.xingw.autowall.Utils.PictUtil;
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
public class service extends Service {
    boolean isServiceRunning = false;
    Realm mRealm;

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        BootBroadcastReceiver receiver = new BootBroadcastReceiver();
        registerReceiver(receiver, filter);
        mRealm = Realm.getInstance(this);
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (true) {
                    Wall wall = mRealm.where(Wall.class).findFirst();
                    if (!wall.isToday()) {
                        //检查是否联网
                        if (Util.isWifiEnabled(getBaseContext())) {
                            try {
                                sleep(60000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {
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

    public void refreshGoods() {
        if (!Util.isnewday(mRealm)) {
            Toast.makeText(this, "今天没啥好更新的", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "新的一天到来了，待我更新壁纸", Toast.LENGTH_SHORT).show();
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
            if (null != goodsResult && null != goodsResult.getResults()) {
                mRealm.beginTransaction();
                for (Goods goods : goodsResult.getResults()) {
                    Wall wall = Wall.queryImageById(mRealm, goods.get_id());
                    if (null == wall) wall = mRealm.createObject(Wall.class);
                    Wall.updateDbGoods(wall, goods);
                    setImageToWallpaper(wall);
                }
                mRealm.commitTransaction();
                return;
            }
            mRealm.cancelTransaction();
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
                try {
                    setWallpaper(mBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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