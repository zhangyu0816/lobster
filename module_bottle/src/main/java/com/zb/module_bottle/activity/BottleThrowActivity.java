package com.zb.module_bottle.activity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_bottle.BR;
import com.zb.module_bottle.R;
import com.zb.module_bottle.vm.BottleThrowViewModel;

@Route(path = RouteUtils.Bottle_Throw)
public class BottleThrowActivity extends BottleBaseActivity {
    @Override
    public int getRes() {
        return R.layout.bottle_throw;
    }

    @Override
    public void initUI() {
        BottleThrowViewModel viewModel = new BottleThrowViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel,viewModel);
    }
}
