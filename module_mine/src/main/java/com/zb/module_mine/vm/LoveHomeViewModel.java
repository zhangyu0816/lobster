package com.zb.module_mine.vm;

import android.view.View;

import com.zb.lib_base.api.openedMemberPriceListApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.VipInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.databinding.AcLoveHomeBinding;
import com.zb.module_mine.windows.OpenLovePW;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.ViewDataBinding;

public class LoveHomeViewModel extends BaseViewModel {
    private AcLoveHomeBinding mBinding;
    private List<VipInfo> vipInfoList = new ArrayList<>();

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (AcLoveHomeBinding) binding;
        goAnimator(mBinding.ivLogoWen, 0.8f, 1.0f, 800L);
        goAnimator(mBinding.ivLogoXin, 0.8f, 1.0f, 800L);
    }

    public void toLoveMoney(View view) {
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
        if (vipInfoList.size() == 0) {
            openedMemberPriceList();
        } else {
            new OpenLovePW(mBinding.getRoot()).setVipInfoList(vipInfoList).initUI();
        }
    }

    private void openedMemberPriceList() {
        MineApp.pfAppType = "205";
        openedMemberPriceListApi api = new openedMemberPriceListApi(new HttpOnNextListener<List<VipInfo>>() {
            @Override
            public void onNext(List<VipInfo> o) {
                vipInfoList = o;
                MineApp.pfAppType = "203";
                new OpenLovePW(mBinding.getRoot()).setVipInfoList(vipInfoList).initUI();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                MineApp.pfAppType = "203";
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
