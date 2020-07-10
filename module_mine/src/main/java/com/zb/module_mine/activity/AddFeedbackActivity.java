package com.zb.module_mine.activity;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.model.FeedbackInfo;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.vm.AddFeedbackViewModel;

import java.util.Arrays;

@Route(path = RouteUtils.Mine_Add_Feedback)
public class AddFeedbackActivity extends MineBaseActivity {
    @Autowired(name = "feedbackInfo")
    FeedbackInfo feedbackInfo;

    private BaseReceiver cameraReceiver;
    private AddFeedbackViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.mine_add_feedback;
    }

    @Override
    public void initUI() {
        viewModel = new AddFeedbackViewModel();
        viewModel.feedbackInfo = feedbackInfo;
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);

        cameraReceiver = new BaseReceiver(activity, "lobster_camera") {
            @Override
            public void onReceive(Context context, Intent intent) {
                int cameraType = intent.getIntExtra("cameraType", 0);
                boolean isMore = intent.getBooleanExtra("isMore", false);
                String path = intent.getStringExtra("filePath");
                // 相册
                if (cameraType == 0) {
                    if (isMore) {
                        viewModel.images.clear();
                        viewModel.images.addAll(Arrays.asList(path.split(",")));
                        viewModel.images.add("add_image_icon");
                        viewModel.adapter.notifyDataSetChanged();
                    }
                } else if (cameraType == 2) {
                    viewModel.images.add(viewModel.images.size() - 1, path);
                    viewModel.adapter.notifyDataSetChanged();
                }
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraReceiver.unregisterReceiver();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            viewModel.back(null);
            return true;
        }
        return false;
    }
}
