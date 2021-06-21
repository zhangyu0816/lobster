package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.FilmMsg;

import java.util.List;

import rx.Observable;

public class cameraFilmMsgListApi extends BaseEntity<List<FilmMsg>> {
    int pageNo;

    public cameraFilmMsgListApi setPageNo(int pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public cameraFilmMsgListApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.cameraFilmMsgList(pageNo,10);
    }
}
