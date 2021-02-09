package com.zb.module_home.iv;

import android.view.View;

import com.zb.lib_base.model.Review;

public interface DiscoverDetailVMInterface {
    void selectGift(View view);

    void toRewardList(View view);

    void toMemberDetail(View view);

    void dynDetail();

    void seeGiftRewards();

    void seeReviews();

    void makeEvaluate();

    void otherInfo();

    void deleteDyn();

    void dynDoReview();

    void dynLike(View view);

    void dynDoLike();

    void dynCancelLike();

    void selectReview(Review review);

    void closeAt(View view);

    void toReviewList(View view);

    void toReviewMemberDetail(Review review);

    void editReview(View view);

    void attentionStatus();

    void attentionOther();

    void cancelAttention();

}
