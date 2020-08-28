package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BaseResultEntity;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;

public class dynDoReviewApi extends BaseEntity<BaseResultEntity> {
    long reviewId;  //回复另外一个评论  （页面上，是显示回复某个人。 实际参数是使用动态评论id）
    long friendDynId;  //动态的Id
    String text;     //评论的内容

    public dynDoReviewApi setReviewId(long reviewId) {
        this.reviewId = reviewId;
        return this;
    }

    public dynDoReviewApi setFriendDynId(long friendDynId) {
        this.friendDynId = friendDynId;
        return this;
    }

    public dynDoReviewApi setText(String text) {
        this.text = text;
        return this;
    }

    public dynDoReviewApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        Map<String, String> map = new HashMap<>();
        if (reviewId > 0)
            map.put("reviewId", reviewId + "");
        map.put("friendDynId", friendDynId + "");
        map.put("text", text);
        return methods.dynDoReview(map);
    }
}
