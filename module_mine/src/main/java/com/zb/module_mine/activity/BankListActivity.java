package com.zb.module_mine.activity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.vm.BankListViewModel;


@Route(path = RouteUtils.Mine_Bank_List)
public class BankListActivity extends MineBaseActivity {
    @Override
    public int getRes() {
        return R.layout.mine_bank_list;
    }

    @Override
    public void initUI() {
        BankListViewModel viewModel = new BankListViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.title, "我的银行卡");
    }
}
