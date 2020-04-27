package com.zb.module_mine.vm;

import android.view.View;

import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.iv.SettingVMInterface;

public class SettingViewModel extends BaseViewModel implements SettingVMInterface {
    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void toRealName(View view) {
        ActivityUtils.getMineRealName();
    }

    @Override
    public void toWallet(View view) {
        ActivityUtils.getMineWallet();
    }

    @Override
    public void toLocation(View view) {
        ActivityUtils.getMineLocation();
    }

    @Override
    public void toPass(View view) {
        ActivityUtils.getMineModifyPass();
    }

    @Override
    public void toNotice(View view) {
        ActivityUtils.getMineNotice();
    }

    @Override
    public void toCleanCache(View view) {

    }

    @Override
    public void toFeedback(View view) {
        ActivityUtils.getMineFeedback();
    }

    @Override
    public void toRule(View view) {
        ActivityUtils.getMineWeb("隐私政策","");
    }

    @Override
    public void toAboutUs(View view) {
        ActivityUtils.getMineWeb("关于我们","");
    }
}
