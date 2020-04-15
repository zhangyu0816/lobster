package com.zb.lib_base.windows;

import android.annotation.SuppressLint;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

public abstract class BasePopupWindow extends PopupWindow {

    public AppCompatActivity activity;
    public ViewDataBinding mBinding;

    @SuppressLint("ClickableViewAccessibility")
    public BasePopupWindow(AppCompatActivity activity, View parentView) {
        this.activity = activity;
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(activity), getRes(), null, false);
        View view = mBinding.getRoot();
        setWidth(LinearLayout.LayoutParams.FILL_PARENT);
        setHeight(LinearLayout.LayoutParams.FILL_PARENT);
        setBackgroundDrawable(new BitmapDrawable());
        setFocusable(true);
        setOutsideTouchable(true);
        setContentView(view);
        showAtLocation(parentView, Gravity.CENTER, 0, 0);
        update();

        view.setOnTouchListener((v, event) -> {
            if (isShowing()) {
                dismiss();
            }
            return false;
        });
    }

    public abstract int getRes();

    public abstract void initUI();

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

    public void selectIndex(int position) {
    }
}
