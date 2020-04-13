package com.zb.lib_base.utils;

import com.alibaba.android.arouter.launcher.ARouter;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityUtils {
    /*********************** 首页 **************************/
    // 图文动态
    public static void getHomePublishImage() {
        ARouter.getInstance().build(RouteUtils.Home_Publish_image).navigation();
    }
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
    public static void getCameraMain(AppCompatActivity activity, boolean isMore) {
        ARouter.getInstance().build(RouteUtils.Camera_Main).withBoolean("isMore", isMore).navigation(activity, 1001);
    }

    // 相册多选
    public static void getCameraMore(AppCompatActivity activity) {
        ARouter.getInstance().build(RouteUtils.Camera_More).navigation(activity, 1001);
    }
}
