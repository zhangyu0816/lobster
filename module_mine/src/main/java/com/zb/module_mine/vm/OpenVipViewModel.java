package com.zb.module_mine.vm;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.myInfoApi;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.VipAdPW;
import com.zb.module_mine.databinding.MineOpenVipBinding;
import com.zb.module_mine.iv.OpenVipVMInterface;

import androidx.databinding.ViewDataBinding;

public class OpenVipViewModel extends BaseViewModel implements OpenVipVMInterface {
    private MineOpenVipBinding vipBinding;
    public MineInfo mineInfo;
    public BaseReceiver openVipReceiver;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        vipBinding = (MineOpenVipBinding) binding;
        mineInfo = mineInfoDb.getMineInfo();
        // 开通会员
        openVipReceiver = new BaseReceiver(activity, "lobster_openVip") {
            @Override
            public void onReceive(Context context, Intent intent) {
                myInfo();
            }
        };
    }

    @Override
    public void getVip(int index) {
        new VipAdPW( activity, mBinding.getRoot(), true, 0);
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void myInfo() {
        myInfoApi api = new myInfoApi(new HttpOnNextListener<MineInfo>() {
            @Override
            public void onNext(MineInfo o) {
                mineInfo = o;
                mineInfoDb.saveMineInfo(o);
                vipBinding.setViewModel(OpenVipViewModel.this);
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
