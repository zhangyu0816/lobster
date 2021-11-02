package com.zb.module_mine.vm;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.payOrderForTranLoveApi;
import com.zb.lib_base.api.submitBlackBoxOrderForTranApi;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.LoveNumber;
import com.zb.lib_base.model.OrderTran;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.LovePaymentPW;
import com.zb.module_mine.databinding.AcLoveGetBinding;

import androidx.databinding.ViewDataBinding;

public class LoveGetViewModel extends BaseViewModel {
    private AcLoveGetBinding mBinding;
    private BaseReceiver loveSaveReceiver;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (AcLoveGetBinding) binding;
        mBinding.setSexIndex(1);
        loveSaveReceiver = new BaseReceiver(activity, "lobster_loveSave") {
            @Override
            public void onReceive(Context context, Intent intent) {
                activity.finish();
            }
        };
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
                payOrderForTranLove(o.getNumber());
            }
        }, activity)
                .setOrderCategory(8)
                .setSex(mBinding.getSexIndex());
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void payOrderForTranLove(String number) {
        payOrderForTranLoveApi api = new payOrderForTranLoveApi(new HttpOnNextListener<OrderTran>() {
            @Override
            public void onNext(OrderTran o) {
                new LovePaymentPW(mBinding.getRoot()).setOrderTran(o).setType(0).initUI();
            }
        }, activity).setNumber(number);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
