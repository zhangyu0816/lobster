package com.zb.module_card.windows;

import android.view.View;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.windows.BasePopupWindow;
import com.zb.module_card.BR;
import com.zb.module_card.R;


public class SuperLikePW extends BasePopupWindow {

    private String myHead;
    private String otherHead;
    private boolean isPair;
    private int mySex;
    private int otherSex;

    public SuperLikePW(RxAppCompatActivity activity, View parentView, String myHead, String otherHead, boolean isPair, int mySex, int otherSex) {
        super(activity, parentView, true);
        this.myHead = myHead;
        this.otherHead = otherHead;
        this.isPair = isPair;
        this.mySex = mySex;
        this.otherSex = otherSex;
        initUI();
    }

    @Override
    public int getRes() {
        return R.layout.pws_super_like;
    }

    @Override
    public void initUI() {
        mBinding.setVariable(BR.myHead, myHead);
        mBinding.setVariable(BR.otherHead, otherHead);
        mBinding.setVariable(BR.isPair, isPair);
        mBinding.setVariable(BR.mySex, mySex);
        mBinding.setVariable(BR.otherSex, otherSex);
    }
}
