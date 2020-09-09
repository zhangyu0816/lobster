package com.zb.module_bottle.vm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.api.bottleHistoryMsgListApi;
import com.zb.lib_base.api.myBottleApi;
import com.zb.lib_base.api.otherInfoApi;
import com.zb.lib_base.api.readOverDriftBottleHistoryMsgApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.BottleCacheDb;
import com.zb.lib_base.db.HistoryMsgDb;
import com.zb.lib_base.emojj.EmojiHandler;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.imcore.CustomMessageBody;
import com.zb.lib_base.imcore.ImUtils;
import com.zb.lib_base.model.BottleCache;
import com.zb.lib_base.model.BottleInfo;
import com.zb.lib_base.model.HistoryMsg;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.model.PrivateMsg;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DateUtil;
import com.zb.lib_base.utils.KeyboardStateObserver;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_bottle.R;
import com.zb.module_bottle.adapter.BottleAdapter;
import com.zb.module_bottle.databinding.BottleChatBinding;
import com.zb.module_bottle.iv.BottleChatVMInterface;
import com.zb.module_bottle.windows.BottleVipPW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import io.realm.Realm;
import io.realm.RealmResults;

public class BottleChatViewModel extends BaseViewModel implements BottleChatVMInterface, OnRefreshListener {
    public long driftBottleId;
    public BottleAdapter adapter;
    public BottleAdapter emojiAdapter;
    private BottleChatBinding mBinding;
    public BottleInfo bottleInfo;
    private long otherUserId = 0;
    private BottleCacheDb bottleCacheDb;

    private long historyMsgId = 0;
    private HistoryMsgDb historyMsgDb;
    private List<HistoryMsg> historyMsgList = new ArrayList<>();
    private RealmResults<HistoryMsg> realmResults;
    private int pagerNo = 0;
    private int pageSize = 20;
    private boolean updateAll = false;
    private List<Integer> emojiList = new ArrayList<>();
    private BaseReceiver bottleChatReceiver;
    private MemberInfo memberInfo;


    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (BottleChatBinding) binding;
        bottleCacheDb = new BottleCacheDb(Realm.getDefaultInstance());
        historyMsgDb = new HistoryMsgDb(Realm.getDefaultInstance());
        ImUtils.getInstance(activity).setCallBackForMsg(this::updateMySend);
        setAdapter();
        setProhibitEmoji(mBinding.edContent);
        bottleChatReceiver = new BaseReceiver(activity, "lobster_upMessage/driftBottleId=" + driftBottleId) {
            @Override
            public void onReceive(Context context, Intent intent) {
                CustomMessageBody body = (CustomMessageBody) intent.getSerializableExtra("customMessageBody");
                String msgId = intent.getStringExtra("msgId");
                HistoryMsg historyMsg = new HistoryMsg();
                historyMsg.setThirdMessageId(msgId);
                historyMsg.setFromId(body.getFromId());
                historyMsg.setToId(body.getToId());
                historyMsg.setTitle(body.getSummary());
                historyMsg.setStanza(body.getStanza());
                historyMsg.setMsgType(body.getMsgType());
                historyMsg.setResLink(body.getResLink());
                historyMsg.setResTime(body.getResTime());
                historyMsg.setCreationDate(DateUtil.getNow(DateUtil.yyyy_MM_dd_HH_mm_ss));
                historyMsg.setOtherUserId(otherUserId);
                historyMsg.setMsgChannelType(2);
                historyMsg.setDriftBottleId(driftBottleId);
                historyMsg.setMainUserId(BaseActivity.userId);
                historyMsgDb.saveHistoryMsg(historyMsg);
                historyMsgList.add(adapter.getItemCount(), historyMsg);
                updateTime();
                adapter.notifyItemChanged(adapter.getItemCount() - 1);
                mBinding.chatList.scrollToPosition(adapter.getItemCount() - 1);

                bottleCacheDb.updateBottleCache(driftBottleId, memberInfo.getImage(), memberInfo.getNick(), () -> new Handler().postDelayed(() -> {
                    // 更新会话列表
                    Intent data = new Intent("lobster_singleBottleCache");
                    data.putExtra("driftBottleId", body.getDriftBottleId());
                    activity.sendBroadcast(data);
                }, 500));
            }
        };

