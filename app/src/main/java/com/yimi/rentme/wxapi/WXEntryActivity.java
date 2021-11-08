package com.yimi.rentme.wxapi;


import android.widget.Toast;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.umeng.socialize.weixin.view.WXCallbackActivity;
import com.zb.lib_base.app.MineApp;

public class WXEntryActivity extends WXCallbackActivity {
    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_LAUNCH_WX_MINIPROGRAM) {
            WXLaunchMiniProgram.Resp launch = (WXLaunchMiniProgram.Resp) resp;
            Toast.makeText(MineApp.sContext, launch.extMsg, Toast.LENGTH_SHORT).show();
        }
    }
}
