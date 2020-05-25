package com.zb.module_bottle.activity;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_bottle.BR;
import com.zb.module_bottle.R;
import com.zb.module_bottle.vm.BottleChatViewModel;

@Route(path = RouteUtils.Bottle_Chat)
public class BottleChatActivity extends BottleBaseActivity {
    @Autowired(name = "driftBottleId")
    long driftBottleId;

    @Override
    public int getRes() {
        return R.layout.bottle_chat;
    }

    @Override
    public void initUI() {
        BottleChatViewModel viewModel = new BottleChatViewModel();
        viewModel.driftBottleId = driftBottleId;
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
    }
}
