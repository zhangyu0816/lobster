package com.zb.module_mine.activity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.databinding.MineFeedbackBinding;
import com.zb.module_mine.vm.FeedbackViewModel;


@Route(path = RouteUtils.Mine_Feedback)
public class FeedbackActivity extends MineBaseActivity {
    private FeedbackViewModel viewModel;
    private MineFeedbackBinding binding;

    @Override
    public int getRes() {
        return R.layout.mine_feedback;
    }

    @Override
    public void initUI() {
        viewModel = new FeedbackViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.title, "反馈列表");
        binding = (MineFeedbackBinding) mBinding;
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.onRefresh(binding.refresh);
    }
}
