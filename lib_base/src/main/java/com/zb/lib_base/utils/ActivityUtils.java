package com.zb.lib_base.utils;

import com.alibaba.android.arouter.launcher.ARouter;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityUtils {

    // 首页主activity
    public static AppCompatActivity getHomeMainActivity() {
        AppCompatActivity activity = (AppCompatActivity) ARouter.getInstance().build(RouteUtils.Home_Activity_Main).navigation();
        return activity;
    }
    // 我的主activity
    public static AppCompatActivity getMineMainActivity() {
        AppCompatActivity activity = (AppCompatActivity) ARouter.getInstance().build(RouteUtils.Mine_Activity_Main).navigation();
        return activity;
    }
}
