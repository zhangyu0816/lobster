package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BaseResultEntity;

import rx.Observable;

public class bindBankCardApi extends BaseEntity<BaseResultEntity> {
    long bankId;  //银行Id
    String accountNo; //账号
    String openAccountLocation;//网点地址

    public bindBankCardApi setBankId(long bankId) {
        this.bankId = bankId;
        return this;
    }

    public bindBankCardApi setAccountNo(String accountNo) {
        this.accountNo = accountNo;
        return this;
    }

    public bindBankCardApi setOpenAccountLocation(String openAccountLocation) {
        this.openAccountLocation = openAccountLocation;
        return this;
    }

    public bindBankCardApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("上传资料");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.bindBankCard(bankId, accountNo, openAccountLocation);
    }
}
