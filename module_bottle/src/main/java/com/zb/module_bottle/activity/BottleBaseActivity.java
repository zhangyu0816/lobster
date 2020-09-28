package com.zb.module_bottle.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.module_bottle.R;

public abstract class BottleBaseActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.BottleTheme);
        super.onCreate(savedInstanceState);
    }

    public void fitScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(View.SYSTEM_UI_FLAG_FULLSCREEN);// 导致华为手机模糊
            getWindow().addFlags(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);// 导致华为手机黑屏
            getWindow().addFlags(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            getWindow().addFlags(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }
}
