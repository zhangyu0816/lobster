package com.zb.module_bottle.vm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
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
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import io.realm.RealmResults;

public class BottleChatViewModel extends BaseViewModel implements BottleChatVMInterface, OnRefreshListener {
    public long driftBottleId;
    public boolean isNotice = false;
    public BottleAdapter adapter;
    public BottleAdapter emojiAdapter;
    private BottleChatBinding mBinding;
    public BottleInfo bottleInfo;
    private long otherUserId = 0;

    private long historyMsgId = 0;
    private List<HistoryMsg> historyMsgList = new ArrayList<>();
    private RealmResults<HistoryMsg> realmResults;
    private int pagerNo = 0;
    private int pageSize = 20;
    private boolean updateAll = false;
    private List<Integer> emojiList = new ArrayList<>();
    private BaseReceiver bottleChatReceiver;
    public MemberInfo memberInfo;
    private boolean isFirst = true;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (BottleChatBinding) binding;
        mBinding.setMemberInfo(new MemberInfo());
        ImUtils.getInstance().setCallBackForMsg(this::updateMySend);
        setAdapter();
        setProhibitEmoji(mBinding.edContent);
        bottleChatReceiver = new BaseReceiver(activity, "lobster_upMessage/driftBottleId=" + driftBottleId) {
            @Override
            public void onReceive(Context context, Intent intent) {
                CustomMessageBody body = (CustomMessageBody) intent.getSerializableExtra("customMessageBody");
                String msgId = intent.getStringExtra("msgId");
                HistoryMsg historyMsg = HistoryMsg.createHistory(msgId, body, otherUserId, 2, driftBottleId);
                HistoryMsgDb.getInstance().saveHistoryMsg(historyMsg);

                boolean hasId = false;
                if (isNotice) {
                    for (HistoryMsg item : historyMsgList) {
                        if (TextUtils.equals(item.getThirdMessageId(), msgId)) {
                            hasId = true;
                            break;
                        }
                    }
                }
                if (!hasId) {
                    historyMsgList.add(adapter.getItemCount(), historyMsg);
                    updateTime();
                    adapter.notifyItemChanged(adapter.getItemCount() - 1);
                    mBinding.chatList.scrollToPosition(adapter.getItemCount() - 1);
                }

                BottleCacheDb.getInstance().updateBottleCache(driftBottleId, memberInfo.getImage(), memberInfo.getNick(), () -> new Handler().postDelayed(() -> {
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
                if (Objects.requireNonNull(mBinding.edContent.getText()).toString().trim().isEmpty()) {
                    SCToastUtil.showToast(activity, "请输入回复内容", true);
                    return false;
                }
                ImUtils.getInstance().sendChatMessage(activity, 1, mBinding.edContent.getText().toString(), "", 0, "【文字】", driftBottleId, 2);
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

        mBinding.chatList.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_MOVE && isSoftShowing() && isFirst) {
                isFirst = false;
                hintKeyBoard();
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                isFirst = true;
            }
            return false;
        });
    }

    public void onDestroy() {
        try {
            bottleChatReceiver.unregisterReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void back(View view) {
        super.back(view);
        ImUtils.getInstance().markRead();
        activity.finish();
    }

    public void onResume() {
        if (otherUserId > 0)
            ImUtils.getInstance().setChat(true, activity);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        if (updateAll) {
            mBinding.refresh.finishRefresh();
            return;
        }
        pagerNo++;
        List<HistoryMsg> tempList = HistoryMsgDb.getInstance().getLimitList(realmResults, pagerNo * pageSize, pageSize);
        Collections.reverse(tempList);
        updateTime();
        historyMsgList.addAll(0, tempList);
        adapter.notifyItemRangeChanged(0, tempList.size());
        updateAll = tempList.size() == 0;
        mBinding.refresh.finishRefresh();
    }

    @Override
    public void setAdapter() {
        realmResults = HistoryMsgDb.getInstance().getRealmResults(otherUserId, 2, driftBottleId);
        historyMsgList.addAll(HistoryMsgDb.getInstance().getLimitList(realmResults, pagerNo * pageSize, pageSize));
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
                ImUtils.getInstance().setOtherUserId(otherUserId);
                ImUtils.getInstance().setChat(true, activity);
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
                HistoryMsgDb.getInstance().saveHistoryMsg(historyMsg);
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
            @SuppressLint("CheckResult")
            @Override
            public void onNext(MemberInfo o) {
                memberInfo = o;
                mBinding.setMemberInfo(memberInfo);
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
                    HistoryMsgDb.getInstance().saveHistoryMsg(HistoryMsg.createHistoryForPrivate(privateMsg, otherUserId, 2, driftBottleId));
                }
                if (historyMsgId == 0)
                    historyMsgId = o.get(0).getId();
                bottleHistoryMsgList(pageNo + 1);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    if (historyMsgId > 0)
                        readOverHistoryMsg();
                    historyMsgList.clear();
                    realmResults = HistoryMsgDb.getInstance().getRealmResults(otherUserId, 2, driftBottleId);
                    historyMsgList.addAll(HistoryMsgDb.getInstance().getLimitList(realmResults, pagerNo * pageSize, pageSize));
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
                        BottleCacheDb.getInstance().saveBottleCache(bottleCache);
                        // 更新会话列表
                        Intent data = new Intent("lobster_singleBottleCache");
                        data.putExtra("driftBottleId", driftBottleId);
                        activity.sendBroadcast(data);

                        activity.sendBroadcast(new Intent("lobster_bottleNum"));
                    } else {
                        BottleCacheDb.getInstance().updateBottleCache(driftBottleId, memberInfo.getImage(), memberInfo.getNick(), new BottleCacheDb.CallBack() {
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
                                BottleCacheDb.getInstance().saveBottleCache(bottleCache);
                                // 更新会话列表
                                Intent data = new Intent("lobster_singleBottleCache");
                                data.putExtra("driftBottleId", driftBottleId);
                                activity.sendBroadcast(data);

                                activity.sendBroadcast(new Intent("lobster_bottleNum"));
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
        new BottleVipPW(mBinding.getRoot());
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
        String content = Objects.requireNonNull(mBinding.edContent.getText()).toString() + String.format("{f:%d}", position + 1);
        mBinding.edContent.setText(content);
        mBinding.edContent.setSelection(content.length());
    }

    @Override
    public void deleteContent(View view) {
        mBinding.edContent.onKeyDown(KeyEvent.KEYCODE_DEL, new KeyEvent(R.id.ed_content, KeyEvent.ACTION_DOWN));
    }

    @Override
    public void sendMsg(View view) {
        if (bottleInfo == null)
            return;
        if (bottleInfo.getDestroyType() != 0) {
            SCToastUtil.showToast(activity, "该漂流瓶已被对方销毁", true);
            return;
        }
        if (Objects.requireNonNull(mBinding.edContent.getText()).toString().trim().isEmpty()) {
            SCToastUtil.showToast(activity, "请输入回复内容", true);
            return;
        }
        ImUtils.getInstance().sendChatMessage(activity, 1, mBinding.edContent.getText().toString(), "", 0, "【文字】", driftBottleId, 2);
        mBinding.edContent.setText("");
        mBinding.setIsEmoji(false);
        hintKeyBoard();
    }

    private void updateMySend(String msgId, CustomMessageBody body) {
        // 会话列表
        BottleCache bottleCache = new BottleCache();
        bottleCache.setDriftBottleId(driftBottleId);
        bottleCache.setUserId(otherUserId);
        bottleCache.setNick(memberInfo.getNick());
        bottleCache.setImage(memberInfo.getImage());
        bottleCache.setCreationDate(DateUtil.getNow(DateUtil.yyyy_MM_dd_HH_mm_ss));
        bottleCache.setStanza(body.getStanza());
        bottleCache.setMsgType(body.getMsgType());
        bottleCache.setNoReadNum(0);
        bottleCache.setPublicTag("");
        bottleCache.setEffectType(1);
        bottleCache.setAuthType(1);
        bottleCache.setMainUserId(BaseActivity.userId);
        BottleCacheDb.getInstance().saveBottleCache(bottleCache);
        // 更新会话列表
        Intent data = new Intent("lobster_singleBottleCache");
        data.putExtra("driftBottleId", driftBottleId);
        activity.sendBroadcast(data);

        // 记录我们发出去的消息
        body.setFromId(BaseActivity.userId);
        body.setToId(otherUserId);
        HistoryMsg historyMsg = HistoryMsg.createHistory(msgId, body, otherUserId, 2, driftBottleId);
        HistoryMsgDb.getInstance().saveHistoryMsg(historyMsg);
        historyMsgList.add(historyMsg);
        updateTime();
        adapter.notifyItemChanged(adapter.getItemCount() - 1);
        mBinding.chatList.scrollToPosition(adapter.getItemCount() - 1);
    }

    private void updateTime() {
        String time;
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
