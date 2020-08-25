package com.zb.module_mine.iv;

import android.view.View;

import com.zb.lib_base.model.StanzaInfo;
import com.zb.lib_base.model.SystemMsg;

public interface SystemMsgVMInterface {

    void systemHistoryMsgList();

    void clearHistoryMsg(long messageId);

    void toImageVideo(View view, SystemMsg systemMsg);

    void toVoice(View view, SystemMsg systemMsg, int direction);

    void check(StanzaInfo stanzaInfo);
}
