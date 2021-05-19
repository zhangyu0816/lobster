package com.zb.module_mine.vm;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.myInfoApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.model.VipInfo;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.VipAdPW;
import com.zb.module_mine.R;
import com.zb.module_mine.databinding.MineOpenVipBinding;
import com.zb.module_mine.iv.OpenVipVMInterface;

import androidx.databinding.ViewDataBinding;

public class OpenVipViewModel extends BaseViewModel implements OpenVipVMInterface {
    private MineOpenVipBinding vipBinding;
    public BaseReceiver openVipReceiver;
    public boolean isFinish = false;
    private boolean isOpen = false;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        vipBinding = (MineOpenVipBinding) binding;
        // 开通会员
        openVipReceiver = new BaseReceiver(activity, "lobster_openVip") {
            @Override
            public void onReceive(Context context, Intent intent) {
                isOpen = true;
                myInfo();
            }
        };
        myInfo();

        goAnimator(vipBinding.tvOpen,0.85f,1f,800L);
    }

    @Override
    public void getVip(int index) {
        new VipAdPW(mBinding.getRoot(), 0, "");
    }

    @Override
    public void back(View view) {
        super.back(view);
        stopGo();
        activity.finish();
    }

    @Override
    public void myInfo() {
        myInfoApi api = new myInfoApi(new HttpOnNextListener<MineInfo>() {
            @Override
            public void onNext(MineInfo o) {
                MineApp.mineInfo = o;
                vipBinding.setMineInfo(MineApp.mineInfo);
                setBtn();
                if (isOpen && isFinish)
                    activity.finish();
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void setBtn() {
        VipInfo vipInfo = vipBinding.getVipInfo();
        if (MineApp.isFirstOpen) {
            vipBinding.tvBtn.setText(activity.getResources().getString(R.string.open_btn_month, (vipInfo.getDayCount() / 30) * 2, vipInfo.getPrice()));
        } else if (MineApp.mineInfo.getMemberType() == 2) {
            vipBinding.tvBtn.setText("立即续费VIP特权");
        } else {
            vipBinding.tvBtn.setText(activity.getResources().getString(R.string.open_btn, vipInfo.getPrice()));
        }
    }
}
