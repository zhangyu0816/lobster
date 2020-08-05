package com.zb.module_bottle.activity;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.model.BottleInfo;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_bottle.BR;
import com.zb.module_bottle.R;
import com.zb.module_bottle.vm.BottleContentViewModel;

@Route(path = RouteUtils.Bottle_Content)
public class BottleContentActivity extends BottleBaseActivity {

    @Autowired(name = "bottleInfo")
    BottleInfo bottleInfo;

    @Override
    public int getRes() {
        return R.layout.bottle_content;
    }

    @Override
    public void initUI() {
        fitComprehensiveScreen();
        BottleContentViewModel viewModel = new BottleContentViewModel();
        viewModel.bottleInfo = bottleInfo;
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.title, bottleInfo.getDriftBottleId() == 0 ? "扔一个瓶子" : "我的漂流瓶");
        mBinding.setVariable(BR.bottleInfo, bottleInfo);
    }
}
