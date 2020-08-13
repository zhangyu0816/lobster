package com.zb.module_home.iv;

import android.view.View;

public interface FollowVMInterface {

    void attentionDyn();

    void onRefreshForNet(View view);

    void clickItem(int position);

    void doLike(View view, int position);

    void dynDoLike();

    void dynCancelLike();

}
