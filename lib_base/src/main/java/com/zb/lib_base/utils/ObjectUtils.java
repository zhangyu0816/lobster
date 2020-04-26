package com.zb.lib_base.utils;

import com.zb.lib_base.R;
import com.zb.lib_base.app.MineApp;

import java.util.Map;

public class ObjectUtils {

    // 根据屏幕宽获取尺寸
    public static int getViewSizeByWidth(float scale) {
        return (int) (MineApp.W * scale);
    }

    // 根据屏幕高获取尺寸
    public static int getViewSizeByHeight(float scale) {
        return (int) (MineApp.H * scale);
    }

    // 根据屏幕宽获取尺寸
    public static int getViewSizeByWidthFromMax(int width) {
        return (int) (MineApp.W * (float)width / 1080f);
    }

    // 根据屏幕高获取尺寸
    public static int getViewSizeByHeightFromMax(int height) {
        return (int) (MineApp.H * (float)height / 1920f);
    }

    // 根据屏幕宽获取尺寸
    public static int getViewSizeByWidthFromMax750(int width) {
        return (int) (MineApp.W * width / 750f);
    }

    // 根据屏幕高获取尺寸
    public static int getViewSizeByHeightFromMax1334(int width, int height) {
        return (int) (getViewSizeByWidthFromMax750(width) * (float) height / (float) width);
    }

    public static int getLogoHeight(float scale) {
        return (int) (getViewSizeByWidth(scale) * 510f / 345f);
    }

    public static int getLogoWidth(float scale) {
        return (int) (getViewSizeByHeight(scale) * 345f / 510f);
    }

    public static int getVipBgHeight(float scale) {
        return (int) (getViewSizeByWidth(scale) * 254f / 1035f);
    }

    public static int getVipIntroHeight() {
        return (int) (MineApp.W * 2111f / 1125f);
    }

    public static int getVipIntroBgHeight(float scale) {
        return (int) (getViewSizeByWidth(scale) * 458f / 1035f);
    }

    // 默认图片
    public static int getDefaultRes() {
        return R.mipmap.empty_icon;
    }
    // 超级喜欢
    public static int getSuperLikeRes() {
        return R.mipmap.super_like_icon;
    }

    // 线条颜色
    public static int getLineColor() {
        return R.color.black_efe;
    }

    // 获取选中时显示的数量
    public static String getSelectCount(Map map, String s) {
        return map.containsKey(s) ? map.get(s) + "" : "";
    }

    // 最大输入字数
    public static String editMax(String content, int max) {
        return content.length() + "/" + max;
    }

    // 是不是视频
    public static boolean isVideo(String filePath) {
        return filePath.contains(".mp4");
    }

}
