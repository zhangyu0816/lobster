package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.Reward;

import java.util.List;

import rx.Observable;

public class seeGiftRewardsApi extends BaseEntity<List<Reward>> {
    long friendDynId;
    int rewardSortType; //排序 1.默认排序   2.打赏金额 由大到小
    int pageNo;
    int row;

    public seeGiftRewardsApi setFriendDynId(long friendDynId) {
        this.friendDynId = friendDynId;
        return this;
    }

    public seeGiftRewardsApi setRewardSortType(int rewardSortType) {
        this.rewardSortType = rewardSortType;
        return this;
    }

    public seeGiftRewardsApi setPageNo(int pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public seeGiftRewardsApi setRow(int row) {
        this.row = row;
        return this;
    }

    public seeGiftRewardsApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.seeGiftRewards(friendDynId, rewardSortType, pageNo,row);
    }
}
