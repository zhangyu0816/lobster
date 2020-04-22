package com.zb.lib_base.views.card;

import android.view.View;

public interface OnSwipeListener<T> {

    /**
     * 卡片还在滑动时回调
     *
     * @param view 该滑动卡片的viewHolder
     * @param ratio      滑动进度的比例
     * @param direction  卡片滑动的方向，CardConfig.SWIPING_LEFT 为向左滑，CardConfig.SWIPING_RIGHT 为向右滑，
     *                   CardConfig.SWIPING_NONE 为不偏左也不偏右
     */
    void onSwiping(View view, float ratio, int direction);

    /**
     * 卡片完全滑出时回调
     *
     * @param view 该滑出卡片的viewHolder
     * @param t          该滑出卡片的数据
     * @param direction  卡片滑出的方向，CardConfig.SWIPED_LEFT 为左边滑出；CardConfig.SWIPED_RIGHT 为右边滑出
     */
    void onSwiped(View view, T t, int direction);

    /**
     * 所有的卡片全部滑出时回调
     */
    void onSwipedClear();

}
