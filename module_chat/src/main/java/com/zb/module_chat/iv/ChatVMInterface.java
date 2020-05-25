package com.zb.module_chat.iv;

import android.view.View;
import android.widget.ImageView;

import com.zb.lib_base.model.HistoryMsg;

public interface ChatVMInterface {

    void otherInfo();

    void historyMsgList(int pageNo);

    void thirdHistoryMsgList(int pageNo);

    void toDetail(View view);

    void toImageVideo(HistoryMsg historyMsg);

    void toVoice(ImageView view, HistoryMsg historyMsg, int direction);
}
