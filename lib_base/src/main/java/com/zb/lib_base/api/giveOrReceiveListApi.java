package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.GiftRecord;

import java.util.List;

import rx.Observable;

public class giveOrReceiveListApi extends BaseEntity<List<GiftRecord>> {
    int pageNo;
    int friendDynGiftType;// 1 赠礼 2 收礼

    public giveOrReceiveListApi setPageNo(int pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public giveOrReceiveListApi setFriendDynGiftType(int friendDynGiftType) {
        this.friendDynGiftType = friendDynGiftType;
        return this;
    }

    public giveOrReceiveListApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.giveOrReceiveList(pageNo, friendDynGiftType);
    }
}
