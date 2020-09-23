package com.zb.module_home.windows;

import android.view.View;

import com.zb.lib_base.model.GiftInfo;
import com.zb.lib_base.windows.BasePopupWindow;
import com.zb.module_home.BR;
import com.zb.module_home.R;

public class GiveSuccessPW extends BasePopupWindow {

    private GiftInfo giftInfo;
    private int giftNum;

    public GiveSuccessPW(View parentView, GiftInfo giftInfo, int giftNum) {
        super(parentView, true);
        this.giftInfo = giftInfo;
        this.giftNum = giftNum;
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
        mBinding.setVariable(BR.giftNum, giftNum);
    }

    @Override
    public void sure(View view) {
        super.sure(view);
        dismiss();
    }
}
