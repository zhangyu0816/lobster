package com.zb.module_home;

import android.content.Intent;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_home.vm.PublishImageViewModel;

import java.util.Arrays;

@Route(path = RouteUtils.Home_Publish_image)
public class PublishImageActivity extends HomeBaseActivity {
    private PublishImageViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.home_public_image;
    }

    @Override
    public void initUI() {
        viewModel = new PublishImageViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.title,"");
        mBinding.setVariable(BR.content,"");
        viewModel.setAdapter();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == 1) {
            String fileNames = data.getStringExtra("filePaths");
            viewModel.images.clear();
            viewModel.images.addAll(Arrays.asList(fileNames.split(",")));
            viewModel.images.add("add_image_icon");
            viewModel.adapter.notifyDataSetChanged();
        }
    }
}
