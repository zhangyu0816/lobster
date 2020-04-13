package com.zb.module_camera.fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseFragment;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_camera.R;
import com.zb.module_camera.databinding.CameraPictureBinding;
import com.zb.module_camera.vm.PictureViewModel;

import androidx.databinding.library.baseAdapters.BR;

@Route(path = RouteUtils.Camera_Picture_Fragment)
public class PictureFragment extends BaseFragment {
    @Override
    public int getRes() {
        return R.layout.camera_picture;
    }

    @Override
    public void initUI() {
        PictureViewModel viewModel = new PictureViewModel();
        viewModel.isMore = true;
        mBinding.setVariable(BR.viewModel, viewModel);
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.isMore, true);
        AdapterBinding.viewSize(((CameraPictureBinding) mBinding).imagesList, MineApp.W, (int) (MineApp.H * 0.4f));
    }
}
