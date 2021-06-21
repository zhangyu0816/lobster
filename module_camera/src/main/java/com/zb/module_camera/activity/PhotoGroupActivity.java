package com.zb.module_camera.activity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_camera.BR;
import com.zb.module_camera.R;
import com.zb.module_camera.vm.PhotoGroupViewModel;

@Route(path = RouteUtils.Camera_Photo_Group)
public class PhotoGroupActivity extends WhiteCameraBaseActivity {

    private  PhotoGroupViewModel viewModel;
    @Override
    public int getRes() {
        return R.layout.ac_photo_group;
    }

    @Override
    public void initUI() {
         viewModel = new PhotoGroupViewModel();
        mBinding.setVariable(BR.viewModel,viewModel);
        viewModel.setBinding(mBinding);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
    }
}
