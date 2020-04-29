package com.zb.lib_base.windows;

import android.view.View;

import com.zb.lib_base.BR;
import com.zb.lib_base.R;

import androidx.appcompat.app.AppCompatActivity;

public class TextPW extends BasePopupWindow {
    private String content = "";
    private String title = "";
    private CallBack mCallBack;

    public TextPW(AppCompatActivity activity, View parentView, String title, String content) {
        super(activity, parentView, true);
        this.title = title;
        this.content = content;
        initUI();
    }

    public TextPW(AppCompatActivity activity, View parentView, String title, String content, CallBack callBack) {
        super(activity, parentView, true);
        this.title = title;
        this.content = content;
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
    }

    @Override
    public void sure(View view) {
        super.sure(view);
        if (mCallBack != null)
            mCallBack.sure();
        dismiss();
    }

    public interface CallBack {
        void sure();
    }
}
