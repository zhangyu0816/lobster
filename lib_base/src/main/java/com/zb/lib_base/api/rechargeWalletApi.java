package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.OrderTran;

import rx.Observable;

public class rechargeWalletApi extends BaseEntity<OrderTran> {
    double money;

    public rechargeWalletApi setMoney(double money) {
        this.money = money;
        return this;
    }

    public rechargeWalletApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("提交充值订单");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.rechargeWallet(money);
    }
}
