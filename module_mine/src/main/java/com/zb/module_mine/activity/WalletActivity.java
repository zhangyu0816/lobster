package com.zb.module_mine.activity;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.vm.WalletViewModel;

@Route(path = RouteUtils.Mine_Wallet)
public class WalletActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.MineTheme);
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getRes() {
        return R.layout.mine_wallet;
    }

    @Override
    public void initUI() {
        fitComprehensiveScreen();
        WalletViewModel viewModel = new WalletViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.title, "我的钱包");
        mBinding.setVariable(BR.right, "账单明细");
    }
}
