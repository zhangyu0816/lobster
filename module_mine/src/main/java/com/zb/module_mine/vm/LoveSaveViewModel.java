package com.zb.module_mine.vm;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.payOrderForTranLoveApi;
import com.zb.lib_base.api.saveBlackBoxPersonInfoApi;
import com.zb.lib_base.api.submitBlackBoxOrderForTranApi;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.LoveNumber;
import com.zb.lib_base.model.OrderTran;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.AreaPW;
import com.zb.lib_base.windows.LovePaymentPW;
import com.zb.module_mine.databinding.AcLoveSaveBinding;

import androidx.databinding.ViewDataBinding;

public class LoveSaveViewModel extends BaseViewModel {
    private AcLoveSaveBinding mBinding;
    private long mProvinceId = 0;
    private long mCityId = 0;
    private BaseReceiver loveSaveReceiver;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (AcLoveSaveBinding) binding;
        mBinding.setSexIndex(1);
        mBinding.setAge("");
        mBinding.setWxNum("");
        mBinding.setProvinceName("");

        loveSaveReceiver = new BaseReceiver(activity, "lobster_loveSave") {
            @Override
            public void onReceive(Context context, Intent intent) {
                SCToastUtil.showToast(activity, "登记成功", true);
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

    public void close(View view) {
        hintKeyBoard();
    }

    public void selectSex(int sexIndex) {
        mBinding.setSexIndex(sexIndex);
    }

    public void selectProvince(View view) {
        hintKeyBoard();
        new AreaPW(mBinding.getRoot()).setCallBack((name, provinceId, cityId) -> {
            mBinding.setProvinceName(name);
            mProvinceId = provinceId;
            mCityId = cityId;
        }).initUI();
    }

    public void loveSave(View view) {
        if (mBinding.getAge().isEmpty()) {
            SCToastUtil.showToast(activity, "请输入年龄", true);
            return;
        }
        if (Integer.parseInt(mBinding.getAge()) < 18) {
            SCToastUtil.showToast(activity, "年龄未满18岁，无法存入", true);
            return;
        }
        if (mBinding.getWxNum().isEmpty()) {
            SCToastUtil.showToast(activity, "请输入微信号", true);
            return;
        }
        if (mBinding.getProvinceName().isEmpty()) {
            SCToastUtil.showToast(activity, "请选择省份", true);
            return;
        }
        saveBlackBoxPersonInfo();
    }

    private void saveBlackBoxPersonInfo() {
        saveBlackBoxPersonInfoApi api = new saveBlackBoxPersonInfoApi(new HttpOnNextListener<Long>() {
            @Override
            public void onNext(Long o) {
                submitBlackBoxOrderForTran(o);
            }
        }, activity)
                .setAge(Integer.parseInt(mBinding.getAge()))
                .setWxNum(mBinding.getWxNum())
                .setProvinceId(mProvinceId)
                .setCityId(mCityId)
                .setSex(mBinding.getSexIndex());
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void submitBlackBoxOrderForTran(long blackBoxPersonInfoId) {
        submitBlackBoxOrderForTranApi api = new submitBlackBoxOrderForTranApi(new HttpOnNextListener<LoveNumber>() {
            @Override
            public void onNext(LoveNumber o) {
                payOrderForTranLove(o.getNumber());
            }
        }, activity)
                .setOrderCategory(7)
                .setBlackBoxPersonInfoId(blackBoxPersonInfoId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void payOrderForTranLove(String number) {
        payOrderForTranLoveApi api = new payOrderForTranLoveApi(new HttpOnNextListener<OrderTran>() {
            @Override
            public void onNext(OrderTran o) {
                new LovePaymentPW(activity).setOrderTran(o).setType(1).show(activity.getSupportFragmentManager());
            }
        }, activity).setNumber(number);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
