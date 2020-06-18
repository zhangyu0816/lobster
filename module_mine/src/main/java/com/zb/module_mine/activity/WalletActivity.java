package com.zb.module_mine.activity;

import android.content.Context;
import android.content.Intent;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.vm.WalletViewModel;

@Route(path = RouteUtils.Mine_Wallet)
public class WalletActivity extends MineBaseActivity {
    private BaseReceiver updateWalletReceiver;

    @Override
    public int getRes() {
        return R.layout.mine_wallet;
    }

    @Override
    public void initUI() {
        fitComprehensiveScreen();
        WalletViewModel viewModel = new WalletViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.title, "我的钱包");
        mBinding.setVariable(BR.right, "账单明细");
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
