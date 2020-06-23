package com.zb.module_card.windows;

import android.view.View;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.api.superExposureApi;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.utils.DateUtil;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.windows.BasePopupWindow;
import com.zb.module_card.BR;
import com.zb.module_card.R;

public class ExposurePW extends BasePopupWindow {

    private CallBack callBack;

    public ExposurePW(RxAppCompatActivity activity, View parentView, CallBack callBack) {
        super(activity, parentView, true);
        this.callBack = callBack;
        initUI();
    }

    @Override
    public int getRes() {
        return R.layout.pws_exposure;
    }

    @Override
    public void initUI() {
        mBinding.setVariable(BR.pw, ExposurePW.this);
    }

    @Override
    public void sure(View view) {
        super.sure(view);
        superExposureApi api = new superExposureApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                SCToastUtil.showToast(activity, "曝光成功", true);
                PreferenceUtil.saveStringValue(activity, "exposureTime", DateUtil.getNow(DateUtil.yyyy_MM_dd));
                dismiss();
            }

            @Override
            public void onError(Throwable e) {
                callBack.error(e);
                dismiss();
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    public interface CallBack {
        void error(Throwable e);
    }
}
