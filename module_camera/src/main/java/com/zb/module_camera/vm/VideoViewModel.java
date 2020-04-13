package com.zb.module_camera.vm;

import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_camera.iv.VideoVMInterface;

public class VideoViewModel extends BaseViewModel implements VideoVMInterface {
    @Override
    public void selectIndex(int index) {
        if(index == 0){
            ActivityUtils.getCameraMain(activity,true);
        }else if(index == 2){
            ActivityUtils.getCameraPhoto();
        }
        activity.finish();
    }
}
