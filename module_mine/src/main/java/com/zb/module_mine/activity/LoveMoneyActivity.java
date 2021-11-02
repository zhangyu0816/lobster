package com.zb.module_mine.activity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.vm.LoveMoneyViewModel;

@Route(path = RouteUtils.Mine_LoveMoney)
public class LoveMoneyActivity extends BaseScreenActivity{
    @Override
    public int getRes() {
        return R.layout.ac_love_money;
    }

    @Override
    public void initUI() {
        LoveMoneyViewModel viewModel = new LoveMoneyViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel,viewModel);
    }
}
