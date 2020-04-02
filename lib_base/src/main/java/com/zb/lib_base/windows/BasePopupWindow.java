package com.zb.lib_base.windows;

import android.view.View;
import android.widget.PopupWindow;

import androidx.appcompat.app.AppCompatActivity;

public class BasePopupWindow extends PopupWindow {

    public AppCompatActivity activity;

    public BasePopupWindow(AppCompatActivity activity) {
        this.activity = activity;
    }

    /**
     * 取消
     *
     * @param view
     */
    public void cancel(View view) {
    }

    /**
     * 确认
     *
     * @param view
     */
    public void sure(View view) {
    }
}
