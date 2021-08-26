package com.zb.module_mine.vm;

import android.view.View;

import com.zb.lib_base.api.realNameVerifyApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.Authentication;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.PreferenceUtil;
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
        ActivityUtils.getMineWeb("提现帮助", HttpManager.BASE_URL + "mobile/xiagu_get_help.html");
    }

    @Override
    public void withdraw(View view) {
        if (PreferenceUtil.readIntValue(activity, "withdraw") == 0) {
            new TextPW(activity, mBinding.getRoot(), "提现说明", "您在使用提现服务时，为确认您所提供的身份信息、提现账号（银行卡信息或支付宝信息）为您本人信息且属实，需要您提供以下信息供我们收集：真实姓名、身份证号及身份证核验授权。" +
                    "真实姓名、身份证号属于个人敏感信息，如您选择不提供上述信息，您可能无法使用提现服务，但不影响其他服务。" +
                    "\n先在卡包内绑定提现账户，单次提现金额不少于10元。提交提现申请后，我们将会在3个工作日内打款至提现账户。", "同意",
                    false, true, () -> {
                PreferenceUtil.saveIntValue(activity, "withdraw", 1);
                if (MineApp.walletInfo.getCanWithdrawCreditWallet() > 0) {
                    realNameVerify(1);
                } else {
                    SCToastUtil.showToast(activity, "暂无可提现收益", true);
                }
            });
        } else {
            if (MineApp.walletInfo.getCanWithdrawCreditWallet() > 0) {
                realNameVerify(1);
            } else {
                SCToastUtil.showToast(activity, "暂无可提现收益", true);
            }
        }
    }

    @Override
    public void toProfitRecord(View view) {
        ActivityUtils.getMineTranRecord(32);
    }

    @Override
    public void toBindingZFB(View view) {
        if (PreferenceUtil.readIntValue(activity, "bindingZFB") == 0) {
            new TextPW(activity, mBinding.getRoot(), "绑定银行卡说明", "您在使用提现服务时，为确认您所提供的身份信息、提现账号（银行卡信息或支付宝信息）为您本人信息且属实，需要您提供以下信息供我们收集：真实姓名、身份证号及身份证核验授权。" +
                    "真实姓名、身份证号属于个人敏感信息，如您选择不提供上述信息，您可能无法使用提现服务，但不影响其他服务。" +
                    "\n先在卡包内绑定提现账户，单次提现金额不少于10元。提交提现申请后，我们将会在3个工作日内打款至提现账户。",
                    "同意", false, true, () -> {
                PreferenceUtil.saveIntValue(activity, "bindingZFB", 1);
                realNameVerify(2);
            });
        } else {
            realNameVerify(2);
        }
    }

    @Override
    public void incomeDeposit(View view) {
    }

    @Override
    public void realNameVerify(int position) {
        realNameVerifyApi api = new realNameVerifyApi(new HttpOnNextListener<Authentication>() {
            @Override
            public void onNext(Authentication o) {
                if (o.getIsChecked() == 1) {
                    if (position == 1)
                        ActivityUtils.getMineWithdraw();
                    else
                        ActivityUtils.getMineBindingBank();
                } else {
                    if (o.getIsChecked() == 0 || o.getIsChecked() == 100) {
                        new TextPW(mBinding.getRoot(), "实名认证", "实名认证还在审核中，请稍后再试！");
                    } else {
                        new TextPW(mBinding.getRoot(), "实名认证", "你的实名认证审核失败，请前往查看失败原因并重新提交！", "重新认证", () -> ActivityUtils.getMineAuthentication(o));
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    new TextPW(mBinding.getRoot(), "实名认证", position == 1 ? "你还未实名认证无法提现,请前往提交实名认证信息！" : "你还未实名认证无法添加银行卡,请前往提交实名认证信息！", "去认证", () -> ActivityUtils.getMineAuthentication(new Authentication()));
                }
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
