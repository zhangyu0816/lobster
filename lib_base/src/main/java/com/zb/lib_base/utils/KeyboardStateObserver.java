package com.zb.lib_base.utils;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

public class KeyboardStateObserver {
    private static final String TAG = KeyboardStateObserver.class.getSimpleName();
    private static RxAppCompatActivity mActivity;

    public static KeyboardStateObserver getKeyboardStateObserver(RxAppCompatActivity activity) {
        mActivity = activity;
        return new KeyboardStateObserver(activity);
    }

    private View mChildOfContent;
    private int usableHeightPrevious;
    private OnKeyboardVisibilityListener listener;

    public void setKeyboardVisibilityListener(OnKeyboardVisibilityListener listener) {
        this.listener = listener;
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
            int heightDifference = usableHeightSansKeyboard - usableHeightNow - StatusBarUtil.getStatusBarHeight(mActivity);
            if (heightDifference > (usableHeightSansKeyboard / 4)) {
                if (listener != null) {
                    listener.onKeyboardHeight(heightDifference);
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
        return (r.bottom - r.top);// 全屏模式下： return r.bottom
    }

    public interface OnKeyboardVisibilityListener {
        void onKeyboardHeight(int height);
    }
}
