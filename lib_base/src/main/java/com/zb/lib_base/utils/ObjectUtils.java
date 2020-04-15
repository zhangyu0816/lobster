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
        return (int) (MineApp.W * width / 1080f);
    }

    // 根据屏幕高获取尺寸
    public static int getViewSizeByHeightFromMax(int height) {
        return (int) (MineApp.H * height / 1920f);
    }

    public static int getLogoHeight(float scale) {
        return (int) (getViewSizeByWidth(scale) * 510f / 345f);
    }

    public static int getLogoWidth(float scale) {
        return (int) (getViewSizeByHeight(scale) * 345f / 510f);
    }

    // 默认图片
    public static int getDefaultRes() {
        return R.mipmap.empty_icon;
    }

    // 上传图片
    public static int getAddImageRes() {
        return R.mipmap.add_image_icon;
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
        if (filePath.contains(".mp4")
                || filePath.contains(".3gp")
                || filePath.contains(".wmv")
                || filePath.contains(".ts")
                || filePath.contains(".rmvb")
                || filePath.contains(".mov")
                || filePath.contains(".m4v")
                || filePath.contains(".avi")
                || filePath.contains(".m3u8")
                || filePath.contains(".3gpp")
                || filePath.contains(".3gpp2")
                || filePath.contains(".mkv")
                || filePath.contains(".flv")
                || filePath.contains(".divx")
                || filePath.contains(".f4v")
                || filePath.contains(".rm")
                || filePath.contains(".asf")
                || filePath.contains(".ram")
                || filePath.contains(".mpg")
                || filePath.contains(".v8")
                || filePath.contains(".swf")
                || filePath.contains(".m2v")
                || filePath.contains(".asx")
                || filePath.contains(".ra")
                || filePath.contains(".ndivx")
                || filePath.contains(".xvid")) {
            return true;
        }
        return false;
    }
}
