package com.zb.lib_base.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
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
        int usableHeightNow = computeUsableHeight(mActivity);
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
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

    private int computeUsableHeight(RxAppCompatActivity activity) {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);

        Point point = getNavigationBarSize(activity);
        int bottomHeight = point.y;
        if (bottomHeight < 100)
            bottomHeight = bottomHeight * 2;
        return isFull ? (r.bottom + bottomHeight) : (r.bottom - r.top + bottomHeight);// 全屏模式下： return r.bottom
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

    public static Point getNavigationBarSize(RxAppCompatActivity context) {
        Point appUsableSize = getAppUsableScreenSize(context);
        Point realScreenSize = getRealScreenSize(context);

        // navigation bar on the right
        if (appUsableSize.x < realScreenSize.x) {
            return new Point(realScreenSize.x - appUsableSize.x, appUsableSize.y);
        }

        // navigation bar at the bottom
        if (appUsableSize.y < realScreenSize.y) {
            return new Point(appUsableSize.x, realScreenSize.y - appUsableSize.y);
        }

        // navigation bar is not present
        return new Point();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static Point getAppUsableScreenSize(RxAppCompatActivity context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static Point getRealScreenSize(RxAppCompatActivity context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();

        display.getRealSize(size);

        return size;
    }
}
