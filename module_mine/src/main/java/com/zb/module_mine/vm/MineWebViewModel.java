package com.zb.module_mine.vm;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.markProductListApi;
import com.zb.lib_base.api.myBankCardsApi;
import com.zb.lib_base.api.openMakePartnerApi;
import com.zb.lib_base.api.payOrderForTranShareApi;
import com.zb.lib_base.api.realNameVerifyApi;
import com.zb.lib_base.api.shareChangeCashApi;
import com.zb.lib_base.http.CustomProgressDialog;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.Authentication;
import com.zb.lib_base.model.MineBank;
import com.zb.lib_base.model.OrderNumber;
import com.zb.lib_base.model.OrderTran;
import com.zb.lib_base.model.ShareProduct;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.PaymentPW;
import com.zb.lib_base.windows.TextPW;
import com.zb.module_mine.databinding.MineWebBinding;
import com.zb.module_mine.iv.MineWebVMInterface;

import java.util.List;

import androidx.databinding.ViewDataBinding;

public class MineWebViewModel extends BaseViewModel implements MineWebVMInterface {
    private MineWebBinding mBinding;
    private long makeProductId;
    private BaseReceiver addBankReceiver;
    private long bankAccountId;
    public double money;
    public String shareSignUrl;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (MineWebBinding) binding;
        markProductList();
        addBankReceiver = new BaseReceiver(activity, "lobster_addBank") {
            @Override
            public void onReceive(Context context, Intent intent) {
                myBankCards();
            }
        };
    }

    public void onDestroy() {
        try {
            addBankReceiver.unregisterReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void markProductList() {
        markProductListApi api = new markProductListApi(new HttpOnNextListener<List<ShareProduct>>() {
            @Override
            public void onNext(List<ShareProduct> o) {
                for (ShareProduct item : o) {
                    if (item.getMarkeType() == 1) {
                        makeProductId = item.getId();
                    }
                }
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void openMakePartner() {
        if (makeProductId == 0) {
            return;
        }
        openMakePartnerApi api = new openMakePartnerApi(new HttpOnNextListener<OrderNumber>() {
            @Override
            public void onNext(OrderNumber o) {
                payOrderForTranShare(o.getOrderNumber());
            }
        }, activity).setMakeProductId(makeProductId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void payOrderForTranShare(String orderNumber) {
        payOrderForTranShareApi api = new payOrderForTranShareApi(new HttpOnNextListener<OrderTran>() {
            @Override
            public void onNext(OrderTran o) {
                new PaymentPW(mBinding.getRoot(), o, 3);
            }
        }, activity).setOrderNumber(orderNumber);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void realNameVerify() {
        realNameVerifyApi api = new realNameVerifyApi(new HttpOnNextListener<Authentication>() {
            @Override
            public void onNext(Authentication o) {
                if (o.getIsChecked() == 1) {
                    myBankCards();
                } else {
                    CustomProgressDialog.stopLoading();
                    if (o.getIsChecked() == 0 || o.getIsChecked() == 100) {
                        new TextPW(mBinding.getRoot(), "实名认证", "实名认证还在审核中，请稍后再试！");
                    } else {
                        new TextPW(mBinding.getRoot(), "实名认证", "你的实名认证审核失败，请前往查看失败原因并重新提交！", "重新认证", () -> ActivityUtils.getMineAuthentication(o));
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                CustomProgressDialog.stopLoading();
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    new TextPW(mBinding.getRoot(), "实名认证", "你还未实名认证无法提现,请前往提交实名认证信息！", "去认证", () -> ActivityUtils.getMineAuthentication(new Authentication()));
                }
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void myBankCards() {
        myBankCardsApi api = new myBankCardsApi(new HttpOnNextListener<List<MineBank>>() {
            @Override
            public void onNext(List<MineBank> o) {
                bankAccountId = o.get(0).getId();
                shareChangeCash();
            }

            @Override
            public void onError(Throwable e) {
                CustomProgressDialog.stopLoading();
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    ActivityUtils.getMineBindingBank();
                }
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void shareChangeCash() {
        shareChangeCashApi api = new shareChangeCashApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                CustomProgressDialog.stopLoading();
                SCToastUtil.showToast(activity, "已提交提现信息", true);
                mBinding.webView.reload();
            }

            @Override
            public void onError(Throwable e) {
                CustomProgressDialog.stopLoading();
            }
        }, activity).setMoney(money).setBankAccountId(bankAccountId);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
