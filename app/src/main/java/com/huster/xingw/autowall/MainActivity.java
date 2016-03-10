package com.huster.xingw.autowall;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import butterknife.Bind;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.fab_quiz)
    FloatingActionButton fab;

    public static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_main);
        fab.setImageResource(R.drawable.ic_action_rotate_left);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshGoods();
            }
        });
    }

    public void refreshGoods(){

    }

}
