package com.zb.module_mine.vm;

import android.view.View;

import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.iv.NewsManagerVMInterface;

public class NewsManagerViewModel extends BaseViewModel implements NewsManagerVMInterface {

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void right(View view) {
        super.right(view);
    }

    @Override
    public void toGiftNews(View view) {
        ActivityUtils.getMineGiftNews();
    }

    @Override
    public void toReviewNews(View view) {

    }

    @Override
    public void toLikeNews(View view) {

    }

    @Override
    public void toLobsterNews(View view) {

    }
}
