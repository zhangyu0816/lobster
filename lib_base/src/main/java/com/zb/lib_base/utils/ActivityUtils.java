package com.zb.lib_base.utils;

import com.alibaba.android.arouter.launcher.ARouter;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityUtils {
    /*********************** 首页 **************************/
    /*********************** 卡片 **************************/
    /*********************** 对话 **************************/
    /*********************** 我的 **************************/


    /*********************** 注册 **************************/

    // 注册主页
    public static void getRegisterMain() {
        ARouter.getInstance().build(RouteUtils.Register_Main).navigation();
    }

    // 注册--登录
    public static void getRegisterLogin() {
        ARouter.getInstance().build(RouteUtils.Register_Login).navigation();
    }

    // 注册--昵称
    public static void getRegisterNick() {
        ARouter.getInstance().build(RouteUtils.Register_Nick).navigation();
    }

    // 注册--生日
    public static void getRegisterBirthday() {
        ARouter.getInstance().build(RouteUtils.Register_Birthday).navigation();
    }

    // 注册--手机号
    public static void getRegisterPhone(boolean isLogin) {
        ARouter.getInstance().build(RouteUtils.Register_Phone).withBoolean("isLogin", isLogin).navigation();
    }

    // 注册--验证码
    public static void getRegisterCode(boolean isLogin) {
        ARouter.getInstance().build(RouteUtils.Register_Code).withBoolean("isLogin", isLogin).navigation();
    }

    // 注册--头像
    public static void getRegisterLogo() {
        ARouter.getInstance().build(RouteUtils.Register_Logo).navigation();
    }

    // 注册--多图
    public static void getRegisterImages() {
        ARouter.getInstance().build(RouteUtils.Register_Images).navigation();
    }

    /*********************** 相册 **************************/

    // 相册主页
    public static void getCameraMain(AppCompatActivity activity) {
        ARouter.getInstance().build(RouteUtils.Camera_Main).navigation(activity, 1001);
    }
}
