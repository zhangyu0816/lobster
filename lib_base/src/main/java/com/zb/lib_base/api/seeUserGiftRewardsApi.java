package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.Reward;

import java.util.List;

import rx.Observable;

public class seeUserGiftRewardsApi extends BaseEntity<List<Reward>> {
    long otherUserId;
    int rewardSortType; //排序 1.默认排序   2.打赏金额 由大到小
    int pageNo;
    int row;

    public seeUserGiftRewardsApi setOtherUserId(long otherUserId) {
        this.otherUserId = otherUserId;
        return this;
    }

    public seeUserGiftRewardsApi setPageNo(int pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public seeUserGiftRewardsApi setRewardSortType(int rewardSortType) {
        this.rewardSortType = rewardSortType;
        return this;
    }

    public seeUserGiftRewardsApi setRow(int row) {
        this.row = row;
        return this;
    }

    public seeUserGiftRewardsApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.seeUserGiftRewards(otherUserId, rewardSortType, pageNo,row);
    }
}
