package com.zb.module_card.iv;

import android.view.View;

public interface MemberVideoVMInterface {
    void dynPiazzaList();

    void personOtherDyn();

    void onRefreshForNet(View view);

    void clickItem(int position);

    void doLike(View view, int position);

    void dynDoLike();

    void dynCancelLike();
}
