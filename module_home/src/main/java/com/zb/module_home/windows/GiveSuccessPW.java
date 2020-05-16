package com.zb.module_home.windows;

import android.view.View;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.model.GiftInfo;
import com.zb.lib_base.windows.BasePopupWindow;
import com.zb.module_home.BR;
import com.zb.module_home.R;

public class GiveSuccessPW extends BasePopupWindow {

    private GiftInfo giftInfo;

    public GiveSuccessPW(RxAppCompatActivity activity, View parentView, GiftInfo giftInfo) {
        super(activity, parentView, true);
        this.giftInfo = giftInfo;
        initUI();
    }

    @Override
    public int getRes() {
        return R.layout.pws_give_success;
    }

    @Override
    public void initUI() {
        mBinding.setVariable(BR.pw, this);
        mBinding.setVariable(BR.giftInfo, giftInfo);
    }

    @Override
    public void sure(View view) {
        super.sure(view);
        dismiss();
    }
}
