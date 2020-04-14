package com.zb.module_home;

import android.content.Context;
import android.content.Intent;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_home.vm.PublishImageViewModel;

import java.util.Arrays;

@Route(path = RouteUtils.Home_Publish_image)
public class PublishImageActivity extends HomeBaseActivity {
    private PublishImageViewModel viewModel;
    private BaseReceiver cameraReceiver;

    @Override
    public int getRes() {
        return R.layout.home_public_image;
    }

    @Override
    public void initUI() {
        viewModel = new PublishImageViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.title, "");
        mBinding.setVariable(BR.content, "");
        viewModel.setAdapter();

        cameraReceiver = new BaseReceiver(activity, "lobster_camera") {
            @Override
            public void onReceive(Context context, Intent intent) {
                int type = intent.getIntExtra("cameraType", 0);
                boolean isMore = intent.getBooleanExtra("isMore", false);
                String path = intent.getStringExtra("filePath");
                long time = intent.getLongExtra("time", 0);
                if (type == 0) {
                    // 相册
                    if (isMore) {
                        viewModel.images.clear();
                        viewModel.images.addAll(Arrays.asList(path.split(",")));
                        viewModel.images.add("add_image_icon");
                        viewModel.adapter.notifyDataSetChanged();
                    }
                } else if (type == 1) {
                    // 视频
                    viewModel.images.clear();
                    viewModel.images.add(path);
                    viewModel.images.add("add_image_icon");
                    viewModel.adapter.notifyDataSetChanged();
                    viewModel.videoTime = time;
                } else if (type == 2) {
                    // 拍照
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
}
