package com.zb.module_home.iv;

import android.view.View;

public interface DiscoverVideoL2VMInterface {
    void videoPlay(View view);

    void toReviews(View view);

    void toGood(View view);

    void toShare(View view);

    void toMemberDetail(View view);

    void dynDetail();

    void otherInfo();

    void attentionStatus();

    void attentionOther();

    void cancelAttention();

    void dynDoLike();

    void dynCancelLike();

    void deleteDyn();

    void makeEvaluate();
}