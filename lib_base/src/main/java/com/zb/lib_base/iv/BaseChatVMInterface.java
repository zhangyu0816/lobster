package com.zb.lib_base.iv;

import android.view.View;

import com.zb.lib_base.model.HistoryMsg;
import com.zb.lib_base.model.StanzaInfo;

import java.io.File;

public interface BaseChatVMInterface {

    void toDetail(View view);

    void toImageVideo(View view, HistoryMsg historyMsg, int direction);

    void toVoice(View view, HistoryMsg historyMsg, int direction, int position);

    void check(StanzaInfo stanzaInfo);

    void addEmoji(int position, int emojiRes);

    void closeNotice(View view);

    void openNotice(View view);

    void toVoiceKeyboard(View view);

    void toKeyboard(View view);

    void toCamera(View view);

    void toEmoji(View view);

    void sendMsg(View view);

    void deleteContent(View view);

    void uploadSound(File file, int resTime);

    void stopPlayer();

    void dynDetail(long discoverId);

    void myBottle();

    void otherInfo();

    void historyMsgList(int pageNo);

    void thirdHistoryMsgList(int pageNo);

    void bottleHistoryMsgList(int pageNo);

    void flashHistoryMsgList(int pageNo);
}
