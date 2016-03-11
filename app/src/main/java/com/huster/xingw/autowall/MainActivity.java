package com.huster.xingw.autowall;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.huster.xingw.autowall.Utils.Util;
import com.orhanobut.logger.Logger;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.processor.Utils;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.fab_quiz)
    FloatingActionButton fab;

    private Realm realm;
    public static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.w("更新了");
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context = getApplicationContext();
        realm.getInstance(this);
        fab.setImageResource(R.drawable.ic_action_rotate_left);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshGoods();
            }
        });
    }

    public void refreshGoods(){
        if (!Util.isnewday(realm)){
            return;
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
