package com.zb.module_card.iv;

import android.view.View;

import com.zb.lib_base.model.DiscoverInfo;

public interface MemberDetailVMInterface {

    void dislike(View view);

    void like(View view);

    void toDiscoverList(View view);

    void toDiscoverDetail(DiscoverInfo discoverInfo);

    void selectImage(int position);

    void openVip(View view);

    void otherInfo();

    void otherRentInfo();

    void personOtherDyn();

    void attentionStatus();

    void attentionOther();

    void cancelAttention();

    void makeEvaluate(int likeOtherStatus);

    void memberInfoConf();
}
