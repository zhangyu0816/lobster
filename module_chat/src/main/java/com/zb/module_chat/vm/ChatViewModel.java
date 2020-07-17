package com.zb.module_chat.vm;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.contact.YWContactFactory;
import com.alibaba.mobileim.conversation.IYWConversationService;
import com.alibaba.mobileim.conversation.YWConversation;
import com.alibaba.mobileim.conversation.YWMessage;
import com.alibaba.mobileim.conversation.YWMessageBody;
import com.alibaba.mobileim.conversation.YWMessageChannel;
import com.maning.imagebrowserlibrary.MNImage;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.api.dynDetailApi;
import com.zb.lib_base.api.historyMsgListApi;
import com.zb.lib_base.api.myImAccountInfoApi;
import com.zb.lib_base.api.otherImAccountInfoApi;
import com.zb.lib_base.api.otherInfoApi;
import com.zb.lib_base.api.readOverHistoryMsgApi;
import com.zb.lib_base.api.thirdHistoryMsgListApi;
import com.zb.lib_base.api.thirdReadChatApi;
import com.zb.lib_base.api.uploadSoundApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.ChatListDb;
import com.zb.lib_base.db.HistoryMsgDb;
import com.zb.lib_base.db.ResFileDb;
import com.zb.lib_base.emojj.EmojiHandler;
import com.zb.lib_base.http.HttpChatUploadManager;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.imcore.CustomMessageBody;
import com.zb.lib_base.imcore.LoginSampleHelper;
import com.zb.lib_base.model.ChatList;
import com.zb.lib_base.model.DiscoverInfo;
import com.zb.lib_base.model.HistoryMsg;
import com.zb.lib_base.model.ImAccount;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.model.ResourceUrl;
import com.zb.lib_base.model.StanzaInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DataCleanManager;
import com.zb.lib_base.utils.DateUtil;
import com.zb.lib_base.utils.DownLoad;
import com.zb.lib_base.utils.KeyboardStateObserver;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.uploadImage.PhotoManager;
import com.zb.lib_base.views.SoundView;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_chat.BR;
import com.zb.module_chat.R;
import com.zb.module_chat.adapter.ChatAdapter;
import com.zb.module_chat.databinding.ChatChatBinding;
import com.zb.module_chat.iv.ChatVMInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import io.realm.Realm;
import io.realm.RealmResults;

public class ChatViewModel extends BaseViewModel implements ChatVMInterface, OnRefreshListener {
    public long otherUserId;
    public MemberInfo memberInfo;
    public ChatAdapter adapter;
    public MineInfo mineInfo;
    public ResFileDb resFileDb;
    public ChatAdapter emojiAdapter;
    private List<Integer> emojiList = new ArrayList<>();
    private ChatListDb chatListDb;
    private ChatChatBinding mBinding;
    private List<HistoryMsg> historyMsgList = new ArrayList<>();
    private RealmResults<HistoryMsg> realmResults;
    private int pagerNo = 0;
    private int pageSize = 20;
    private HistoryMsgDb historyMsgDb;
    private LoginSampleHelper loginHelper;
    // 阿里百川
    private String otherIMUserId;
    private YWConversation conversation;
    private int timeOut = 10 * 1000;
    private IYWConversationService mConversationService;
    private long historyMsgId = 0;
    private boolean updateAll = false;
    private AnimationDrawable drawable; // 语音播放

    private PhotoManager photoManager;
    private SoundView soundView;
    private ImageView preImageView;
    private int preDirection;
    private ObjectAnimator animator;
    private BaseReceiver chatReceiver;
    private int soundPosition = -1;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (ChatChatBinding) binding;
        resFileDb = new ResFileDb(Realm.getDefaultInstance());
        historyMsgDb = new HistoryMsgDb(Realm.getDefaultInstance());
        chatListDb = new ChatListDb(Realm.getDefaultInstance());
        mineInfo = mineInfoDb.getMineInfo();
        loginHelper = LoginSampleHelper.getInstance();
        setAdapter();

        setProhibitEmoji(mBinding.edContent);

        photoManager = new PhotoManager(activity, () -> {
            sendChatMessage(2, "", photoManager.jointWebUrl(","), 0, "【图片】");
            photoManager.deleteAllFile();
        });

        photoManager.setChat(true);

