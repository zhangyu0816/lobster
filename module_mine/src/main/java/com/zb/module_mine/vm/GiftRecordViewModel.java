package com.zb.module_mine.vm;

import android.view.View;

import com.zb.lib_base.api.realNameVerifyApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.Authentication;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.TextPW;
import com.zb.module_mine.iv.GiftRecordVMInterface;

public class GiftRecordViewModel extends BaseViewModel implements GiftRecordVMInterface {

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void right(View view) {
        super.right(view);
    }

    @Override
    public void withdraw(View view) {
        if (MineApp.walletInfo.getCanWithdrawCreditWallet() > 0) {
            realNameVerify();
        } else {
            SCToastUtil.showToastBlack(activity, "暂无可提现收益");
        }
    }

    @Override
    public void toProfitRecord(View view) {
        ActivityUtils.getMineTranRecord(31);
    }

    @Override
    public void toBindingZFB(View view) {
        ActivityUtils.getMineBindingBank();
    }

    @Override
    public void incomeDeposit(View view) {
//        new TextPW(activity, mBinding.getRoot(), "收益押金说明", "说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明");
    }

    @Override
    public void realNameVerify() {
        realNameVerifyApi api = new realNameVerifyApi(new HttpOnNextListener<Authentication>() {
            @Override
            public void onNext(Authentication o) {
                if(o.getIsChecked()==1){
                    ActivityUtils.getMineWithdraw();
                }else{
                    if(o.getIsChecked()==0||o.getIsChecked()==100){
                        new TextPW(activity,mBinding.getRoot(),"实名认证","实名认证还在审核中，请稍后再试！");
                    }else{
                        new TextPW(activity, mBinding.getRoot(), "实名认证", "你的实名认证审核失败，请前往查看失败原因并重新提交！", () -> ActivityUtils.getMineAuthentication(o));
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    new TextPW(activity, mBinding.getRoot(), "实名认证", "你还未实名认证无法提现,请前往提交实名认证信息！", () -> ActivityUtils.getMineAuthentication(new Authentication()));
                }
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
