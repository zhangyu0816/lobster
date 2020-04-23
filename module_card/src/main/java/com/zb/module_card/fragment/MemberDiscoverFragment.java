package com.zb.module_card.fragment;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseFragment;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.module_card.BR;
import com.zb.module_card.R;
import com.zb.module_card.vm.MemberDiscoverViewModel;

@Route(path = RouteUtils.Card_Member_Discover_Fragment)
public class MemberDiscoverFragment extends BaseFragment {

    @Autowired(name = "userId")
    long userId;

    @Override
    public int getRes() {
        return R.layout.card_member_discover;
    }

    @Override
    public void initUI() {
        MemberDiscoverViewModel viewModel = new MemberDiscoverViewModel();

        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        viewModel.setAdapter();
    }
}
