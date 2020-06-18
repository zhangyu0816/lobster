package com.zb.module_mine.activity;

import android.content.Context;
import android.content.Intent;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.vm.GiftRecordViewModel;

@Route(path = RouteUtils.Mine_Gift_Record)
public class GiftRecordActivity extends MineBaseActivity {
    private BaseReceiver updateWalletReceiver;


    @Override
    public int getRes() {
        return R.layout.mine_gift_record;
    }

    @Override
    public void initUI() {
        fitComprehensiveScreen();
        GiftRecordViewModel viewModel = new GiftRecordViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.title, "礼物收益");
        mBinding.setVariable(BR.right, "帮助");
        updateWalletReceiver = new BaseReceiver(activity, "lobster_updateWallet") {
            @Override
            public void onReceive(Context context, Intent intent) {
                mBinding.setVariable(BR.walletInfo, MineApp.walletInfo);
            }
        };
        mBinding.setVariable(BR.walletInfo, MineApp.walletInfo);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateWalletReceiver.unregisterReceiver();
    }
}
