package com.zb.module_mine.activity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.vm.WithdrawViewModel;

@Route(path = RouteUtils.Mine_Withdraw)
public class WithdrawActivity extends MineBaseActivity {
    private WithdrawViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.mine_withdraw;
    }

    @Override
    public void initUI() {
        fitComprehensiveScreen();
        viewModel = new WithdrawViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.title, "提现");
        mBinding.setVariable(BR.right, "");
        mBinding.setVariable(BR.money, "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
    }
}
