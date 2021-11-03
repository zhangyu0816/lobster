package com.zb.module_mine.vm;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.myInfoApi;
import com.zb.lib_base.api.openedMemberPriceListApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.model.VipInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.databinding.AcLoveHomeBinding;
import com.zb.module_mine.windows.OpenLovePW;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.ViewDataBinding;

public class LoveHomeViewModel extends BaseViewModel {
    private AcLoveHomeBinding mBinding;
    private BaseReceiver openVipReceiver;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (AcLoveHomeBinding) binding;
        goAnimator(mBinding.ivLogoWen, 0.8f, 1.0f, 800L);
        goAnimator(mBinding.ivLogoXin, 0.8f, 1.0f, 800L);
        openVipReceiver = new BaseReceiver(activity, "lobster_openVip") {
            @Override
            public void onReceive(Context context, Intent intent) {
                myInfo();
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            openVipReceiver.unregisterReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toLoveMoney(View view) {
        if (MineApp.mineInfo.getAppType() != 205) {
            SCToastUtil.showToast(activity, "成为地摊主后才有收益哦", true);
            return;
        }
        ActivityUtils.getLoveMoney();
    }

    public void toLoveShare(View view) {

    }

    public void onSave(View view) {
        ActivityUtils.getLoveSave();
    }

    public void onGet(View view) {
        ActivityUtils.getLoveGet();
    }

    public void onOpen(View view) {
        if (MineApp.loveInfoList.size() == 0) {
            openedMemberPriceList();
        } else {
            new OpenLovePW(mBinding.getRoot()).setVipInfoList(MineApp.loveInfoList).initUI();
        }
    }

    private void openedMemberPriceList() {
        MineApp.pfAppType = "205";
        openedMemberPriceListApi api = new openedMemberPriceListApi(new HttpOnNextListener<List<VipInfo>>() {
            @Override
            public void onNext(List<VipInfo> o) {
                MineApp.loveInfoList.clear();
                MineApp.loveInfoList = o;
                MineApp.pfAppType = "203";
                new OpenLovePW(mBinding.getRoot()).setVipInfoList(MineApp.loveInfoList).initUI();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                MineApp.pfAppType = "203";
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void myInfo() {
        myInfoApi api = new myInfoApi(new HttpOnNextListener<MineInfo>() {
            @Override
            public void onNext(MineInfo o) {
                MineApp.mineInfo = o;
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
