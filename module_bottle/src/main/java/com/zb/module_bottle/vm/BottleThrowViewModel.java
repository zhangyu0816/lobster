package com.zb.module_bottle.vm;

import android.view.View;

import com.zb.lib_base.api.findBottleApi;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.BottleInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_bottle.iv.BottleThrowVMInterface;
import com.zb.module_bottle.windows.BottleContentPW;

import androidx.databinding.ViewDataBinding;

public class BottleThrowViewModel extends BaseViewModel implements BottleThrowVMInterface {

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void collectBottle(View view) {
        findBottle();
    }

    @Override
    public void throwBottle(View view) {
        new BottleContentPW(activity, mBinding.getRoot(), new BottleInfo(), true);
    }

    @Override
    public void myBottle(View view) {
        ActivityUtils.getBottleList();
    }

    @Override
    public void findBottle() {
        findBottleApi api = new findBottleApi(new HttpOnNextListener<BottleInfo>() {
            @Override
            public void onNext(BottleInfo o) {
                new BottleContentPW(activity, mBinding.getRoot(), o, false);
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
