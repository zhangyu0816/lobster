package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.FlashInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;

public class flashUserListApi extends BaseEntity<List<FlashInfo>> {
    int sex;

    public flashUserListApi setSex(int sex) {
        this.sex = sex;
        return this;
    }

    public flashUserListApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        Map<String, String> map = new HashMap<>();
        if (MineApp.sex != -1)
            map.put("sex", MineApp.sex + "");
        return methods.flashUserList(map);
    }
}
