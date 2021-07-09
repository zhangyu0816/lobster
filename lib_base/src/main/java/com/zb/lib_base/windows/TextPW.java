package com.zb.lib_base.windows;

import android.annotation.SuppressLint;
import android.view.View;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.BR;
import com.zb.lib_base.R;

public class TextPW extends BasePopupWindow {
    private String content;
    private String title;
    private CallBack mCallBack;
    private String btnName = "我知道了";
    private boolean showCancel = false;

    public TextPW(View parentView, String title, String content) {
        super(parentView, true);
        this.title = title;
        this.content = content;
        initUI();
    }

    public TextPW(View parentView, String title, String content, String btnName, CallBack callBack) {
        super(parentView, true);
        this.title = title;
        this.content = content;
        this.btnName = btnName;
        mCallBack = callBack;
        initUI();
    }

    public TextPW(View parentView, String title, String content, boolean canClick, CallBack callBack) {
        super(parentView, canClick);
        this.title = title;
        this.content = content;
        this.btnName = "明白了";
        mCallBack = callBack;
        initUI();
    }

    @SuppressLint("ClickableViewAccessibility")
    public TextPW(View parentView, String title, String content, String btnName, boolean canClick, CallBack callBack) {
        super(parentView, canClick);
        this.title = title;
        this.content = content;
        this.btnName = btnName;
        mCallBack = callBack;
        mBinding.getRoot().setOnTouchListener((v, event) -> {
            if (isShowing()) {
                dismiss();
                if (mCallBack != null)
                    mCallBack.cancel();
            }
            return false;
        });
        initUI();
    }

    @SuppressLint("ClickableViewAccessibility")
    public TextPW(RxAppCompatActivity activity, View parentView, String title, String content, String btnName, boolean canClick, CallBack callBack) {
        super(activity, parentView, canClick);
        this.title = title;
        this.content = content;
        this.btnName = btnName;
        mCallBack = callBack;
        mBinding.getRoot().setOnTouchListener((v, event) -> {
            if (isShowing()) {
                dismiss();
                if (mCallBack != null)
                    mCallBack.cancel();
            }
            return false;
        });
        initUI();
    }

    @SuppressLint("ClickableViewAccessibility")
    public TextPW(RxAppCompatActivity activity, View parentView, String title, String content, String btnName, boolean canClick, boolean showCancel, CallBack callBack) {
        super(activity, parentView, false);
        this.title = title;
        this.content = content;
        this.btnName = btnName;
        this.showCancel = showCancel;
        mCallBack = callBack;
        initUI();
    }

    @Override
    public int getRes() {
        return R.layout.pws_text;
    }

    @Override
    public void initUI() {
        mBinding.setVariable(BR.pw, this);
        mBinding.setVariable(BR.title, title);
        mBinding.setVariable(BR.content, content);
        mBinding.setVariable(BR.btnName, btnName);
        mBinding.setVariable(BR.showCancel, showCancel);
    }

    @Override
    public void cancel(View view) {
        super.cancel(view);
        dismiss();
        if (mCallBack != null)
            mCallBack.cancel();
    }

    @Override
    public void sure(View view) {
        super.sure(view);
        dismiss();
        if (mCallBack != null)
            mCallBack.sure();
    }

    @FunctionalInterface
    public interface CallBack {
        void sure();

        default void cancel() {
        }
    }
}
