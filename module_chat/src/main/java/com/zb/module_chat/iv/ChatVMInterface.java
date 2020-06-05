package com.zb.module_chat.iv;

import android.view.View;

import com.zb.lib_base.model.HistoryMsg;

import java.io.File;

public interface ChatVMInterface {

    void otherInfo();

    void historyMsgList(int pageNo);

    void thirdHistoryMsgList(int pageNo);

    void toDetail(View view);

    void toImageVideo(View view, HistoryMsg historyMsg, int direction);

    void toVoice(View view, HistoryMsg historyMsg, int direction);

    void toVoiceKeyboard(View view);

    void toKeyboard(View view);

    void toCamera(View view);

    void toEmoji(View view);

    void addEmoji(int position,int emojiRes);

    void deleteContent(View view);

    void stopPlayer();

    void uploadSound(File file, int resTime);
}
