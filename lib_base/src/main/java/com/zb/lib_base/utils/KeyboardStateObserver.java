package com.zb.lib_base.utils;

import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.lang.reflect.Method;

public class KeyboardStateObserver {
    private static final String TAG = KeyboardStateObserver.class.getSimpleName();
    private static RxAppCompatActivity mActivity;
    private boolean isFull = false;

    public static KeyboardStateObserver getKeyboardStateObserver(RxAppCompatActivity activity) {
        mActivity = activity;
        return new KeyboardStateObserver(activity);
    }

    private View mChildOfContent;
    private int usableHeightPrevious;
    private OnKeyboardVisibilityListener listener;

    public void setKeyboardVisibilityListener(OnKeyboardVisibilityListener listener, boolean isFull) {
        this.listener = listener;
        this.isFull = isFull;
    }

    private KeyboardStateObserver(RxAppCompatActivity activity) {
        FrameLayout content = activity.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(this::possiblyResizeChildOfContent);
    }

    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
            boolean hasNavigationBar = checkDeviceHasNavigationBar();
            int heightDifference = usableHeightSansKeyboard - usableHeightNow - (isFull ? (hasNavigationBar ? StatusBarUtil.getStatusBarHeight(mActivity)*2 : 0) : (hasNavigationBar ? StatusBarUtil.getStatusBarHeight(mActivity) : 0));
            if (heightDifference > (usableHeightSansKeyboard / 5)) {
                if (listener != null) {
                    listener.onKeyboardHeight(heightDifference);
                }
            } else {
                if (listener != null) {
                    listener.onKeyboardHide();
                }
            }
            usableHeightPrevious = usableHeightNow;
            Log.d(TAG, "usableHeightNow: " + usableHeightNow + " | usableHeightSansKeyboard:" + usableHeightSansKeyboard + " | heightDifference:" + heightDifference);
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        Log.d(TAG, "rec bottom>" + r.bottom + " | rec top>" + r.top);
        return isFull ? r.bottom : (r.bottom - r.top);// 全屏模式下： return r.bottom
    }

    @FunctionalInterface
    public interface OnKeyboardVisibilityListener {
        void onKeyboardHeight(int height);

        default void onKeyboardHide() {
        }
    }

    //获取是否存在NavigationBar
    private boolean checkDeviceHasNavigationBar() {
        return getDpi() - mActivity.getWindowManager().getDefaultDisplay().getHeight() > 0;
    }

    private int getDpi() {
        int dpi = 0;
        Display display = mActivity.getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            dpi = dm.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dpi;
    }
}
