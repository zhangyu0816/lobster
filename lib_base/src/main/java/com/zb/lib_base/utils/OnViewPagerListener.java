package com.zb.lib_base.utils;

import android.view.View;

public interface OnViewPagerListener {
    //停止播放的监听
    void onPageRelease(boolean isNest, View view);

    //播放的监听
    void onPageSelected(boolean isButton, View view);


}
