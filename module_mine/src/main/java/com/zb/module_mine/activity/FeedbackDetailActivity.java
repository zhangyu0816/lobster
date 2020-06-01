package com.zb.module_mine.activity;

import android.content.Context;
import android.content.Intent;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.model.FeedbackInfo;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.vm.FeedbackDetailViewModel;

import java.util.Arrays;

@Route(path = RouteUtils.Mine_Feedback_Detail)
public class FeedbackDetailActivity extends MineBaseActivity {
    @Autowired(name = "feedbackInfo")
    FeedbackInfo feedbackInfo;

    private BaseReceiver cameraReceiver;
    private FeedbackDetailViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.mine_feedback_detail;
    }

    @Override
    public void initUI() {
        viewModel = new FeedbackDetailViewModel();
        viewModel.feedbackInfo = feedbackInfo;
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);

        cameraReceiver = new BaseReceiver(activity, "lobster_camera") {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isMore = intent.getBooleanExtra("isMore", false);
                String path = intent.getStringExtra("filePath");
                // 相册
                if (isMore) {
                    viewModel.images.clear();
                    viewModel.images.addAll(Arrays.asList(path.split(",")));
                    viewModel.images.add("add_image_icon");
                    viewModel.adapter.notifyDataSetChanged();
                }
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraReceiver.unregisterReceiver();
        viewModel.back(null);
    }
}
