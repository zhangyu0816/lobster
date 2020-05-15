package com.zb.module_home.vm;

import android.view.View;

import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_home.iv.HomeVMInterface;

public class HomeViewModel extends BaseViewModel implements HomeVMInterface {

    @Override
    public void publishDiscover(View view) {
        ActivityUtils.getHomePublishImage();
    }
}
