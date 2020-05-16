package com.zb.module_home.iv;

import android.view.View;

import com.zb.lib_base.model.Review;

public interface DiscoverDetailVMInterface {
    void selectGift(View view);

    void toRewardList(View view);

    void dynDetail();

    void giftList();

    void seeGiftRewards();

    void seeReviews();

    void makeEvaluate();

    void otherInfo();

    void deleteDyn();

    void walletAndPop();

    void dynDoReview();

    void selectReview(Review review);
}
