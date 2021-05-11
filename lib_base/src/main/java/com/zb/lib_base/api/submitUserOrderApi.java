package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.OrderNumber;

import rx.Observable;

public class submitUserOrderApi extends BaseEntity<OrderNumber> {
    long otherUserId;
    long giftId;  //礼物id
    int giftNum;

    public submitUserOrderApi setOtherUserId(long otherUserId) {
        this.otherUserId = otherUserId;
        return this;
    }

    public submitUserOrderApi setGiftId(long giftId) {
        this.giftId = giftId;
        return this;
    }

    public submitUserOrderApi setGiftNum(int giftNum) {
        this.giftNum = giftNum;
        return this;
    }

    public submitUserOrderApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("支付礼物");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.submitUserOrder(otherUserId, giftId, giftNum);
    }
}
