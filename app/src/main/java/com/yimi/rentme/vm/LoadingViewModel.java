package com.yimi.rentme.vm;

import com.yimi.rentme.iv.LoadingVMInterface;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.api.myInfoApi;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;

public class LoadingViewModel extends BaseViewModel implements LoadingVMInterface {

    @Override
    public void myInfo() {
        ActivityUtils.getMainActivity();
        activity.finish();
//        if (BaseActivity.sessionId.isEmpty()) {
//            ActivityUtils.getRegisterMain();
//            activity.finish();
//        } else {
//            myInfoApi api = new myInfoApi(new HttpOnNextListener<MineInfo>() {
//                @Override
//                public void onNext(MineInfo o) {
//                    mineInfoDb.saveMineInfo(o);
//                    ActivityUtils.getMainActivity();
//                    activity.finish();
//                }
//
//                @Override
//                public void onError(Throwable e) {
//                    if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NOT_LOGIN) {
//                        ActivityUtils.getRegisterMain();
//                        activity.finish();
//                    }
//                }
//            }, activity);
//            HttpManager.getInstance().doHttpDeal(api);
//        }
    }
}
