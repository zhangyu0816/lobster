package com.zb.lib_base.utils;

import com.alibaba.android.arouter.launcher.ARouter;

import androidx.fragment.app.Fragment;

public class FragmentUtils {
    // 首页
    public static Fragment getHomeFragment() {
        return (Fragment) ARouter.getInstance().build(RouteUtils.Home_Fragment).navigation();
    }

    // 首页 -- 关注
    public static Fragment getHomeFollowFragment() {
        return (Fragment) ARouter.getInstance().build(RouteUtils.Home_Follow_Fragment).navigation();
    }

    // 卡片
    public static Fragment getCardFragment() {
        return (Fragment) ARouter.getInstance().build(RouteUtils.Card_Fragment).navigation();
    }

    // 用户详情 -- 动态
    public static Fragment getCardMemberDiscoverFragment(long userId) {
        return (Fragment) ARouter.getInstance().build(RouteUtils.Member_Discover_Fragment).withLong("userId", userId).navigation();
    }

    // 用户详情 -- 小视频
    public static Fragment getCardMemberVideoFragment(long userId) {
        return (Fragment) ARouter.getInstance().build(RouteUtils.Member_Video_Fragment).withLong("userId", userId).navigation();
    }

    // 对话
    public static Fragment getChatFragment() {
        return (Fragment) ARouter.getInstance().build(RouteUtils.Chat_Fragment).navigation();
    }

    // 对话 - 匹配
    public static Fragment getChatPairFragment() {
        return (Fragment) ARouter.getInstance().build(RouteUtils.Chat_Pair_Fragment).navigation();
    }

    // 对话 - 列表
    public static Fragment getChatListFragment() {
        return (Fragment) ARouter.getInstance().build(RouteUtils.Chat_List_Fragment).navigation();
    }

    // 我的
    public static Fragment getMineFragment() {
        return (Fragment) ARouter.getInstance().build(RouteUtils.Mine_Fragment).navigation();
    }
}
