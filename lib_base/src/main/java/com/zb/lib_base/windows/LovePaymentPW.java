package com.zb.lib_base.windows;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.R;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.alipayFastPayTranApi;
import com.zb.lib_base.api.walletPayTranApi;
import com.zb.lib_base.api.wxpayAppPayTranApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.databinding.PwsLovePaymentBinding;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.AliPay;
import com.zb.lib_base.model.OrderTran;
import com.zb.lib_base.model.PayResult;
import com.zb.lib_base.model.WXPay;
import com.zb.lib_base.utils.SCToastUtil;

import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class LovePaymentPW extends BaseDialogFragment {
    private PwsLovePaymentBinding mBinding;
    private OrderTran orderTran;
    private BaseReceiver paySuccessReceiver;
    private int type;
    private int payIndex = -1;
    private CallBack callBack;

    public LovePaymentPW(RxAppCompatActivity activity) {
        super(activity, true, false);
    }

    public LovePaymentPW setOrderTran(OrderTran orderTran) {
        this.orderTran = orderTran;
        return this;
    }

    public LovePaymentPW setType(int type) {
        this.type = type;
        return this;
    }

    @Override
    public void onStart() {
        super.onStart();
        center(0.9f);
    }

    public LovePaymentPW setFinish(CallBack callBack) {
        this.callBack = callBack;
        setCallBack(callBack);
        return this;
    }

    public void show(FragmentManager manager) {
        show(manager, "LovePaymentPW");
    }

    @Override
    public int getLayoutId() {
        return R.layout.pws_love_payment;
    }

    @Override
    public void setDataBinding(ViewDataBinding viewDataBinding) {
        mBinding = (PwsLovePaymentBinding) viewDataBinding;
    }

    @Override
    public void initUI() {
        mBinding.screenView.setOnClickListener(view -> {
            if (callBack != null)
                callBack.onFinish();
            dismiss();
        });
        mBinding.setPw(this);
        mBinding.setPayIndex(payIndex);
        mBinding.setType(type);
        paySuccessReceiver = new BaseReceiver(activity, "lobster_paySuccess") {
            @Override
            public void onReceive(Context context, Intent intent) {
                paySuccess();
            }
        };
    }

    public void selectPay(int payIndex) {
        this.payIndex = payIndex;
        mBinding.setPayIndex(payIndex);
    }

    public void pay(View view) {
        if (payIndex == -1) {
            SCToastUtil.showToast(activity, "?????????????????????", true);
            return;
        }
        if (payIndex == 0) {
            alipayFastPayTran(orderTran.getTranOrderId());
        } else if (payIndex == 1) {
            wxpayAppPayTran(orderTran.getTranOrderId());
        } else {
            walletPayTran(orderTran.getTranOrderId());
        }
    }

    private void paySuccess() {
        LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_loveSave"));
        try {
            paySuccessReceiver.unregisterReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
        dismiss();
    }

    // ???????????????
    private void alipayFastPayTran(String tranOrderId) {
        alipayFastPayTranApi api = new alipayFastPayTranApi(new HttpOnNextListener<AliPay>() {
            @Override
            public void onNext(AliPay o) {
                Runnable ra = () -> {
                    // ??????PayTask ??????
                    PayTask alipay = new PayTask(activity);
                    // ???????????????????????????????????????
                    String result = alipay.pay(o.getPayInfo());

                    activity.runOnUiThread(() -> {
                        PayResult payResult = new PayResult(result);
                        String resultStatus = payResult.getResultStatus();
                        if (TextUtils.equals(resultStatus, "9000")) {
                            SCToastUtil.showToast(activity, "????????????", true);
                            paySuccess();
                        } else {
                            if (TextUtils.equals(resultStatus, "8000")) {
                                SCToastUtil.showToast(activity, "?????????????????????", true);
                            } else {
                                SCToastUtil.showToast(activity, "????????????", true);
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

    // ????????????
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

    /**
     * ????????????
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
}
