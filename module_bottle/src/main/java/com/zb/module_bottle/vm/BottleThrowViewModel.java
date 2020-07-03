package com.zb.module_bottle.vm;

import android.view.View;

import com.zb.lib_base.api.findBottleApi;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.BottleInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_bottle.BR;
import com.zb.module_bottle.iv.BottleThrowVMInterface;
import com.zb.module_bottle.windows.BottleContentPW;

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
        mBinding.setVariable(BR.title, "扔一个瓶子");
        mBinding.setVariable(BR.showBtn, false);
        new BottleContentPW(activity, mBinding.getRoot(), new BottleInfo(), true, () -> {
            mBinding.setVariable(BR.title, "我的漂流瓶");
            mBinding.setVariable(BR.showBtn, true);
        });
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
                new BottleContentPW(activity, mBinding.getRoot(), o, false, null);
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