        // 发送
        mBinding.edContent.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                if (bottleInfo == null)
                    return false;
                if (bottleInfo.getDestroyType() != 0) {
                    SCToastUtil.showToast(activity, "该漂流瓶已被对方销毁", true);
                    return true;
                }
                if (mBinding.edContent.getText().toString().trim().isEmpty()) {
                    SCToastUtil.showToast(activity, "请输入回复内容", true);
                    return false;
                }
                ImUtils.getInstance(activity).sendChatMessage(1, mBinding.edContent.getText().toString(), "", 0, "【文字】", driftBottleId, 2);
                mBinding.edContent.setText("");
                hintKeyBoard();
            }
            return true;
        });

        KeyboardStateObserver.getKeyboardStateObserver(activity).
                setKeyboardVisibilityListener(height -> {
                    mBinding.setIsEmoji(false);
                    PreferenceUtil.saveIntValue(activity, "keyboardHeight", height);
                    AdapterBinding.viewSize(mBinding.emojiList, MineApp.W, height);
                }, false);
        AdapterBinding.viewSize(mBinding.emojiList, MineApp.W, PreferenceUtil.readIntValue(activity, "keyboardHeight") == 0 ? MineApp.H / 3 : PreferenceUtil.readIntValue(activity, "keyboardHeight"));
    }

    public void onDestroy() {
        bottleChatReceiver.unregisterReceiver();
    }

    @Override
    public void back(View view) {
        super.back(view);
        ImUtils.getInstance(activity).markRead();
        activity.finish();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        if (updateAll) {
            mBinding.refresh.finishRefresh();
            return;
        }
        pagerNo++;
        List<HistoryMsg> tempList = historyMsgDb.getLimitList(realmResults, pagerNo * pageSize, pageSize);
        Collections.reverse(tempList);
        updateTime();
        historyMsgList.addAll(0, tempList);
        adapter.notifyItemRangeChanged(0, tempList.size());
        updateAll = tempList.size() == 0;
        mBinding.refresh.finishRefresh();
    }

    @Override
    public void setAdapter() {
        realmResults = historyMsgDb.getRealmResults(otherUserId, 2, driftBottleId);
        historyMsgList.addAll(historyMsgDb.getLimitList(realmResults, pagerNo * pageSize, pageSize));
        Collections.reverse(historyMsgList);
        updateTime();

        adapter = new BottleAdapter<>(activity, R.layout.item_bottle_chat, historyMsgList, this);
        mBinding.chatList.scrollToPosition(adapter.getItemCount() - 1);
        mBinding.refresh.setEnableLoadMore(false);
        for (int i = 1; i < EmojiHandler.maxEmojiCount; i++) {
            emojiList.add(EmojiHandler.sCustomizeEmojisMap.get(i));
        }
        emojiAdapter = new BottleAdapter<>(activity, R.layout.item_bottle_emoji, emojiList, this);


        myBottle();
    }

    @Override
    public void myBottle() {
        myBottleApi api = new myBottleApi(new HttpOnNextListener<BottleInfo>() {
            @Override
            public void onNext(BottleInfo o) {
                bottleInfo = o;
                mBinding.setNick(bottleInfo.getOtherNick());

                otherUserId = bottleInfo.getUserId() == BaseActivity.userId ? bottleInfo.getOtherUserId() : bottleInfo.getUserId();
                ImUtils.getInstance(activity).setOtherUserId(otherUserId);
                // 记录我们发出去的消息
                HistoryMsg historyMsg = new HistoryMsg();
                historyMsg.setThirdMessageId("1");
                historyMsg.setMainUserId(BaseActivity.userId);
                historyMsg.setFromId(bottleInfo.getUserId());
                historyMsg.setToId(bottleInfo.getOtherUserId());
                historyMsg.setCreationDate(bottleInfo.getCreateTime());
                historyMsg.setStanza(bottleInfo.getText());
                historyMsg.setMsgType(1);
                historyMsg.setTitle("【文字】");
                historyMsg.setResTime(0);
                historyMsg.setResLink("");
                historyMsg.setOtherUserId(otherUserId);
                historyMsg.setMsgChannelType(2);
                historyMsg.setDriftBottleId(driftBottleId);
                historyMsgDb.saveHistoryMsg(historyMsg);
                historyMsgList.add(historyMsg);
                updateTime();
                adapter.notifyDataSetChanged();
                mBinding.chatList.scrollToPosition(adapter.getItemCount() - 1);
                otherInfo();
            }
        }, activity).setDriftBottleId(driftBottleId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void otherInfo() {
        otherInfoApi api = new otherInfoApi(new HttpOnNextListener<MemberInfo>() {
            @Override
            public void onNext(MemberInfo o) {
                memberInfo = o;
                activity.sendBroadcast(new Intent("lobster_bottleNum"));
                ImUtils.getInstance(activity).setChat(true);
                new Thread(() -> bottleHistoryMsgList(1)).start();
            }
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void bottleHistoryMsgList(int pageNo) {
        bottleHistoryMsgListApi api = new bottleHistoryMsgListApi(new HttpOnNextListener<List<PrivateMsg>>() {
            @Override
            public void onNext(List<PrivateMsg> o) {
                for (PrivateMsg privateMsg : o) {
                    HistoryMsg historyMsg = new HistoryMsg();
                    historyMsg.setThirdMessageId(privateMsg.getThirdMessageId());
                    historyMsg.setMainUserId(BaseActivity.userId);
                    historyMsg.setFromId(privateMsg.getFromId());
                    historyMsg.setToId(privateMsg.getToId());
                    historyMsg.setCreationDate(privateMsg.getCreationDate());
                    historyMsg.setStanza(privateMsg.getStanza());
                    historyMsg.setMsgType(privateMsg.getMsgType());
                    historyMsg.setTitle(privateMsg.getTitle());
                    historyMsg.setResTime(privateMsg.getResTime());
                    historyMsg.setResLink(privateMsg.getResLink());
                    historyMsg.setOtherUserId(otherUserId);
                    historyMsg.setMsgChannelType(2);
                    historyMsg.setDriftBottleId(driftBottleId);
                    historyMsgDb.saveHistoryMsg(historyMsg);
                }
                historyMsgId = o.get(o.size() - 1).getId();
                bottleHistoryMsgList(pageNo + 1);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    if (historyMsgId > 0)
                        readOverHistoryMsg();
                    historyMsgList.clear();
                    realmResults = historyMsgDb.getRealmResults(otherUserId, 2, driftBottleId);
                    historyMsgList.addAll(historyMsgDb.getLimitList(realmResults, pagerNo * pageSize, pageSize));
                    Collections.reverse(historyMsgList);
                    updateTime();
                    adapter.notifyDataSetChanged();
                    mBinding.chatList.scrollToPosition(adapter.getItemCount() - 1);

                    if (historyMsgList.size() > 0) {
                        HistoryMsg historyMsg = historyMsgList.get(historyMsgList.size() - 1);
                        BottleCache bottleCache = new BottleCache();
                        bottleCache.setDriftBottleId(driftBottleId);
                        bottleCache.setUserId(otherUserId);
                        bottleCache.setNick(memberInfo.getNick());
                        bottleCache.setImage(memberInfo.getImage());
                        bottleCache.setCreationDate(historyMsg.getCreationDate());
                        bottleCache.setStanza(historyMsg.getStanza());
                        bottleCache.setMsgType(historyMsg.getMsgType());
                        bottleCache.setNoReadNum(0);
                        bottleCache.setPublicTag("");
                        bottleCache.setEffectType(1);
                        bottleCache.setAuthType(1);
                        bottleCache.setMainUserId(BaseActivity.userId);
                        bottleCacheDb.saveBottleCache(bottleCache);
                        // 更新会话列表
                        Intent data = new Intent("lobster_singleBottleCache");
                        data.putExtra("driftBottleId", driftBottleId);
                        activity.sendBroadcast(data);
                    } else {
                        bottleCacheDb.updateBottleCache(driftBottleId, memberInfo.getImage(), memberInfo.getNick(), new BottleCacheDb.CallBack() {
                            @Override
                            public void success() {
                                // 更新会话列表
                                Intent data = new Intent("lobster_singleBottleCache");
                                data.putExtra("driftBottleId", driftBottleId);
                                activity.sendBroadcast(data);
                            }

                            @Override
                            public void fail() {
                                BottleCache bottleCache = new BottleCache();
                                bottleCache.setDriftBottleId(driftBottleId);
                                bottleCache.setUserId(otherUserId);
                                bottleCache.setNick(memberInfo.getNick());
                                bottleCache.setImage(memberInfo.getImage());
                                bottleCache.setCreationDate(DateUtil.getNow(DateUtil.yyyy_MM_dd_HH_mm_ss));
                                bottleCache.setStanza(bottleInfo.getText());
                                bottleCache.setMsgType(1);
                                bottleCache.setNoReadNum(0);
                                bottleCache.setPublicTag("");
                                bottleCache.setEffectType(1);
                                bottleCache.setAuthType(1);
                                bottleCache.setMainUserId(BaseActivity.userId);
                                bottleCacheDb.saveBottleCache(bottleCache);
                                // 更新会话列表
                                Intent data = new Intent("lobster_singleBottleCache");
                                data.putExtra("driftBottleId", driftBottleId);
                                activity.sendBroadcast(data);
                            }
                        });
                    }
                }
            }
        }, activity).setDriftBottleId(driftBottleId).setOtherUserId(otherUserId).setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    /**
     * 清空用户消息
     */
    private void readOverHistoryMsg() {
        if (bottleInfo.getDestroyType() != 0) {
            return;
        }
        readOverDriftBottleHistoryMsgApi api = new readOverDriftBottleHistoryMsgApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
            }
        }, activity).setOtherUserId(otherUserId).setMessageId(historyMsgId).setDriftBottleId(driftBottleId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void toMemberDetail(View view) {
        hintKeyBoard();
        if (MineApp.mineInfo.getMemberType() == 2) {
            ActivityUtils.getCardMemberDetail(otherUserId, false);
            return;
        }
        new BottleVipPW(activity, mBinding.getRoot());
    }

    @Override
    public void toKeyboard(View view) {
        view.setFocusable(false);
        view.setFocusableInTouchMode(false);

        mBinding.setIsEmoji(false);
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.findFocus();
        view.postDelayed(() -> {
            InputMethodManager inputManager =
                    (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(view, 0);
        }, 300);
    }

    @Override
    public void toCamera(View view) {

    }

    @Override
    public void toEmoji(View view) {
        if (mBinding.getIsEmoji()) {
            mBinding.setIsEmoji(false);
        } else {
            hintKeyBoard();
            new Handler().postDelayed(() -> {
                mBinding.edContent.setFocusable(true);
                mBinding.edContent.setFocusableInTouchMode(true);
                mBinding.edContent.requestFocus();
                mBinding.edContent.findFocus();
                mBinding.setIsEmoji(true);
            }, 300);
        }
    }

    @Override
    public void addEmoji(int position, int emojiRes) {
        @SuppressLint("DefaultLocale")
        String content = mBinding.edContent.getText().toString() + String.format("{f:%d}", position + 1);
        mBinding.edContent.setText(content);
        mBinding.edContent.setSelection(content.length());
    }

    @Override
    public void deleteContent(View view) {
        mBinding.edContent.onKeyDown(KeyEvent.KEYCODE_DEL, new KeyEvent(R.id.ed_content, KeyEvent.ACTION_DOWN));
    }

    private void updateMySend(String msgId, int msgType, String stanza, String resLink, int resTime, String summary, long driftBottleId, int msgChannelType) {

        // 会话列表
        BottleCache bottleCache = new BottleCache();
        bottleCache.setDriftBottleId(driftBottleId);
        bottleCache.setUserId(otherUserId);
        bottleCache.setNick(memberInfo.getNick());
        bottleCache.setImage(memberInfo.getImage());
        bottleCache.setCreationDate(DateUtil.getNow(DateUtil.yyyy_MM_dd_HH_mm_ss));
        bottleCache.setStanza(stanza);
        bottleCache.setMsgType(msgType);
        bottleCache.setNoReadNum(0);
        bottleCache.setPublicTag("");
        bottleCache.setEffectType(1);
        bottleCache.setAuthType(1);
        bottleCache.setMainUserId(BaseActivity.userId);
        bottleCacheDb.saveBottleCache(bottleCache);
        // 更新会话列表
        Intent data = new Intent("lobster_singleBottleCache");
        data.putExtra("driftBottleId", driftBottleId);
        activity.sendBroadcast(data);

        // 记录我们发出去的消息
        HistoryMsg historyMsg = new HistoryMsg();
        historyMsg.setThirdMessageId(msgId);
        historyMsg.setMainUserId(BaseActivity.userId);
        historyMsg.setFromId(BaseActivity.userId);
        historyMsg.setToId(otherUserId);
        historyMsg.setCreationDate(DateUtil.getNow(DateUtil.yyyy_MM_dd_HH_mm_ss));
        historyMsg.setStanza(stanza);
        historyMsg.setMsgType(msgType);
        historyMsg.setTitle(summary);
        historyMsg.setResTime(resTime);
        historyMsg.setResLink(resLink);
        historyMsg.setOtherUserId(otherUserId);
        historyMsg.setMsgChannelType(msgChannelType);
        historyMsg.setDriftBottleId(driftBottleId);
        historyMsgDb.saveHistoryMsg(historyMsg);

        historyMsgList.add(historyMsg);
        updateTime();
        adapter.notifyItemChanged(adapter.getItemCount() - 1);
        mBinding.chatList.scrollToPosition(adapter.getItemCount() - 1);
    }

    private void updateTime() {
        String time = "";
        if (historyMsgList.size() > 0) {
            historyMsgList.get(0).setShowTime(true);
            time = historyMsgList.get(0).getCreationDate();
            for (int i = 1; i < historyMsgList.size(); i++) {
                if (DateUtil.getDateCount(historyMsgList.get(i).getCreationDate(), time, DateUtil.yyyy_MM_dd_HH_mm_ss, 1000f * 60f) > 3) {
                    time = historyMsgList.get(i).getCreationDate();
                    historyMsgList.get(i).setShowTime(true);
                } else {
                    historyMsgList.get(i).setShowTime(false);
                }
            }
        }
    }
}
