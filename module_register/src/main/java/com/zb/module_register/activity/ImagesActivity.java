package com.zb.module_register.activity;

import android.content.Intent;
import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_register.BR;
import com.zb.module_register.R;
import com.zb.module_register.databinding.RegisterImagesBinding;
import com.zb.module_register.vm.ImagesViewModel;

@Route(path = RouteUtils.Register_Images)
public class ImagesActivity extends RegisterBaseActivity {
    private ImagesViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.register_images;
    }

    @Override
    public void initUI() {
        viewModel = new ImagesViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        viewModel.setAdapter();

        RegisterImagesBinding binding = (RegisterImagesBinding) mBinding;
        // 步骤进度跳
        AdapterBinding.viewSize(binding.includeLayout.whiteBg, MineApp.W, 5);
        AdapterBinding.viewSize(binding.includeLayout.whiteView, MineApp.W, 5);
        // 列表宽
        AdapterBinding.viewSize(binding.imagesList, ObjectUtils.getViewSizeByWidth(0.9f), ObjectUtils.getLogoHeight(0.6f));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == 1) {
            String fileName = data.getStringExtra("fileName");
            if (viewModel.images.get(viewModel._position).isEmpty()) {
                for (int i = 0; i < viewModel.images.size(); i++) {
                    if (viewModel.images.get(i).isEmpty()) {
                        viewModel._position = i;
                        viewModel.images.set(viewModel._position, fileName);
                        viewModel.adapter.notifyItemChanged(viewModel._position);
                        return;
                    }
                }
            } else {
                viewModel.images.set(viewModel._position, fileName);
                viewModel.adapter.notifyItemChanged(viewModel._position);
            }
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
