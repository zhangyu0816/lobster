package com.zb.lib_base.iv;

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
