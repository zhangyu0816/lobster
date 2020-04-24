package com.zb.lib_base.windows;

import android.view.View;

import com.zb.lib_base.BR;
import com.zb.lib_base.R;

import androidx.appcompat.app.AppCompatActivity;

public class BottleQuestionPW extends BasePopupWindow {

    public BottleQuestionPW(AppCompatActivity activity, View parentView) {
        super(activity, parentView,true);
        initUI();
    }

    @Override
    public int getRes() {
        return R.layout.pws_bottle_question;
    }

    @Override
    public void initUI() {
        mBinding.setVariable(BR.pw, this);
    }

    @Override
    public void sure(View view) {
        super.sure(view);
        dismiss();
    }
}