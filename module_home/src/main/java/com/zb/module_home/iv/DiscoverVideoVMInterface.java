package com.zb.module_home.iv;

import android.view.View;

public interface DiscoverVideoVMInterface {
    void videoPlay(View view);

    void toReviews(View view);

    void toGood(View view);

    void toShare(View view);

    void toMemberDetail(View view);

    void doReward(View view);

    void dynDetail();

    void otherInfo();

    void attentionStatus();

    void attentionOther();

    void cancelAttention();

    void dynDoLike();

    void dynCancelLike();

    void deleteDyn();

    void makeEvaluate();

    void seeLikers(int pageNo);

    void seeReviews(int pageNo);
}
