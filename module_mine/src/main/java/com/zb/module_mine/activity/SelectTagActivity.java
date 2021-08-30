package com.zb.module_mine.activity;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.vm.SelectTagViewModel;

@Route(path = RouteUtils.Mine_Select_Tag)
public class SelectTagActivity extends MineBaseActivity {
    @Autowired(name = "serviceTags")
    String serviceTags;

    private SelectTagViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.mine_select_tag;
    }

    @Override
    public void initUI() {
        viewModel = new SelectTagViewModel();
        viewModel.serviceTags = serviceTags;
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.title, "我的标签");
        mBinding.setVariable(BR.showTag, !serviceTags.isEmpty());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinding = null;
        viewModel = null;
    }
}
