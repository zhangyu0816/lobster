package com.zb.module_mine.activity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.vm.LoveGetViewModel;

@Route(path = RouteUtils.Mine_LoveGet)
public class LoveGetActivity extends BaseScreenActivity {

    @Override
    public int getRes() {
        return R.layout.ac_love_get;
    }

    @Override
    public void initUI() {
        LoveGetViewModel viewModel = new LoveGetViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
    }
}
