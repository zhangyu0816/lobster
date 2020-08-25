package com.zb.module_card.iv;

import android.view.View;

public interface MemberDetailVMInterface {

    void dislike(View view);

    void like(View view);

    void toDiscoverList(View view);

    void otherInfo();

    void otherRentInfo();

    void personOtherDyn();

    void attentionStatus();

    void attentionOther();

    void cancelAttention();

    void makeEvaluate(int likeOtherStatus);

    void memberInfoConf();
}
