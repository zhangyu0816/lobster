package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BaseResultEntity;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;

public class cameraReviewApi extends BaseEntity<BaseResultEntity> {
    long cameraFilmResourceId;  //胶卷资源id
    long reviewId; //评论id [可空]
    String text;//内容

    public cameraReviewApi setCameraFilmResourceId(long cameraFilmResourceId) {
        this.cameraFilmResourceId = cameraFilmResourceId;
        return this;
    }

    public cameraReviewApi setReviewId(long reviewId) {
        this.reviewId = reviewId;
        return this;
    }

    public cameraReviewApi setText(String text) {
        this.text = text;
        return this;
    }

    public cameraReviewApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("发表评论...");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        Map<String, Object> map = new HashMap<>();
        map.put("cameraFilmResourceId", cameraFilmResourceId);
        if (reviewId != 0)
            map.put("reviewId", reviewId);
        map.put("text", text);

        return methods.cameraReview(map);
    }
}