        // 发送
        mBinding.edContent.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                if (mBinding.getContent().trim().isEmpty()) {
                    return false;
                }
                sendChatMessage(1, mBinding.getContent(), "", 0, "【文字】");
                mBinding.setContent("");
                closeImplicit(mBinding.edContent);
            }
            return true;
        });

        soundView = new SoundView(activity, mBinding.audioBtn);
        soundView.setCallBack(new SoundView.CallBack() {
            @Override
            public void sendSoundBack(int resTime, String audioPath) {
                uploadSound(new File(audioPath), resTime);
            }

            @Override
            public void playEndBack(View view) {
                stopVoiceDrawable();
                if (soundPosition != -1)
                    adapter.notifyItemChanged(soundPosition);
            }

            @Override
            public void soundEnd() {
                mBinding.tvSpeak.setPressed(false);
            }
        });

        mBinding.tvSpeak.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mBinding.tvSpeak.setPressed(true);
                    mBinding.tvSpeak.setText("松开  结束");
                    getPermissions(3);
                    return true;
                case MotionEvent.ACTION_UP:
                    mBinding.audioBtn.setVisibility(View.GONE);
                    soundView.stop();
                    mBinding.tvSpeak.setText("按住  说话");
                    break;
            }
            return false;
        });

        chatReceiver = new BaseReceiver(activity, "lobster_upMessage/friend=" + otherUserId) {
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
                historyMsg.setMainUserId(BaseActivity.userId);
                historyMsgList.add(adapter.getItemCount(), historyMsg);
                adapter.notifyItemChanged(adapter.getItemCount());
                mBinding.chatList.scrollToPosition(adapter.getItemCount() - 1);
            }
        };

        KeyboardStateObserver.getKeyboardStateObserver(activity).
                setKeyboardVisibilityListener(height -> {
                    mBinding.setIsVoice(false);
                    mBinding.setIsEmoji(false);
                    PreferenceUtil.saveIntValue(activity, "keyboardHeight", height);
                    AdapterBinding.viewSize(mBinding.emojiList, MineApp.W, height);
                }, false);
        AdapterBinding.viewSize(mBinding.emojiList, MineApp.W, PreferenceUtil.readIntValue(activity, "keyboardHeight") == 0 ? MineApp.H / 3 : PreferenceUtil.readIntValue(activity, "keyboardHeight"));

    }

    @Override
    public void back(View view) {
        super.back(view);
        hintKeyBoard();
        DataCleanManager.deleteFile(new File(activity.getCacheDir(), "videos"));
        DataCleanManager.deleteFile(new File(activity.getCacheDir(), "images"));
        chatReceiver.unregisterReceiver();
        try {
            mConversationService.markReaded(conversation);
        } catch (Exception e) {
        }
        activity.finish();
    }

    @Override
    public void setAdapter() {
        realmResults = historyMsgDb.getRealmResults(otherUserId, 1, 0);
        historyMsgList.addAll(historyMsgDb.getLimitList(realmResults, pagerNo * pageSize, pageSize));
        Collections.reverse(historyMsgList);
        updateTime();
        adapter = new ChatAdapter<>(activity, R.layout.item_chat, historyMsgList, this);
        mBinding.chatList.scrollToPosition(adapter.getItemCount() - 1);
        mBinding.refresh.setEnableLoadMore(false);
        for (int i = 1; i < EmojiHandler.maxEmojiCount; i++) {
            emojiList.add(EmojiHandler.sCustomizeEmojisMap.get(i));
        }
        emojiAdapter = new ChatAdapter<>(activity, R.layout.item_emoji, emojiList, this);

        mBinding.bottomLayout.setVisibility(otherUserId < 10010 ? View.GONE : View.VISIBLE);
        otherInfo();
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
    public void otherInfo() {
        otherInfoApi api = new otherInfoApi(new HttpOnNextListener<MemberInfo>() {
            @Override
            public void onNext(MemberInfo o) {
                memberInfo = o;
                mBinding.setVariable(BR.viewModel, ChatViewModel.this);
                if (loginHelper.getImCore() == null) {
                    myImAccountInfoApi();
                } else {
                    otherImAccountInfoApi();
                }
                new Thread(() -> historyMsgList(1)).start();
            }
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void historyMsgList(int pageNo) {
        historyMsgListApi api = new historyMsgListApi(new HttpOnNextListener<List<HistoryMsg>>() {
            @Override
            public void onNext(List<HistoryMsg> o) {
                for (HistoryMsg historyMsg : o) {
                    historyMsg.setMsgChannelType(1);
                    historyMsg.setDriftBottleId(0);
                    historyMsg.setOtherUserId(otherUserId);
                    historyMsg.setMainUserId(BaseActivity.userId);
                    historyMsgDb.saveHistoryMsg(historyMsg);
                }
                historyMsgId = o.get(o.size() - 1).getId();
                historyMsgList(pageNo + 1);
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
        }, activity).setOtherUserId(otherUserId).setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void thirdHistoryMsgList(int pageNo) {
        thirdHistoryMsgListApi api = new thirdHistoryMsgListApi(new HttpOnNextListener<List<HistoryMsg>>() {
            @Override
            public void onNext(List<HistoryMsg> o) {
                for (HistoryMsg historyMsg : o) {
                    historyMsg.setMsgChannelType(1);
                    historyMsg.setDriftBottleId(0);
                    historyMsg.setOtherUserId(otherUserId);
                    historyMsg.setMainUserId(BaseActivity.userId);
                    historyMsgDb.saveHistoryMsg(historyMsg);
                }
                thirdHistoryMsgList(pageNo + 1);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    thirdReadChat();
                    historyMsgList.clear();
                    realmResults = historyMsgDb.getRealmResults(otherUserId, 1, 0);
                    historyMsgList.addAll(historyMsgDb.getLimitList(realmResults, pagerNo * pageSize, pageSize));
                    Collections.reverse(historyMsgList);
                    updateTime();
                    adapter.notifyDataSetChanged();
                    mBinding.chatList.scrollToPosition(adapter.getItemCount() - 1);

                    if (historyMsgList.size() > 0) {
                        HistoryMsg historyMsg = historyMsgList.get(historyMsgList.size() - 1);
                        ChatList chatList = new ChatList();
                        chatList.setUserId(otherUserId);
                        chatList.setNick(memberInfo.getNick());
                        chatList.setImage(memberInfo.getImage());
                        chatList.setCreationDate(historyMsg.getCreationDate());
                        chatList.setStanza(historyMsg.getStanza());
                        chatList.setMsgType(historyMsg.getMsgType());
                        chatList.setNoReadNum(0);
                        chatList.setPublicTag("");
                        chatList.setEffectType(1);
                        chatList.setAuthType(1);
                        chatList.setChatType(otherUserId == BaseActivity.dynUserId ? 5 : 4);
                        chatListDb.saveChatList(chatList);
                        Intent data = new Intent("lobster_updateChat");
                        data.putExtra("userId", otherUserId);
                        data.putExtra("updateImage", true);
                        activity.sendBroadcast(data);
                        activity.sendBroadcast(new Intent("lobster_unReadCount"));
                    } else {
                        chatListDb.updateMember(otherUserId, memberInfo.getImage(), memberInfo.getNick(), otherUserId == BaseActivity.dynUserId ? 5 : 4, new ChatListDb.CallBack() {
                            @Override
                            public void success() {
                                Intent data = new Intent("lobster_updateChat");
                                data.putExtra("userId", otherUserId);
                                data.putExtra("updateImage", true);
                                activity.sendBroadcast(data);
                                activity.sendBroadcast(new Intent("lobster_unReadCount"));
                            }

                            @Override
                            public void fail() {
                                ChatList chatList = new ChatList();
                                chatList.setUserId(otherUserId);
                                chatList.setNick(memberInfo.getNick());
                                chatList.setImage(memberInfo.getImage());
                                chatList.setCreationDate(DateUtil.getNow(DateUtil.yyyy_MM_dd_HH_mm_ss));
                                chatList.setStanza("欢迎留言");
                                chatList.setMsgType(1);
                                chatList.setNoReadNum(0);
                                chatList.setPublicTag("");
                                chatList.setEffectType(1);
                                chatList.setAuthType(1);
                                chatList.setChatType(otherUserId == BaseActivity.dynUserId ? 5 : 4);
                                chatListDb.saveChatList(chatList);
                                Intent data = new Intent("lobster_updateChat");
                                data.putExtra("userId", otherUserId);
                                data.putExtra("updateImage", true);
                                activity.sendBroadcast(data);
                                activity.sendBroadcast(new Intent("lobster_unReadCount"));
                            }
                        });
                    }

                }
            }
        }, activity).setOtherUserId(otherUserId).setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void toDetail(View view) {
        if (otherUserId > 10010)
            ActivityUtils.getCardMemberDetail(otherUserId, false);
    }

    private ImageView ivPlay;
    private ImageView ivProgress;

    @Override
    public void toImageVideo(View view, HistoryMsg historyMsg, int direction) {
        if (historyMsg.getMsgType() == 2) {
            ArrayList<String> imageList = new ArrayList<>();
            imageList.add(historyMsg.getResLink());
            MNImage.imageBrowser(activity, mBinding.getRoot(), imageList, 0, false, null);
        } else {
            if (direction == 0) {
                ivPlay = view.findViewById(R.id.iv_play);
                ivProgress = view.findViewById(R.id.iv_progress);
            } else {
                ivPlay = view.findViewById(R.id.iv_play_mine);
                ivProgress = view.findViewById(R.id.iv_progress_mine);
            }
            ivPlay.setVisibility(View.GONE);
            DownLoad.getFilePath(historyMsg.getResLink(), BaseActivity.getDownloadFile(".mp4").getAbsolutePath(), new DownLoad.CallBack() {
                @Override
                public void success(String filePath) {
                    ivPlay.setVisibility(View.VISIBLE);
                    ivProgress.setVisibility(View.GONE);
                    if (animator != null)
                        animator.cancel();
                    ActivityUtils.getCameraVideoPlay(filePath);
                }

                @Override
                public void onLoading(long total, long current) {
                    animator = ObjectAnimator.ofFloat(ivProgress, "rotation", 0, 360).setDuration(700);
                    animator.setRepeatMode(ValueAnimator.RESTART);
                    animator.setRepeatCount(Animation.INFINITE);
                    animator.start();
                    ivPlay.setVisibility(View.GONE);
                    ivProgress.setVisibility(View.VISIBLE);
                }
            });
        }
    }


    @Override
    public void toVoice(View view, HistoryMsg historyMsg, int direction, int position) {
        DownLoad.getFilePath(historyMsg.getResLink(), BaseActivity.getDownloadFile(".amr").getAbsolutePath(), filePath -> {
            soundPosition = position;
            // direction 0 左  1右
            stopVoiceDrawable();
            ImageView voiceView = null;
            if (direction == 0) {
                voiceView = view.findViewById(R.id.iv_voice_left);
            } else {
                voiceView = view.findViewById(R.id.iv_voice_right);
            }
            preImageView = voiceView;
            preDirection = direction;

            preImageView.setImageResource(direction == 0 ? R.drawable.voice_chat_anim_left : R.drawable.voice_chat_anim_right);
            drawable = (AnimationDrawable) preImageView.getDrawable();
            drawable.start();

            soundView.soundPlayer(filePath, view);
        });
    }

    private void stopVoiceDrawable() {
        if (drawable != null) {
            stopPlayer();
            preImageView.setImageResource(preDirection == 0 ? R.mipmap.icon_voice_3_left : R.mipmap.icon_voice_3_right);
            drawable.stop();
            drawable = null;
        }
    }

    @Override
    public void toVoiceKeyboard(View view) {
        hintKeyBoard();
        mBinding.setIsEmoji(false);
        mBinding.setIsVoice(!mBinding.getIsVoice());
        if (mBinding.getIsVoice()) {
            getPermissions(2);
        }
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
        hintKeyBoard();
        mBinding.setIsEmoji(false);
        getPermissions(1);
    }

    @Override
    public void toEmoji(View view) {
        if (mBinding.getIsEmoji()) {
            mBinding.setIsEmoji(false);
        } else {
            mBinding.setIsVoice(false);
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
        String content = mBinding.getContent() + String.format("{f:%d}", position + 1);
        mBinding.edContent.setText(content);
        mBinding.edContent.setSelection(content.length());
    }

    @Override
    public void deleteContent(View view) {
        mBinding.edContent.onKeyDown(KeyEvent.KEYCODE_DEL, new KeyEvent(R.id.ed_content, KeyEvent.ACTION_DOWN));
    }

    @Override
    public void stopPlayer() {
        soundView.stopPlayer();
    }

    @Override
    public void uploadSound(File file, int resTime) {
        uploadSoundApi api = new uploadSoundApi(new HttpOnNextListener<ResourceUrl>() {
            @Override
            public void onNext(ResourceUrl o) {
                sendChatMessage(3, "", o.getUrl(), resTime, "【语音】");
                soundView.setResTime(0);
            }
        }, activity).setFile(file);
        HttpChatUploadManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void check(StanzaInfo stanzaInfo) {
        if (stanzaInfo.getLink().contains("person_detail")) {
            ActivityUtils.getCardMemberDetail(Long.parseLong(stanzaInfo.getLink().replace("zw://appview/person_detail?userId=", "")), false);
        } else {
            dynDetail(Long.parseLong(stanzaInfo.getLink().replace("zw://appview/dynamic_detail?friendDynId=", "")));
        }

    }

    @Override
    public void dynDetail(long discoverId) {
        dynDetailApi api = new dynDetailApi(new HttpOnNextListener<DiscoverInfo>() {
            @Override
            public void onNext(DiscoverInfo o) {
                if (o.getVideoUrl().isEmpty()) {
                    ActivityUtils.getHomeDiscoverDetail(discoverId);
                } else {
                    ActivityUtils.getHomeDiscoverVideo(discoverId);
                }
            }
        }, activity).setFriendDynId(discoverId);
        api.setShowProgress(false);
        HttpManager.getInstance().doHttpDeal(api);
    }

    /**
     * 上传图片
     *
     * @param fileName
     */
    public void uploadImage(String fileName) {
        photoManager.addFileUpload(-1, new File(fileName));
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
        api.setOtherUserId(otherUserId);
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

    /**
     * 清除未读数量
     */
    private void thirdReadChat() {
        thirdReadChatApi api = new thirdReadChatApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {

            }
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
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
        if (memberInfo == null)
            return;
        YWMessageBody body = new CustomMessageBody(msgType, stanza, resLink, resTime, BaseActivity.userId, otherUserId, summary, 0, 1);
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
        // 记录我们发出去的消息
        HistoryMsg historyMsg = new HistoryMsg();
        historyMsg.setCreationDate(DateUtil.getNow(DateUtil.yyyy_MM_dd_HH_mm_ss));
        historyMsg.setId(message.getMsgId());// TODO　需要测试
        historyMsg.setFromId(BaseActivity.userId);
        historyMsg.setToId(otherUserId);
        historyMsg.setMsgType(msgType);
        historyMsg.setResLink(resLink);
        historyMsg.setResTime(resTime);
        historyMsg.setStanza(stanza);
        historyMsg.setTitle(title);
        historyMsg.setOtherUserId(otherUserId);
        historyMsg.setMainUserId(BaseActivity.userId);
        historyMsgList.add(historyMsg);
        updateTime();
        adapter.notifyItemChanged(adapter.getItemCount() - 1);
        mBinding.chatList.scrollToPosition(adapter.getItemCount() - 1);

        ChatList chatList = new ChatList();
        chatList.setUserId(otherUserId);
        chatList.setNick(memberInfo.getNick());
        chatList.setImage(memberInfo.getImage());
        chatList.setCreationDate(historyMsg.getCreationDate());
        chatList.setStanza(historyMsg.getStanza());
        chatList.setMsgType(historyMsg.getMsgType());
        chatList.setNoReadNum(0);
        chatList.setPublicTag("");
        chatList.setEffectType(1);
        chatList.setAuthType(1);
        chatList.setChatType(4);
        chatListDb.saveChatList(chatList);

        Intent data = new Intent("lobster_updateChat");
        data.putExtra("userId", otherUserId);
        activity.sendBroadcast(data);
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
     * 权限
     */
    private void getPermissions(int type) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            performCodeWithPermission("虾菇需要访问读写外部存储权限、相机权限及录音权限", new BaseActivity.PermissionCallback() {
                        @Override
                        public void hasPermission() {
                            setPermissions(type);
                        }

                        @Override
                        public void noPermission() {
                        }
                    }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO);
        } else {
            setPermissions(type);
        }
    }

    private void setPermissions(int type) {
        if (type == 1) {
            ActivityUtils.getCameraMain(activity, false, false);
        } else if (type == 3) {
            soundView.start();
            stopPlayer();
            stopVoiceDrawable();
        }
    }
}
