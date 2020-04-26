package com.zb.module_mine.activity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.vm.GiftNewsViewModel;

@Route(path = RouteUtils.Mine_Gift_News)
public class GiftNewsActivity extends MineBaseActivity {
    @Override
    public int getRes() {
        return R.layout.mine_gift_news;
    }

    @Override
    public void initUI() {
        GiftNewsViewModel viewModel = new GiftNewsViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.noData,true);
        mBinding.setVariable(BR.title,"我的礼物");
    }
}
