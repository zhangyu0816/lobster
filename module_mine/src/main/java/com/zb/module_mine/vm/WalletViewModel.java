package com.zb.module_mine.vm;

import android.view.View;

import com.zb.lib_base.model.WalletInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.iv.WalletVMInterface;

import androidx.databinding.ViewDataBinding;

public class WalletViewModel extends BaseViewModel implements WalletVMInterface {
    public WalletInfo walletInfo;
    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        walletInfo = new WalletInfo();
        walletInfo.setWallet(159f);
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void right(View view) {
        super.right(view);
    }

    @Override
    public void recharge(View view) {

    }

    @Override
    public void toGiftRecord(View view) {
        ActivityUtils.getMineGiftRecord();
    }

    @Override
    public void toGiveRecord(View view) {

    }

    @Override
    public void toGetRecord(View view) {

    }
}
