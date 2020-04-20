package com.zb.module_bottle.vm;

import android.view.View;

import com.zb.lib_base.model.BottleInfo;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_bottle.iv.BottleThrowVMInterface;
import com.zb.module_bottle.windows.BottleContentPW;

import androidx.databinding.ViewDataBinding;

public class BottleThrowViewModel extends BaseViewModel implements BottleThrowVMInterface {
    private BottleInfo bottleInfo;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        bottleInfo = new BottleInfo();
    }

    @Override
    public void collectBottle(View view) {
        bottleInfo.setText("上两节课而非离开微积分来我家法律文件法律文件法律文件法律文件法律文件法律文件发劳务费我 违法为附件为了看了");
        new BottleContentPW(activity, mBinding.getRoot(), bottleInfo,false);
    }

    @Override
    public void throwBottle(View view) {
        bottleInfo.setText("");
        new BottleContentPW(activity, mBinding.getRoot(), bottleInfo,true);
    }

    @Override
    public void myBottle(View view) {

    }
}
