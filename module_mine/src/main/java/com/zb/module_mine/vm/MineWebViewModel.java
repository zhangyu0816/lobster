package com.zb.module_mine.vm;

import android.view.View;

import com.zb.lib_base.api.markProductListApi;
import com.zb.lib_base.api.myBankCardsApi;
import com.zb.lib_base.api.openMakePartnerApi;
import com.zb.lib_base.api.payOrderForTranShareApi;
import com.zb.lib_base.api.realNameVerifyApi;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.Authentication;
import com.zb.lib_base.model.MineBank;
import com.zb.lib_base.model.OrderNumber;
import com.zb.lib_base.model.OrderTran;
import com.zb.lib_base.model.ShareProduct;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.PaymentPW;
import com.zb.lib_base.windows.TextPW;
import com.zb.module_mine.iv.MineWebVMInterface;

import java.util.List;

import androidx.databinding.ViewDataBinding;

public class MineWebViewModel extends BaseViewModel implements MineWebVMInterface {
    private long markeProductId;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        markProductList();
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
                        markeProductId = item.getId();
                    }
                }
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void openMakePartner() {
        if (markeProductId == 0) {
            return;
        }
        openMakePartnerApi api = new openMakePartnerApi(new HttpOnNextListener<OrderNumber>() {
            @Override
            public void onNext(OrderNumber o) {
                payOrderForTranShare(o.getOrderNumber());
            }
        }, activity).setMarkeProductId(markeProductId);
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

            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
