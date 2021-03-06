package com.zb.lib_base.windows;

import android.content.Intent;
import android.view.View;

import com.zb.lib_base.BR;
import com.zb.lib_base.R;
import com.zb.lib_base.api.submitOrderApi;
import com.zb.lib_base.api.submitUserOrderApi;
import com.zb.lib_base.api.walletPayTranApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.databinding.PwsGiftPayBinding;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.GiftInfo;
import com.zb.lib_base.model.OrderNumber;
import com.zb.lib_base.utils.SCToastUtil;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class GiftPayPW extends BasePopupWindow {
    private GiftInfo giftInfo;
    private long friendDynId;
    private long otherUserId;
    private PwsGiftPayBinding binding;
    private CallBack callBack;

    public GiftPayPW(View parentView, GiftInfo giftInfo, long friendDynId, long otherUserId, CallBack callBack) {
        super(parentView, true);
        this.giftInfo = giftInfo;
        this.friendDynId = friendDynId;
        this.otherUserId = otherUserId;
        this.callBack = callBack;
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
            SCToastUtil.showToast(activity, "请输入赠送数量", true);
            return;
        }
        double money = Integer.parseInt(binding.getContent()) * giftInfo.getPayMoney();
        if (money > MineApp.walletInfo.getWallet()) {
            SCToastUtil.showToast(activity, "钱包余额不足，请先充值", true);
            return;
        }
        if (friendDynId != 0)
            submitOrder();
        else
            submitUserOrder();
    }

    private void submitOrder() {
        submitOrderApi api = new submitOrderApi(new HttpOnNextListener<OrderNumber>() {
            @Override
            public void onNext(OrderNumber o) {
                if (o.getIsPayed() == 0)
                    walletPayTran(o.getNumber());
                else {
                    LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_recharge"));
                    callBack.paySuccess();
                    dismiss();
                    new GiveSuccessPW(mBinding.getRoot(), giftInfo, Integer.parseInt(binding.getContent()));
                }
            }
        }, activity).setFriendDynId(friendDynId).setGiftId(giftInfo.getGiftId()).setGiftNum(Integer.parseInt(binding.getContent()));
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void submitUserOrder() {
        submitUserOrderApi api = new submitUserOrderApi(new HttpOnNextListener<OrderNumber>() {
            @Override
            public void onNext(OrderNumber o) {
                if (o.getIsPayed() == 0)
                    walletPayTran(o.getNumber());
                else {
                    LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_recharge"));
                    callBack.paySuccess();
                    dismiss();
                    new GiveSuccessPW(mBinding.getRoot(), giftInfo, Integer.parseInt(binding.getContent()));
                }
            }
        }, activity).setOtherUserId(otherUserId).setGiftId(giftInfo.getGiftId()).setGiftNum(Integer.parseInt(binding.getContent()));
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
                LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_recharge"));
                callBack.paySuccess();
                dismiss();
                new GiveSuccessPW(mBinding.getRoot(), giftInfo, Integer.parseInt(binding.getContent()));
            }
        }, activity).setTranOrderId(tranOrderId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    public interface CallBack {
        void paySuccess();
    }
}
