package com.zb.module_home.activity;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_home.BR;
import com.zb.module_home.R;
import com.zb.module_home.vm.ReportViewModel;

import java.util.Arrays;

@Route(path = RouteUtils.Home_Report)
public class ReportActivity extends HomeBaseActivity {
    @Autowired(name = "otherUserId")
    long otherUserId;

    private BaseReceiver cameraReceiver;
    private ReportViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.home_report;
    }

    @Override
    public void initUI() {
        viewModel = new ReportViewModel();
        viewModel.otherUserId = otherUserId;
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.content, "");

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
                        viewModel.imageAdapter.notifyDataSetChanged();
                    }
                } else if (cameraType == 2) {
                    viewModel.images.add(viewModel.images.size() - 1, path);
                    viewModel.imageAdapter.notifyDataSetChanged();
                }
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            cameraReceiver.unregisterReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
