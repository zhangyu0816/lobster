package com.zb.module_camera.fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseFragment;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_camera.R;

@Route(path = RouteUtils.Camera_Video_Fragment)
public class PublishVideoFragment extends BaseFragment {
    @Override
    public int getRes() {
        return R.layout.camera_video;
    }

    @Override
    public void initUI() {

    }
}
