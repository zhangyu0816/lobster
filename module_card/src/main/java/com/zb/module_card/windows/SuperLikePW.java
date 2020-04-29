package com.zb.module_card.windows;

import android.view.View;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.windows.BasePopupWindow;
import com.zb.module_card.BR;
import com.zb.module_card.R;


public class SuperLikePW extends BasePopupWindow {

    public SuperLikePW(RxAppCompatActivity activity, View parentView) {
        super(activity, parentView, true);
        initUI();
    }

    @Override
    public int getRes() {
        return R.layout.pws_super_like;
    }

    @Override
    public void initUI() {
        mBinding.setVariable(BR.myHead, "");
        mBinding.setVariable(BR.otherHead, "");
    }
}
