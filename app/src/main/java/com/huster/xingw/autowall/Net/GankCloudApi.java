package com.huster.xingw.autowall.Net;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.huster.xingw.autowall.MainActivity;
import com.huster.xingw.autowall.Model.GoodsResult;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.io.IOException;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by Xingw on 2016/3/10.
 */
public class GankCloudApi {

    public static GankCloudApi instance;

    public static GankCloudApi getIns(Context context) {
        if (null == instance) {
            synchronized (GankCloudApi.class) {
                if (null == instance) {
                    instance = new GankCloudApi(context);
                }
            }
        }
        return instance;
    }

    private final GankCloudService mWebService;
    public static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

    public GankCloudApi(Context context) {
        Cache cache;
        OkHttpClient okHttpClient = null;
        try {
            File cacheDir = new File(context.getCacheDir().getPath(),
                    "gank_cache.json") ;
            cache = new Cache(cacheDir,10 * 1024 * 1024);
            okHttpClient = new OkHttpClient();
            okHttpClient.setCache(cache);
        } catch (IOException e) {
            e.printStackTrace();
        }

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://gank.avosapps.com/api")
                .setClient(new OkClient(okHttpClient))
                .setConverter(new GsonConverter(gson))
                .setRequestInterceptor(mRequestInterceptor)
                .build();
        mWebService = restAdapter.create(GankCloudService.class);
    }

    private RequestInterceptor mRequestInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            request.addHeader("Cache-Control", "public, max-age=" + 60 * 60 * 4);
            request.addHeader("Content-Type", "application/json");
        }
    };

    public interface GankCloudService {

        @GET("/data/Android/{limit}/{page}")
        Observable<GoodsResult> getAndroidGoods(
                @Path("limit") int limit,
                @Path("page") int page
        );

        @GET("/data/iOS/{limit}/{page}")
        Observable<GoodsResult> getIosGoods(
                @Path("limit") int limit,
                @Path("page") int page
        );

        @GET("/data/all/{limit}/{page}")
        Observable<GoodsResult> getAllGoods(
                @Path("limit") int limit,
                @Path("page") int page
        );

        @GET("/data/福利/{limit}/{page}")
        Observable<GoodsResult> getBenefitsGoods(
                @Path("limit") int limit,
                @Path("page") int page
        );
    }
    public Observable<GoodsResult> getBenefitsGoods(int limit, int page){
        return mWebService.getBenefitsGoods(limit,page);
    }

}
