package com.zb.module_camera.vm;

import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_camera.iv.PhotoVMInterface;

public class PhotoViewModel extends BaseViewModel implements PhotoVMInterface {
    @Override
    public void selectIndex(int index) {
        if (index == 0) {
            ActivityUtils.getCameraMain(activity, true);
        } else if (index == 1) {
            ActivityUtils.getCameraVideo();
        }
        activity.finish();
    }
}
