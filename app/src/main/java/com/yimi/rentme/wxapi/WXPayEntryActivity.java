package com.yimi.rentme.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.yimi.rentme.R;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.SCToastUtil;

public class WXPayEntryActivity extends RxAppCompatActivity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);
        api = WXAPIFactory.createWXAPI(this, MineApp.WX_PAY_APPID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            int code = resp.errCode;
            String msg = "";
            switch (code) {
                case 0:
                    msg = "支付成功！";
                    sendBroadcast(new Intent("lobster_paySuccess"));
                    break;
                case -1:
                    msg = "支付失败！";
                    break;
                case -2:
                    msg = "您取消了支付！";
                    break;
                default:
                    msg = "支付失败！";
                    break;
            }
            SCToastUtil.showToast(this, msg);
            finish();
        }
    }

}
