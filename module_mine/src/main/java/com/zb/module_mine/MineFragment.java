package com.zb.module_mine;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseFragment;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.vm.MineViewModel;

@Route(path = RouteUtils.Mine_Fragment)
public class MineFragment extends BaseFragment {

    @Override
    public int getRes() {
        return R.layout.mine_frag;
    }

    @Override
    public void initUI() {
        MineViewModel viewModel = new MineViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
    }
}
