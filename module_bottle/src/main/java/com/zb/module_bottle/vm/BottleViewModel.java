package com.zb.module_bottle.vm;

import android.view.View;

import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_bottle.databinding.BottleMainBinding;
import com.zb.module_bottle.iv.BottleVMInterface;

public class BottleViewModel extends BaseViewModel implements BottleVMInterface {
    @Override
    public void entryBottle(View view) {
        if (((BottleMainBinding) mBinding).getIsOutLine()) {
            SCToastUtil.showToastBlack(activity, "当前网络异常，请检查是否连接");
            return;
        }
        ActivityUtils.getBottleThrow();
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }
}
