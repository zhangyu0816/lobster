package com.zb.module_mine.fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseFragment;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.vm.MineViewModel;

@Route(path = RouteUtils.Mine_Fragment)
public class MineFragment extends BaseFragment {
    private MineViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.mine_frag;
    }

    @Override
    public void initUI() {
        viewModel = new MineViewModel();
        viewModel.fm = getChildFragmentManager();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel,viewModel);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.onResume();
    }
}
