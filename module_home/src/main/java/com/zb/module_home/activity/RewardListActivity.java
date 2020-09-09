package com.zb.module_home.activity;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_home.BR;
import com.zb.module_home.R;
import com.zb.module_home.vm.RewardListViewModel;

@Route(path = RouteUtils.Home_Reward_List)
public class RewardListActivity extends HomeBaseActivity {
    @Autowired(name = "friendDynId")
    long friendDynId;

    private RewardListViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.home_reward_list;
    }

    @Override
    public void initUI() {
        viewModel = new RewardListViewModel();
        viewModel.friendDynId = friendDynId;
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.noData, true);
        mBinding.setVariable(BR.remark, "暂无排行榜记录");
    }

}
