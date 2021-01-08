package com.zb.module_card.activity;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_card.BR;
import com.zb.module_card.R;
import com.zb.module_card.vm.DiscoverListViewModel;

@Route(path = RouteUtils.Card_Discover_List)
public class DiscoverListActivity extends CardBaseActivity {
    @Autowired(name = "userId")
    long userId;
    @Autowired(name = "isAttention")
    boolean isAttention;
    @Autowired(name = "memberInfo")
    MemberInfo memberInfo;

    @Override
    public int getRes() {
        return R.layout.card_discover_list;
    }

    @Override
    public void initUI() {
        MineApp.getApp().getActivityMap().put("DiscoverListActivity", activity);
        DiscoverListViewModel viewModel = new DiscoverListViewModel();
        viewModel.otherUserId = userId;
        viewModel.memberInfo = memberInfo;
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.isAttention, isAttention);
        mBinding.setVariable(BR.memberInfo, memberInfo);
        viewModel.setBinding(mBinding);
    }
}
