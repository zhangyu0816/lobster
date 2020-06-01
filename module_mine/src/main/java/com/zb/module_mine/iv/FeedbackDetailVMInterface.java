package com.zb.module_mine.iv;

import android.view.View;

public interface FeedbackDetailVMInterface {

    void submit(View view);

    void selectImage(int position);

    void addFeedBack(String images);
}
