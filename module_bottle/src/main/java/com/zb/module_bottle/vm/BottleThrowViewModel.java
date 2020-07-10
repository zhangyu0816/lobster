package com.zb.module_bottle.vm;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.findBottleApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.BottleInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_bottle.BR;
import com.zb.module_bottle.iv.BottleThrowVMInterface;
import com.zb.module_bottle.windows.BottleContentPW;

import androidx.databinding.ViewDataBinding;

public class BottleThrowViewModel extends BaseViewModel implements BottleThrowVMInterface {
    private BaseReceiver updateContactNumReceiver;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        updateContactNumReceiver = new BaseReceiver(activity, "lobster_updateContactNum") {
            @Override
            public void onReceive(Context context, Intent intent) {
                int chatType = intent.getIntExtra("chatType", 0);
                if (chatType == 2) {
                    mBinding.setVariable(BR.noReadNum, MineApp.noReadBottleNum);
                }
            }
        };
    }

    public void onDestroy() {
        updateContactNumReceiver.unregisterReceiver();
    }

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
                mBinding.setVariable(BR.title, "我的漂流瓶");
                mBinding.setVariable(BR.showBtn, false);
                new BottleContentPW(activity, mBinding.getRoot(), o, false, () -> {
                    mBinding.setVariable(BR.title, "我的漂流瓶");
                    mBinding.setVariable(BR.showBtn, true);
                });
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
