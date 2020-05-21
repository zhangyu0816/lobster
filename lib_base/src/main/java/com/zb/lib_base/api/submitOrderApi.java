package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.OrderNumber;

import rx.Observable;

public class submitOrderApi extends BaseEntity<OrderNumber> {
    long friendDynId;//动态id
    long giftId;  //礼物id
    int giftNum;

    public submitOrderApi setFriendDynId(long friendDynId) {
        this.friendDynId = friendDynId;
        return this;
    }

    public submitOrderApi setGiftId(long giftId) {
        this.giftId = giftId;
        return this;
    }

    public submitOrderApi setGiftNum(int giftNum) {
        this.giftNum = giftNum;
        return this;
    }

    public submitOrderApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("支付礼物");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.submitOrder(friendDynId, giftId, giftNum);
    }
}
