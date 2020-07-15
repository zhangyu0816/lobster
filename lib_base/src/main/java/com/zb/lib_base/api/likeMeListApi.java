package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.LikeMe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;

public class likeMeListApi extends BaseEntity<List<LikeMe>> {
    int pageNo;
    int likeOtherStatus;//1 喜欢  2.超级喜欢 [只有会员可以查看 喜欢我的人 , 不是会员只能查看超级喜欢我的人]

    public likeMeListApi setPageNo(int pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public likeMeListApi setLikeOtherStatus(int likeOtherStatus) {
        this.likeOtherStatus = likeOtherStatus;
        return this;
    }

    public likeMeListApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        Map<String, String> map = new HashMap<>();
        if (pageNo > 0)
            map.put("pageNo", pageNo + "");
        if (likeOtherStatus > 0)
            map.put("likeOtherStatus", likeOtherStatus + "");
        return methods.likeMeList(map);
    }
}
