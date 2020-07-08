package com.zb.module_bottle.activity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_bottle.BR;
import com.zb.module_bottle.R;
import com.zb.module_bottle.vm.BottleListViewModel;

@Route(path = RouteUtils.Bottle_List)
public class BottleListActivity extends BottleBaseActivity {
    private BottleListViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.bottle_list;
    }

    @Override
    public void initUI() {
        fitComprehensiveScreen();
        viewModel = new BottleListViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.noData, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
    }
}
