package com.zb.lib_base.utils;

import com.zb.lib_base.app.MineApp;

/**
 * [简要描述]: 屏幕显示相关 帮助类 [详细描述]:
 *
 * @author [Jarry]
 * @date [Created 2014-2-24]
 * @package [com.easycity.manager.utils]
 * @see [DisplayUtils]
 * @since [EasyCityManager]
 */
public class DisplayUtils {
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(float dpValue) {
        final float scale = MineApp.getApp().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(int pxValue) {
        final float scale = MineApp.getApp().getResources().getDisplayMetrics().density;
        return (int) ((float) pxValue / scale + 0.5f);
    }
}
