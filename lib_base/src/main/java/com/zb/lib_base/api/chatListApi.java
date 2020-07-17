package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.ChatList;

import java.util.List;

import rx.Observable;

public class chatListApi extends BaseEntity<List<ChatList>> {
    int pageNo;

    public chatListApi setPageNo(int pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public chatListApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.chatList(pageNo, 10, 1, 1);
    }
}
