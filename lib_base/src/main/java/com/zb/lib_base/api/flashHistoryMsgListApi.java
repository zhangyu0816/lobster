package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.PrivateMsg;

import java.util.List;

import rx.Observable;

public class flashHistoryMsgListApi extends BaseEntity<List<PrivateMsg>> {
    long otherUserId;
    long flashTalkId;    //闪聊id
    int pageNo;    //页码

    public flashHistoryMsgListApi setOtherUserId(long otherUserId) {
        this.otherUserId = otherUserId;
        return this;
    }

    public flashHistoryMsgListApi setFlashTalkId(long flashTalkId) {
        this.flashTalkId = flashTalkId;
        return this;
    }

    public flashHistoryMsgListApi setPageNo(int pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public flashHistoryMsgListApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.flashHistoryMsgList(otherUserId, flashTalkId, pageNo);
    }
}
