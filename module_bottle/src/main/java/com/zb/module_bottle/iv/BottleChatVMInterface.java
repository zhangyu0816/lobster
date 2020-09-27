package com.zb.module_bottle.iv;

import android.view.View;

public interface BottleChatVMInterface {

    void myBottle();

    void otherInfo();

    void bottleHistoryMsgList(int pageNo);

    void toMemberDetail(View view);

    void toKeyboard(View view);

    void toCamera(View view);

    void closeNotice(View view);

    void openNotice(View view);

    void toEmoji(View view);

    void addEmoji(int position, int emojiRes);

    void deleteContent(View view);

    void sendMsg(View view);
}
