package com.zb.module_mine.activity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.vm.BindingBankViewModel;

@Route(path = RouteUtils.Mine_Binding_Bank)
public class BindingBankActivity extends MineBaseActivity {
    @Override
    public int getRes() {
        return R.layout.mine_binding_bank;
    }

    @Override
    public void initUI() {
        BindingBankViewModel viewModel = new BindingBankViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel,viewModel);
        mBinding.setVariable(BR.title, "绑定银行卡");
        mBinding.setVariable(BR.name, "");
        mBinding.setVariable(BR.bankAccount, "");
        mBinding.setVariable(BR.bankAddress, "");
    }
}
