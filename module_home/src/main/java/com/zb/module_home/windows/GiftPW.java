package com.zb.module_home.windows;

import android.view.View;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.api.payOrderForTranApi;
import com.zb.lib_base.api.submitOrderApi;
import com.zb.lib_base.api.walletPayTranApi;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.GiftInfo;
import com.zb.lib_base.model.OrderNumber;
import com.zb.lib_base.model.OrderTran;
import com.zb.lib_base.model.RechargeInfo;
import com.zb.lib_base.model.WalletInfo;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.windows.BasePopupWindow;
import com.zb.lib_base.windows.RechargePW;
import com.zb.module_home.BR;
import com.zb.module_home.R;
import com.zb.module_home.adapter.HomeAdapter;

import java.util.ArrayList;
import java.util.List;

public class GiftPW extends BasePopupWindow {

    private WalletInfo walletInfo;
    private HomeAdapter adapter;
    private List<GiftInfo> giftInfoList;
    private int preIndex = -1;
    private long friendDynId;

    public GiftPW(RxAppCompatActivity activity, View parentView, WalletInfo walletInfo, List<GiftInfo> giftInfoList, long friendDynId) {
        super(activity, parentView, true);
        this.walletInfo = walletInfo;
        this.giftInfoList = giftInfoList;
        this.friendDynId = friendDynId;
        initUI();
    }

    @Override
    public int getRes() {
        return R.layout.pws_home_gift;
    }

    @Override
    public void initUI() {
        adapter = new HomeAdapter<>(activity, R.layout.item_home_pws_gift, giftInfoList, this);
        mBinding.setVariable(BR.pw, this);
        mBinding.setVariable(BR.walletInfo, walletInfo);
        mBinding.setVariable(BR.adapter, adapter);
    }

    @Override
    public void cancel(View view) {
        super.cancel(view);
        dismiss();
    }

    @Override
    public void recharge(View view) {
        super.recharge(view);
        dismiss();
        List<RechargeInfo> rechargeInfoList = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            RechargeInfo rechargeInfo = new RechargeInfo();
            if (i < 2) {
                rechargeInfo.setPriceDesc("最受欢迎");
            }
            rechargeInfoList.add(rechargeInfo);
        }
        new RechargePW(activity, mBinding.getRoot(), walletInfo, rechargeInfoList);
    }

    @Override
    public void selectIndex(int position) {
        super.selectIndex(position);
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
    public void payGift(View view) {
        super.payGift(view);
        if (walletInfo.getWallet() < giftInfoList.get(preIndex).getPayMoney()) {
            SCToastUtil.showToast(activity, "钱包余额不足，请先充值");
            return;
        }
        submitOrder();
    }

    private void submitOrder() {
        submitOrderApi api = new submitOrderApi(new HttpOnNextListener<OrderNumber>() {
            @Override
            public void onNext(OrderNumber o) {
                payOrderForTran(o.getOrderNumber());
            }
        }, activity).setFriendDynId(friendDynId).setGiftId(giftInfoList.get(preIndex).getGiftId());
        HttpManager.getInstance().doHttpDeal(api);
    }

    /**
     * 获取交易订单号
     *
     * @param orderNumber
     */
    private void payOrderForTran(String orderNumber) {
        payOrderForTranApi api = new payOrderForTranApi(new HttpOnNextListener<OrderTran>() {
            @Override
            public void onNext(OrderTran o) {
                walletPayTran(o.getTranOrderId());
            }
        }, activity).setOrderNumber(orderNumber);
        HttpManager.getInstance().doHttpDeal(api);
    }

    /**
     * 钱包支付
     *
     * @param tranOrderId
     */
    private void walletPayTran(String tranOrderId) {
        walletPayTranApi api = new walletPayTranApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                dismiss();
                new GiveSuccessPW(activity, mBinding.getRoot(), giftInfoList.get(preIndex));
            }
        }, activity).setTranOrderId(tranOrderId);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
