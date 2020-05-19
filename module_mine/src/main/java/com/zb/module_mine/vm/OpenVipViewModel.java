package com.zb.module_mine.vm;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.myInfoApi;
import com.zb.lib_base.api.payOrderForTranApi;
import com.zb.lib_base.api.submitOpenedMemberOrderApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.model.OrderNumber;
import com.zb.lib_base.model.OrderTran;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.PaymentPW;
import com.zb.module_mine.R;
import com.zb.module_mine.adapter.MineAdapter;
import com.zb.module_mine.databinding.MineOpenVipBinding;
import com.zb.module_mine.iv.OpenVipVMInterface;

import androidx.databinding.ViewDataBinding;

public class OpenVipViewModel extends BaseViewModel implements OpenVipVMInterface {
    public MineAdapter adapter;
    private int preIndex = -1;
    private MineOpenVipBinding vipBinding;
    public MineInfo mineInfo;
    public BaseReceiver openVipReceiver;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        vipBinding = (MineOpenVipBinding) binding;
        mineInfo = mineInfoDb.getMineInfo();
        setAdapter();

        // 开通会员
        openVipReceiver = new BaseReceiver(activity, "lobster_openVip") {
            @Override
            public void onReceive(Context context, Intent intent) {
                myInfo();
            }
        };
    }

    @Override
    public void setAdapter() {
        adapter = new MineAdapter<>(activity, R.layout.item_mine_vip, MineApp.vipInfoList, this);
    }

    @Override
    public void getVip(int index) {
        if (index == 0 && preIndex == -1) {
            vipBinding.scrollView.scrollTo(0, vipBinding.scrollView.getHeight());
        } else {
            if (preIndex == -1) {
                SCToastUtil.showToastBlack(activity, "请选择VIP套餐");
                return;
            }
            submitOpenedMemberOrder();
        }
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void selectIndex(int position) {
        if (preIndex != position) {
            adapter.setSelectIndex(position);
            if (preIndex != -1) {
                adapter.notifyItemChanged(preIndex);
            }
            adapter.notifyItemChanged(position);
            preIndex = position;
        }
    }

    @Override
    public void submitOpenedMemberOrder() {
        submitOpenedMemberOrderApi api = new submitOpenedMemberOrderApi(new HttpOnNextListener<OrderNumber>() {
            @Override
            public void onNext(OrderNumber o) {
                payOrderForTran(o.getOrderNumber(), 1);
            }
        }, activity)
                .setMemberOfOpenedProductId(MineApp.vipInfoList.get(preIndex).getMemberOfOpenedProductId());
        HttpManager.getInstance().doHttpDeal(api);
    }

    /**
     * 获取交易订单号
     *
     * @param orderNumber
     */
    private void payOrderForTran(String orderNumber, int payType) {
        payOrderForTranApi api = new payOrderForTranApi(new HttpOnNextListener<OrderTran>() {
            @Override
            public void onNext(OrderTran o) {
                new PaymentPW(activity, mBinding.getRoot(), o, payType);
            }
        }, activity).setOrderNumber(orderNumber);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void myInfo() {
        myInfoApi api = new myInfoApi(new HttpOnNextListener<MineInfo>() {
            @Override
            public void onNext(MineInfo o) {
                mineInfo = o;
                mineInfoDb.saveMineInfo(o);
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
