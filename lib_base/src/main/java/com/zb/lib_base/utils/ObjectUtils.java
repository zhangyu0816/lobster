package com.zb.lib_base.utils;

import com.zb.lib_base.R;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.vm.BaseViewModel;

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

    // 线条颜色
    public static int getLineColor() {
        return R.color.black_efe;
    }

    // 获取选中时显示的数量
    public static String getSelectCount(Map map, int position) {
        return map.containsKey(position) ? map.get(position) + "" : "";
    }

}
