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
import com.zb.lib_base.model.WalletInfo;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.windows.BasePopupWindow;
import com.zb.module_home.BR;
import com.zb.module_home.R;
import com.zb.module_home.databinding.PwsGiftPayBinding;

public class GiftPayPW extends BasePopupWindow {
    private GiftInfo giftInfo;
    private WalletInfo walletInfo;
    private long friendDynId;
    private PwsGiftPayBinding binding;

    public GiftPayPW(RxAppCompatActivity activity, View parentView, GiftInfo giftInfo, WalletInfo walletInfo, long friendDynId) {
        super(activity, parentView, true);
        this.giftInfo = giftInfo;
        this.walletInfo = walletInfo;
        this.friendDynId = friendDynId;
        initUI();
    }

    @Override
    public int getRes() {
        return R.layout.pws_gift_pay;
    }

    @Override
    public void initUI() {
        binding = (PwsGiftPayBinding) mBinding;
        mBinding.setVariable(BR.pw, this);
        mBinding.setVariable(BR.content, "");
    }

    @Override
    public void sure(View view) {
        super.sure(view);
        if (binding.getContent().isEmpty() || Integer.parseInt(binding.getContent()) == 0) {
            SCToastUtil.showToastBlack(activity, "请输入赠送数量");
            return;
        }

        if (Integer.parseInt(binding.getContent()) * giftInfo.getPayMoney() < walletInfo.getWallet()) {
            SCToastUtil.showToastBlack(activity, "钱包余额不足，请先充值");
            return;
        }
        submitOrder();
    }

    private void submitOrder() {
        submitOrderApi api = new submitOrderApi(new HttpOnNextListener<OrderNumber>() {
            @Override
            public void onNext(OrderNumber o) {
                payOrderForTran(o.getNumber());
            }
        }, activity).setFriendDynId(friendDynId).setGiftId(giftInfo.getGiftId()).setGiftNum(Integer.parseInt(binding.getContent()));
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
                new GiveSuccessPW(activity, mBinding.getRoot(), giftInfo);
            }
        }, activity).setTranOrderId(tranOrderId);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
