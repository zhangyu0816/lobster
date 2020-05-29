package com.zb.module_mine.vm;

import android.view.View;

import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.iv.GiftRecordVMInterface;

public class GiftRecordViewModel extends BaseViewModel implements GiftRecordVMInterface {


    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void right(View view) {
        super.right(view);
    }

    @Override
    public void withdraw(View view) {

    }

    @Override
    public void toProfitRecord(View view) {
        ActivityUtils.getMineTranRecord(31);
    }

    @Override
    public void toBindingZFB(View view) {
        ActivityUtils.getMineBindingBank();
    }

    @Override
    public void incomeDeposit(View view) {
//        new TextPW(activity, mBinding.getRoot(), "收益押金说明", "说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明");
    }
}
