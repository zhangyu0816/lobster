package com.zb.module_mine.activity;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.model.FeedbackInfo;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.vm.FeedbackDetailViewModel;

@Route(path = RouteUtils.Mine_Feedback_Detail)
public class FeedbackDetailActivity extends MineBaseActivity {
    @Autowired(name = "feedbackInfo")
    FeedbackInfo feedbackInfo;

    @Override
    public int getRes() {
        return R.layout.mine_feedback_detail;
    }

    @Override
    public void initUI() {
        FeedbackDetailViewModel viewModel = new FeedbackDetailViewModel();
        viewModel.feedbackInfo = feedbackInfo;
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
    }
}
