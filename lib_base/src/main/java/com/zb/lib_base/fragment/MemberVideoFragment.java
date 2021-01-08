package com.zb.lib_base.fragment;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.BR;
import com.zb.lib_base.R;
import com.zb.lib_base.activity.BaseFragment;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.lib_base.vm.MemberVideoViewModel;

@Route(path = RouteUtils.Member_Video_Fragment)
public class MemberVideoFragment extends BaseFragment {
    @Autowired(name = "userId")
    long userId;

    private MemberVideoViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.card_member_video;
    }

    @Override
    public void initUI() {
        viewModel = new MemberVideoViewModel();
        viewModel.otherUserId = userId;
        mBinding.setVariable(BR.viewModel, viewModel);
        viewModel.setBinding(mBinding);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (viewModel != null)
            viewModel.onDestroy();
    }
}
