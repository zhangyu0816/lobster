package com.zb.module_register;

import android.content.Intent;
import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.utils.RouteUtils;
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
        ViewGroup.LayoutParams lp = binding.includeLayout.whiteView.getLayoutParams();
        lp.width = MineApp.W;
        binding.includeLayout.whiteView.setLayoutParams(lp);

        AdapterBinding.viewSize(binding.imagesList, MineApp.W, ObjectUtils.getLogoHeight(0.6f));
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
}
