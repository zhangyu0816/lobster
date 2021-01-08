package com.zb.lib_base.windows;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zb.lib_base.BR;
import com.zb.lib_base.R;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.alipayFastPayTranApi;
import com.zb.lib_base.api.walletPayTranApi;
import com.zb.lib_base.api.wxpayAppPayTranApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.AliPay;
import com.zb.lib_base.model.OrderTran;
import com.zb.lib_base.model.PayResult;
import com.zb.lib_base.model.WXPay;
import com.zb.lib_base.utils.SCToastUtil;

public class PaymentPW extends BasePopupWindow {
    private OrderTran orderTran;
    private int payType; // 1 开通VIP  2 充值
    private BaseReceiver paySuccessReceiver;

    public PaymentPW(View parentView, OrderTran orderTran, int payType) {
        super(parentView, true);
        this.orderTran = orderTran;
        this.payType = payType;
        initUI();
    }

    @Override
    public int getRes() {
        return R.layout.pws_payment;
    }

    @Override
    public void initUI() {
        mBinding.setVariable(BR.pw, this);
        paySuccessReceiver = new BaseReceiver(activity, "lobster_paySuccess") {
            @Override
            public void onReceive(Context context, Intent intent) {
                paySuccess();
            }
        };
    }

    @Override
    public void selectIndex(int position) {
        super.selectIndex(position);
        if (position == 0) {
            // 钱包支付
            walletPayTran(orderTran.getTranOrderId());
        } else if (position == 1) {
            alipayFastPayTran(orderTran.getTranOrderId());
        } else if (position == 2) {
            wxpayAppPayTran(orderTran.getTranOrderId());
        }
    }

    private void paySuccess() {
        if (payType == 1) {
            activity.sendBroadcast(new Intent("lobster_openVip"));
        } else if (payType == 2) {
            activity.sendBroadcast(new Intent("lobster_recharge"));
        } else if (payType == 3) {
            activity.sendBroadcast(new Intent("lobster_openPartner"));
        }
        try {
            paySuccessReceiver.unregisterReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
        dismiss();
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
                paySuccess();
            }
        }, activity).setTranOrderId(tranOrderId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    // 支付宝支付
    private void alipayFastPayTran(String tranOrderId) {
        alipayFastPayTranApi api = new alipayFastPayTranApi(new HttpOnNextListener<AliPay>() {
            @Override
            public void onNext(AliPay o) {
                Runnable ra = () -> {
                    // 构造PayTask 对象
                    PayTask alipay = new PayTask(activity);
                    // 调用支付接口，获取支付结果
                    String result = alipay.pay(o.getPayInfo());

                    activity.runOnUiThread(() -> {
                        PayResult payResult = new PayResult(result);
                        String resultStatus = payResult.getResultStatus();
                        if (TextUtils.equals(resultStatus, "9000")) {
                            SCToastUtil.showToast(activity, "支付成功", true);
                            paySuccess();
                        } else {
                            if (TextUtils.equals(resultStatus, "8000")) {
                                SCToastUtil.showToast(activity, "支付结果确认中", true);
                            } else {
                                SCToastUtil.showToast(activity, "支付失败", true);
                            }
                            try {
                                paySuccessReceiver.unregisterReceiver();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            dismiss();
                        }
                    });
                };
                MineApp.getApp().getFixedThreadPool().execute(ra);
            }
        }, activity).setTranOrderId(tranOrderId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    // 微信支付
    private void wxpayAppPayTran(String tranOrderId) {
        wxpayAppPayTranApi api = new wxpayAppPayTranApi(new HttpOnNextListener<WXPay>() {
            @Override
            public void onNext(WXPay pay) {
                PayReq req = new PayReq();
                req.appId = pay.getAppid();
                req.partnerId = pay.getPartnerid();
                req.prepayId = pay.getPrepayid();
                req.nonceStr = pay.getNoncestr();
                req.timeStamp = pay.getTimestamp();
                req.packageValue = "Sign=WXpay";
                req.sign = pay.getSign();
                WXAPIFactory.createWXAPI(activity, pay.getAppid()).sendReq(req);
            }
        }, activity).setTranOrderId(tranOrderId);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
