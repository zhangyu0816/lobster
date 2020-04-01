package com.zb.lib_base.utils;

import com.alibaba.android.arouter.launcher.ARouter;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityUtils {
    /*********************** 首页 **************************/
    // 首页主activity
    public static AppCompatActivity getHomeMainActivity() {
        AppCompatActivity activity = (AppCompatActivity) ARouter.getInstance().build(RouteUtils.Home_Main).navigation();
        return activity;
    }


    /*********************** 我的 **************************/
    // 我的主activity
    public static AppCompatActivity getMineMainActivity() {
        AppCompatActivity activity = (AppCompatActivity) ARouter.getInstance().build(RouteUtils.Mine_Main).navigation();
        return activity;
    }

    /*********************** 注册 **************************/

    // 注册主页
    public static AppCompatActivity getRegisterMain() {
        AppCompatActivity activity = (AppCompatActivity) ARouter.getInstance().build(RouteUtils.Register_Main).navigation();
        return activity;
    }

    // 注册--登录
    public static AppCompatActivity getRegisterLogin() {
        AppCompatActivity activity = (AppCompatActivity) ARouter.getInstance().build(RouteUtils.Register_Login).navigation();
        return activity;
    }
}
