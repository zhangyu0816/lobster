package com.zb.lib_base.windows;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.BR;
import com.zb.lib_base.R;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.alipayFastPayTranApi;
import com.zb.lib_base.api.walletPayTranApi;
import com.zb.lib_base.api.wxpayAppPayTranApi;
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

    public PaymentPW(RxAppCompatActivity activity, View parentView, OrderTran orderTran, int payType) {
        super(activity, parentView, true);
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
        }
        paySuccessReceiver.unregisterReceiver();
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
                Runnable payRunnable = () -> {
                    // 构造PayTask 对象
                    PayTask alipay = new PayTask(activity);
                    // 调用支付接口，获取支付结果
                    String result = alipay.pay(o.getPayInfo());
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                };
                // 必须异步调用
                Thread payThread = new Thread(payRunnable);
                payThread.start();
            }
        }, activity).setTranOrderId(tranOrderId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private Handler mHandler = new Handler(msg -> {
        switch (msg.what) {
            case 1:
                PayResult payResult = new PayResult((String) msg.obj);
                String resultStatus = payResult.getResultStatus();
                if (TextUtils.equals(resultStatus, "9000")) {
                    SCToastUtil.showToast(activity, "支付成功");
                    paySuccess();
                } else {
                    if (TextUtils.equals(resultStatus, "8000")) {
                        SCToastUtil.showToast(activity, "支付结果确认中");
                    } else {
                        SCToastUtil.showToast(activity, "支付失败");
                    }
                    paySuccessReceiver.unregisterReceiver();
                    dismiss();
                }
                break;
        }
        return false;
    });

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
