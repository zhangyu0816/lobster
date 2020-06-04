package com.zb.module_mine.iv;

import android.view.View;

public interface EditMemberVMInterface {
    void save(View view);

    void selectImage(int position);

    void toEditNick(View view);

    void toSelectBirthday(View view);

    void toSelectJob(View view);

    void toEditSign(View view);

    void toSelectTag(View view);

    void modifyMemberInfo();
}
