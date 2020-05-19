package com.zb.module_mine.vm;

import android.view.View;

import com.zb.lib_base.api.walletAndPopApi;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.RechargeInfo;
import com.zb.lib_base.model.WalletInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.RechargePW;
import com.zb.module_mine.iv.WalletVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.ViewDataBinding;

public class WalletViewModel extends BaseViewModel implements WalletVMInterface {
    public WalletInfo walletInfo;
    private List<RechargeInfo> rechargeInfoList = new ArrayList<>();

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        for (int i = 0; i < 6; i++) {
            RechargeInfo rechargeInfo = new RechargeInfo();
            if (i < 2) {
                rechargeInfo.setPriceDesc("最受欢迎");
            }
            rechargeInfoList.add(rechargeInfo);
        }
        walletAndPop();
    }

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
    public void recharge(View view) {
        new RechargePW(activity, mBinding.getRoot(), walletInfo, rechargeInfoList);
    }

    @Override
    public void toGiftRecord(View view) {
        ActivityUtils.getMineGiftRecord();
    }

    @Override
    public void toGiveRecord(View view) {

    }

    @Override
    public void toGetRecord(View view) {

    }

    @Override
    public void walletAndPop() {
        walletAndPopApi api = new walletAndPopApi(new HttpOnNextListener<WalletInfo>() {
            @Override
            public void onNext(WalletInfo o) {
                walletInfo = o;
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
