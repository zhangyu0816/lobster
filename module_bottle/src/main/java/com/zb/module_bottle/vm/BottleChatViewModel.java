package com.zb.module_bottle.vm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.contact.YWContactFactory;
import com.alibaba.mobileim.conversation.IYWConversationService;
import com.alibaba.mobileim.conversation.YWConversation;
import com.alibaba.mobileim.conversation.YWMessage;
import com.alibaba.mobileim.conversation.YWMessageBody;
import com.alibaba.mobileim.conversation.YWMessageChannel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.api.bottleHistoryMsgListApi;
import com.zb.lib_base.api.myBottleApi;
import com.zb.lib_base.api.myImAccountInfoApi;
import com.zb.lib_base.api.otherImAccountInfoApi;
import com.zb.lib_base.api.readOverHistoryMsgApi;
import com.zb.lib_base.api.thirdHistoryMsgListApi;
import com.zb.lib_base.api.thirdReadChatApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.BottleCacheDb;
import com.zb.lib_base.db.HistoryMsgDb;
import com.zb.lib_base.emojj.EmojiHandler;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.imcore.CustomMessageBody;
import com.zb.lib_base.imcore.LoginSampleHelper;
import com.zb.lib_base.model.BottleCache;
import com.zb.lib_base.model.BottleInfo;
import com.zb.lib_base.model.HistoryMsg;
import com.zb.lib_base.model.ImAccount;
import com.zb.lib_base.model.MineInfo;
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
    public MineInfo mineInfo;
    public BottleInfo bottleInfo;
    private long otherUserId = 0;
    private BottleCacheDb bottleCacheDb;
    // 阿里百川
    private LoginSampleHelper loginHelper;
    private String otherIMUserId;
    private YWConversation conversation;
    private int timeOut = 10 * 1000;
    private IYWConversationService mConversationService;
    private long historyMsgId = 0;
    private HistoryMsgDb historyMsgDb;
    private List<HistoryMsg> historyMsgList = new ArrayList<>();
    private RealmResults<HistoryMsg> realmResults;
    private int pagerNo = 0;
    private int pageSize = 20;
    private boolean updateAll = false;
    private List<Integer> emojiList = new ArrayList<>();
    private BaseReceiver bottleChatReceiver;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (BottleChatBinding) binding;
        bottleCacheDb = new BottleCacheDb(Realm.getDefaultInstance());
        historyMsgDb = new HistoryMsgDb(Realm.getDefaultInstance());
        mineInfo = mineInfoDb.getMineInfo();
        loginHelper = LoginSampleHelper.getInstance();
        setAdapter();

        bottleChatReceiver = new BaseReceiver(activity, "lobster_upMessage/driftBottleId=" + driftBottleId) {
            @Override
            public void onReceive(Context context, Intent intent) {
                YWMessage ywMessage = (YWMessage) intent.getSerializableExtra("ywMessage");
                CustomMessageBody body = (CustomMessageBody) LoginSampleHelper.unpack(ywMessage.getContent());
                HistoryMsg historyMsg = new HistoryMsg();
                historyMsg.setId(ywMessage.getMsgId());
                historyMsg.setFromId(body.getFromId());
                historyMsg.setToId(body.getToId());
                historyMsg.setTitle(body.getSummary());
                historyMsg.setStanza(body.getStanza());
                historyMsg.setMsgType(body.getMsgType());
                historyMsg.setResLink(body.getResLink());
                historyMsg.setResTime(body.getResTime());
                historyMsg.setCreationDate(DateUtil.getNow(DateUtil.yyyy_MM_dd_HH_mm_ss));
                historyMsg.setOtherUserId(otherUserId);
                historyMsg.setThirdMessageId(ywMessage.getMsgId() + "");
                historyMsg.setMsgChannelType(2);
                historyMsg.setDriftBottleId(driftBottleId);
                historyMsg.setMainUserId(BaseActivity.userId);
                historyMsgList.add(adapter.getItemCount(), historyMsg);
                adapter.notifyItemChanged(adapter.getItemCount());
                mBinding.chatList.scrollToPosition(adapter.getItemCount() - 1);
            }
        };

        // 发送
        mBinding.edContent.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                if (bottleInfo.getDestroyType() != 0) {
                    SCToastUtil.showToast(activity, "该漂流瓶已被对方销毁", true);
                    return true;
                }
                if (mBinding.edContent.getText().toString().trim().isEmpty()) {
                    SCToastUtil.showToast(activity, "请输入回复内容", true);
                    return false;
                }
                sendChatMessage(1, mBinding.edContent.getText().toString(), "", 0, "【文字】");
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
                otherUserId = bottleInfo.getOtherUserId();
                mBinding.setNick(bottleInfo.getOtherNick());

                adapter.notifyDataSetChanged();
                mBinding.chatList.scrollToPosition(adapter.getItemCount() - 1);

                if (loginHelper.getImCore() == null) {
                    myImAccountInfoApi();
                } else {
                    otherImAccountInfoApi();
                }
                thirdReadChat();
                bottleCacheDb.updateReadNum(driftBottleId);
                activity.sendBroadcast(new Intent("lobster_bottleNum"));

                new Thread(() -> bottleHistoryMsgList(0)).start();

            }
        }, activity).setDriftBottleId(driftBottleId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void bottleHistoryMsgList(int pageNo) {
        bottleHistoryMsgListApi api = new bottleHistoryMsgListApi(new HttpOnNextListener<List<HistoryMsg>>() {
            @Override
            public void onNext(List<HistoryMsg> o) {
                for (HistoryMsg historyMsg : o) {
                    historyMsg.setOtherUserId(otherUserId);
                    historyMsg.setMainUserId(BaseActivity.userId);
                    historyMsgDb.saveHistoryMsg(historyMsg);
                }
                historyMsgId = o.get(o.size() - 1).getId();
                bottleHistoryMsgList(pageNo + 1);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    if (historyMsgId > 0) {
                        readOverHistoryMsg();
                    } else {
                        thirdHistoryMsgList(1);
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
        readOverHistoryMsgApi api = new readOverHistoryMsgApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                thirdHistoryMsgList(1);
            }
        }, activity).setOtherUserId(otherUserId).setMessageId(historyMsgId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void thirdHistoryMsgList(int pageNo) {
        thirdHistoryMsgListApi api = new thirdHistoryMsgListApi(new HttpOnNextListener<List<HistoryMsg>>() {
            @Override
            public void onNext(List<HistoryMsg> o) {
                for (HistoryMsg historyMsg : o) {
                    historyMsg.setOtherUserId(otherUserId);
                    historyMsg.setMainUserId(BaseActivity.userId);
                    historyMsgDb.saveHistoryMsg(historyMsg);
                }
                thirdHistoryMsgList(pageNo + 1);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    historyMsgList.clear();
                    realmResults = historyMsgDb.getRealmResults(otherUserId, 2, driftBottleId);
                    historyMsgList.addAll(historyMsgDb.getLimitList(realmResults, pagerNo * pageSize, pageSize));
                    if (historyMsgList.size() == 0) {
                        HistoryMsg historyMsg = new HistoryMsg();
                        historyMsg.setStanza(bottleInfo.getText());
                        historyMsg.setMsgType(1);
                        historyMsg.setFromId(otherUserId);
                        historyMsg.setCreationDate(bottleInfo.getCreateTime());
                        historyMsgList.add(historyMsg);
                    }
                    Collections.reverse(historyMsgList);
                    updateTime();
                    adapter.notifyDataSetChanged();
                    mBinding.chatList.scrollToPosition(adapter.getItemCount() - 1);
                }
            }
        }, activity).setOtherUserId(otherUserId).setPageNo(pageNo).setMsgChannelType(1);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void toMemberDetail(View view) {
        hintKeyBoard();
        if (mineInfo.getMemberType() == 2) {
            ActivityUtils.getCardMemberDetail(bottleInfo.getOtherUserId(), false);
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

    /**
     * 发送消息
     *
     * @param msgType
     * @param stanza
     * @param resLink
     * @param resTime
     * @param summary
     */
    private void sendChatMessage(final int msgType, final String stanza, final String resLink, final int resTime, final String summary) {
        if (bottleInfo == null)
            return;
        YWMessageBody body = new CustomMessageBody(msgType, stanza, resLink, resTime, BaseActivity.userId, bottleInfo.getOtherUserId(), summary, driftBottleId, 2);
        body.setSummary(body.getSummary());
        body.setContent(loginHelper.pack(body));
        final YWMessage message = YWMessageChannel.createCustomMessage(body);
        checkConversation();
        conversation.getMessageSender().sendMessage(message, timeOut, new IWxCallback() {

            @Override
            public void onSuccess(Object... arg0) {
                updateMySend(message, stanza, msgType, resLink, resTime, summary);
            }

            @Override
            public void onProgress(int arg0) {

            }

            @Override
            public void onError(int arg0, String arg1) {

            }
        });
    }

    private void updateMySend(YWMessage message, String stanza, int msgType, String resLink, int resTime, String title) {

        // 会话列表
        BottleCache bottleCache = new BottleCache();
        bottleCache.setDriftBottleId(driftBottleId);
        bottleCache.setUserId(otherUserId);
        bottleCache.setCreationDate(DateUtil.getNow(DateUtil.yyyy_MM_dd_HH_mm_ss));
        bottleCache.setStanza(stanza);
        bottleCache.setMsgType(msgType);
        bottleCache.setNoReadNum(0);
        bottleCache.setMainUserId(BaseActivity.userId);
        bottleCacheDb.saveBottleCache(bottleCache);

        // 记录我们发出去的消息
        HistoryMsg historyMsg = new HistoryMsg();
        historyMsg.setCreationDate(DateUtil.getNow(DateUtil.yyyy_MM_dd_HH_mm_ss));
        historyMsg.setId(message.getMsgId());
        historyMsg.setFromId(BaseActivity.userId);
        historyMsg.setToId(otherUserId);
        historyMsg.setMsgType(msgType);
        historyMsg.setResLink(resLink);
        historyMsg.setResTime(resTime);
        historyMsg.setStanza(stanza);
        historyMsg.setTitle(title);
        historyMsg.setOtherUserId(otherUserId);
        historyMsg.setThirdMessageId(message.getMsgId() + "");
        historyMsg.setMsgChannelType(2);
        historyMsg.setDriftBottleId(driftBottleId);
        historyMsg.setMainUserId(BaseActivity.userId);
        historyMsgDb.saveHistoryMsg(historyMsg);
        historyMsgList.add(historyMsg);
        updateTime();
        adapter.notifyDataSetChanged();
        mBinding.chatList.scrollToPosition(adapter.getItemCount() - 1);
        activity.sendBroadcast(new Intent("lobster_updateBottle"));
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

    /**
     * 清除未读数量
     */
    private void thirdReadChat() {
        thirdReadChatApi api = new thirdReadChatApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {

            }
        }, activity).setOtherUserId(bottleInfo.getOtherUserId());
        HttpManager.getInstance().doHttpDeal(api);
    }

    /**
     * 阿里百川登录账号
     */
    private void myImAccountInfoApi() {
        myImAccountInfoApi api = new myImAccountInfoApi(new HttpOnNextListener<ImAccount>() {
            @Override
            public void onNext(ImAccount o) {
                loginHelper.loginOut_Sample();
                loginHelper.login_Sample(activity, o.getImUserId(), o.getImPassWord());
                otherImAccountInfoApi();
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    /**
     * 对方的阿里百川账号
     */
    private void otherImAccountInfoApi() {
        otherImAccountInfoApi api = new otherImAccountInfoApi(new HttpOnNextListener<ImAccount>() {
            @Override
            public void onNext(ImAccount o) {
                otherIMUserId = o.getImUserId();
                mConversationService = loginHelper.getConversationService();
                conversation = mConversationService.getConversationByUserId(otherIMUserId, LoginSampleHelper.APP_KEY);
                checkConversation();
            }
        }, activity);
        api.setOtherUserId(bottleInfo.getOtherUserId());
        HttpManager.getInstance().doHttpDeal(api);
    }

    /**
     * 检查 conversation
     */
    private void checkConversation() {
        if (conversation == null) { // 这里必须判空
            IYWContact contact = YWContactFactory.createAPPContact(otherIMUserId, LoginSampleHelper.APP_KEY);
            conversation = mConversationService.getConversationCreater().createConversationIfNotExist(contact);
        }
    }


}
