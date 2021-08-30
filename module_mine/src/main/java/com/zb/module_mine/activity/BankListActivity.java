package com.zb.module_mine.activity;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.vm.BankListViewModel;


@Route(path = RouteUtils.Mine_Bank_List)
public class BankListActivity extends MineBaseActivity {
    @Autowired(name = "isSelect")
    boolean isSelect;

    private BankListViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.mine_bank_list;
    }

    @Override
    public void initUI() {
        viewModel = new BankListViewModel();
        viewModel.isSelect = isSelect;
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.title, "我的银行卡");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinding = null;
        viewModel = null;
    }
}
