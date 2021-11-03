package com.zb.module_mine.vm;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.getBlackBoxPersonInfoApi;
import com.zb.lib_base.api.payOrderForTranLoveApi;
import com.zb.lib_base.api.submitBlackBoxOrderForTranApi;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.LoveNumber;
import com.zb.lib_base.model.OrderTran;
import com.zb.lib_base.model.PersonInfo;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.LovePaymentPW;
import com.zb.lib_base.windows.WXPW;
import com.zb.module_mine.databinding.AcLoveGetBinding;

import androidx.databinding.ViewDataBinding;

public class LoveGetViewModel extends BaseViewModel {
    private AcLoveGetBinding mBinding;
    private BaseReceiver loveSaveReceiver;
    private String number = "";

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (AcLoveGetBinding) binding;
        mBinding.setSexIndex(1);
        loveSaveReceiver = new BaseReceiver(activity, "lobster_loveSave") {
            @Override
            public void onReceive(Context context, Intent intent) {
                getBlackBoxPersonInfo();
            }
        };
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            loveSaveReceiver.unregisterReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void selectSex(int sexIndex) {
        mBinding.setSexIndex(sexIndex);
    }

    public void pay(View view) {
        submitBlackBoxOrderForTranApi api = new submitBlackBoxOrderForTranApi(new HttpOnNextListener<LoveNumber>() {
            @Override
            public void onNext(LoveNumber o) {
                number = o.getNumber();
                payOrderForTranLove();
            }
        }, activity)
                .setOrderCategory(8)
                .setSex(mBinding.getSexIndex());
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void payOrderForTranLove() {
        payOrderForTranLoveApi api = new payOrderForTranLoveApi(new HttpOnNextListener<OrderTran>() {
            @Override
            public void onNext(OrderTran o) {
                mBinding.sexRelative.setVisibility(View.GONE);
                new LovePaymentPW(activity)
                        .setOrderTran(o).setType(0).setFinish(() -> mBinding.sexRelative.setVisibility(View.VISIBLE))
                        .show(activity.getSupportFragmentManager());
            }
        }, activity).setNumber(number);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void getBlackBoxPersonInfo() {
        getBlackBoxPersonInfoApi api = new getBlackBoxPersonInfoApi(new HttpOnNextListener<PersonInfo>() {
            @Override
            public void onNext(PersonInfo o) {
                new WXPW(mBinding.getRoot()).setWx(o.getWxNum()).setCallBack(() -> mBinding.sexRelative.setVisibility(View.VISIBLE)).initUI();
            }
        }, activity).setNumber(number);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
