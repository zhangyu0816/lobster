package com.zb.module_card;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseFragment;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_card.vm.MemberVideoViewModel;

@Route(path = RouteUtils.Card_Member_Video_Fragment)
public class MemberVideoFragment extends BaseFragment {
    @Override
    public int getRes() {
        return R.layout.card_member_video;
    }

    @Override
    public void initUI() {
        MemberVideoViewModel viewModel = new MemberVideoViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        viewModel.setAdapter();
    }
}
