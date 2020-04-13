package com.zb.lib_base.utils;

import com.alibaba.android.arouter.launcher.ARouter;

import androidx.fragment.app.Fragment;

public class FragmentUtils {
    // 首页
    public static Fragment getHomeFragment() {
        Fragment fragment = (Fragment) ARouter.getInstance().build(RouteUtils.Home_Fragment).navigation();
        return fragment;
    }

    // 首页 -- 关注
    public static Fragment getHomeFollowFragment() {
        Fragment fragment = (Fragment) ARouter.getInstance().build(RouteUtils.Home_Follow_Fragment).navigation();
        return fragment;
    }

    // 首页 -- 推荐
    public static Fragment getHomeRecommendFragment() {
        Fragment fragment = (Fragment) ARouter.getInstance().build(RouteUtils.Home_Recommend_Fragment).navigation();
        return fragment;
    }

    // 首页 -- 小视频
    public static Fragment getHomeVideoFragment() {
        Fragment fragment = (Fragment) ARouter.getInstance().build(RouteUtils.Home_Video_Fragment).navigation();
        return fragment;
    }

    // 卡片
    public static Fragment getCardFragment() {
        Fragment fragment = (Fragment) ARouter.getInstance().build(RouteUtils.Card_Fragment).navigation();
        return fragment;
    }

    // 对话
    public static Fragment getChatFragment() {
        Fragment fragment = (Fragment) ARouter.getInstance().build(RouteUtils.Chat_Fragment).navigation();
        return fragment;
    }

    // 我的
    public static Fragment getMineFragment() {
        Fragment fragment = (Fragment) ARouter.getInstance().build(RouteUtils.Mine_Fragment).navigation();
        return fragment;
    }

    // 选择相册
    public static Fragment getCameraPictureFragment() {
        Fragment fragment = (Fragment) ARouter.getInstance().build(RouteUtils.Camera_Picture_Fragment).navigation();
        return fragment;
    }

    // 选择录像
    public static Fragment getCameraVideoFragment() {
        Fragment fragment = (Fragment) ARouter.getInstance().build(RouteUtils.Camera_Video_Fragment).navigation();
        return fragment;
    }
}
