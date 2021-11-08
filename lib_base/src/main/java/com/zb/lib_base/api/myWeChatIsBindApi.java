package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.PersonInfo;

import rx.Observable;

public class myWeChatIsBindApi extends BaseEntity<PersonInfo> {

    public myWeChatIsBindApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("查询是否绑定微信钱包");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.myWeChatIsBind();
    }
}
