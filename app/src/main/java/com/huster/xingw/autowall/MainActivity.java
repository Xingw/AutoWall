package com.huster.xingw.autowall;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnLongClick;
import io.realm.Realm;
import io.realm.processor.Utils;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SET_WALLPAPER = 0x1001;
    @Bind(R.id.fab_quiz)
    FloatingActionButton fab;
    @Bind(R.id.FrmL_Activity_main)
    FrameLayout frameLayout;

    private static Realm mRealm;
    public static Context context;

    public static Context getContext() {
        return context;
    }

    /***
     * 获取福利图的回调接口，拿到数据用来做背景
     */
    private Observer<GoodsResult> getImageGoodsObserver = new Observer<GoodsResult>() {
        @Override
        public void onNext(final GoodsResult goodsResult) {
            if (null != goodsResult && null != goodsResult.getResults()) {
                mRealm.beginTransaction();
                for (Goods goods : goodsResult.getResults()) {
                    Wall wall = Wall.queryImageById(mRealm, goods.get_id());
                    if (null == wall) wall = mRealm.createObject(Wall.class);
                    Wall.updateDbGoods(wall, goods);
                    setImageToWallpaper(wall,true);
                }
                mRealm.commitTransaction();
                return;
            }
            mRealm.cancelTransaction();
        }

        @Override
        public void onCompleted() {
            Logger.d("获取背景图服务完成");
        }

        @Override
        public void onError(final Throwable error) {
            Logger.e(error, "获取背景图服务失败");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context = getApplicationContext();
        mRealm = Realm.getInstance(this);
        fab.setImageResource(R.drawable.ic_action_rotate_left);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshGoods();
            }
        });
        initView();
    }

    public void initView() {
        Wall wall = mRealm.where(Wall.class).findFirst();
        if(wall !=null)
        setImageToWallpaper(wall,false);
    }

    public void refreshGoods() {
        if (!Util.isnewday(mRealm)) {
            return;
        }

        GankCloudApi.getIns()
                .getBenefitsGoods(1, 1)
                .cache()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getImageGoodsObserver);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    private void checkFileAndSetWallPaper(File file) {
        if (null != file && file.exists()) {
            //get the contentUri for this file and start the intent
            Uri contentUri = FileProvider.getUriForFile(this, "com.huster.xingw.autowall.fileprovider", file);
            //get crop intent
            Intent intent = WallpaperManager.getInstance(this).getCropAndSetWallpaperIntent
                    (contentUri);
            //start activity for result so we can animate if we finish
            startActivityForResult(intent, REQUEST_CODE_SET_WALLPAPER);
        }
    }

    boolean setImageToWallpaper(final Wall wall, final boolean set) {
        if (!PictUtil.hasSDCard()) {
            Toast.makeText(getContext(), "不支持下载文件", Toast.LENGTH_SHORT).show();
            return false;
        }
        Glide.with(this).load(wall.getUrl()).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                Bitmap mBitmap = resource;
                frameLayout.setBackground(new BitmapDrawable(mBitmap));
                if(set) {
                    saveImgFileToLocal(wall, mBitmap);
                }
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);
                Toast.makeText(getContext(), "下载图片失败，请重试", Toast.LENGTH_SHORT).show();
            }
        });
        return false;
    }

    private void saveImgFileToLocal(Wall wall, Bitmap mBitmap) {
        if (null != mBitmap) {
            //create a temporary directory within the cache folder
            File dir = new File(getContext().getCacheDir() + "/images");
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
                Toast.makeText(getContext(), "下载图片失败，请重试", Toast.LENGTH_SHORT).show();
            } finally {
                checkFileAndSetWallPaper(file);
            }
        } else {
            Toast.makeText(getContext(), "下载图片失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_SET_WALLPAPER)
                Toast.makeText(this, "壁纸设置成功", Toast.LENGTH_SHORT).show();
        }
    }
}
