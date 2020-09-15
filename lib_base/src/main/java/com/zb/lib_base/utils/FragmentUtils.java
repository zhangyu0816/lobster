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

    // 卡片
    public static Fragment getCardFragment() {
        Fragment fragment = (Fragment) ARouter.getInstance().build(RouteUtils.Card_Fragment).navigation();
        return fragment;
    }

    // 用户详情 -- 动态
    public static Fragment getCardMemberDiscoverFragment(long userId) {
        Fragment fragment = (Fragment) ARouter.getInstance().build(RouteUtils.Member_Discover_Fragment).withLong("userId", userId).navigation();
        return fragment;
    }

    // 用户详情 -- 小视频
    public static Fragment getCardMemberVideoFragment(long userId) {
        Fragment fragment = (Fragment) ARouter.getInstance().build(RouteUtils.Member_Video_Fragment).withLong("userId", userId).navigation();
        return fragment;
    }

    // 对话
    public static Fragment getChatFragment() {
        Fragment fragment = (Fragment) ARouter.getInstance().build(RouteUtils.Chat_Fragment).navigation();
        return fragment;
    }

    // 对话 - 匹配
    public static Fragment getChatPairFragment() {
        Fragment fragment = (Fragment) ARouter.getInstance().build(RouteUtils.Chat_Pair_Fragment).navigation();
        return fragment;
    }

    // 对话 - 列表
    public static Fragment getChatListFragment() {
        Fragment fragment = (Fragment) ARouter.getInstance().build(RouteUtils.Chat_List_Fragment).navigation();
        return fragment;
    }

    // 我的
    public static Fragment getMineFragment() {
        Fragment fragment = (Fragment) ARouter.getInstance().build(RouteUtils.Mine_Fragment).navigation();
        return fragment;
    }
}
