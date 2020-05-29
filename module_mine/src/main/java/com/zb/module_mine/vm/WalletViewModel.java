package com.zb.module_mine.vm;

import android.view.View;

import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.RechargePW;
import com.zb.module_mine.iv.WalletVMInterface;

public class WalletViewModel extends BaseViewModel implements WalletVMInterface {

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void right(View view) {
        super.right(view);
        ActivityUtils.getMineTranRecord(0);
    }

    @Override
    public void recharge(View view) {
        new RechargePW(activity, mBinding.getRoot());
    }

    @Override
    public void toGiftRecord(View view) {
        ActivityUtils.getMineGiftRecord();
    }

    @Override
    public void toGiveRecord(View view) {
        ActivityUtils.getMineGRGift(1);
    }

    @Override
    public void toGetRecord(View view) {
        ActivityUtils.getMineGRGift(2);
    }
}
