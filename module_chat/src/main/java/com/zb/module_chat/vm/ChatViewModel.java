package com.zb.module_chat.vm;

import android.Manifest;
import android.annotation.SuppressLint;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
import com.zb.lib_base.api.historyMsgListApi;
import com.zb.lib_base.api.myImAccountInfoApi;
import com.zb.lib_base.api.otherImAccountInfoApi;
import com.zb.lib_base.api.otherInfoApi;
import com.zb.lib_base.api.readOverHistoryMsgApi;
import com.zb.lib_base.api.thirdHistoryMsgListApi;
import com.zb.lib_base.api.thirdReadChatApi;
import com.zb.lib_base.api.uploadSoundApi;
import com.zb.lib_base.db.ChatListDb;
import com.zb.lib_base.db.HistoryMsgDb;
import com.zb.lib_base.db.ResFileDb;
import com.zb.lib_base.http.HttpChatUploadManager;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.imcore.CustomMessageBody;
import com.zb.lib_base.imcore.LoginSampleHelper;
import com.zb.lib_base.model.ChatList;
import com.zb.lib_base.model.HistoryMsg;
import com.zb.lib_base.model.ImAccount;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.model.ResourceUrl;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DateUtil;
import com.zb.lib_base.utils.uploadImage.PhotoManager;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_chat.BR;
import com.zb.module_chat.R;
import com.zb.module_chat.adapter.ChatAdapter;
import com.zb.module_chat.databinding.ChatChatBinding;
import com.zb.module_chat.iv.ChatVMInterface;
import com.zb.module_chat.views.SoundView;

import java.io.File;
import java.util.ArrayList;
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

        photoManager = new PhotoManager(activity, () ->
                sendChatMessage(2, "", photoManager.jointWebUrl(","), 0, "【图片】"));

        // 发送
        mBinding.edContent.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                if (mBinding.getContent().isEmpty()) {
                    return false;
                }
                sendChatMessage(1, mBinding.getContent(), "", 0, "【文字】");
            }
            return false;
        });

        soundView = new SoundView(activity, mBinding.audioBtn, new SoundView.CallBack() {
            @Override
            public void sendSoundBack(int resTime, String audioPath) {
                uploadSound(new File(audioPath), resTime);
            }

            @Override
            public void playEndBack(View view) {
                stopVoiceDrawable();
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
                    soundView.start();
                    mBinding.tvSpeak.setText("松开  结束");
                    stopPlayer();
                    stopVoiceDrawable();
                    return true;
                case MotionEvent.ACTION_UP:
                    mBinding.audioBtn.setVisibility(View.GONE);
                    soundView.stop();
                    mBinding.tvSpeak.setText("按住  说话");
                    break;
            }
            return false;
        });
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void setAdapter() {
        realmResults = historyMsgDb.getRealmResults();
        historyMsgList.addAll(historyMsgDb.getLimitList(realmResults, pagerNo * pageSize, pageSize));
        adapter = new ChatAdapter<>(activity, R.layout.item_chat, historyMsgList, this);
        mBinding.chatList.scrollToPosition(adapter.getItemCount() - 1);
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
                mBinding.setVariable(BR.viewModel, this);

                if (loginHelper.getImCore() == null) {
                    myImAccountInfoApi();
                } else {
                    otherImAccountInfoApi();
                }
                thirdReadChat();
                new Thread(() -> historyMsgList(0)).start();
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
                        thirdHistoryMsgList(0);
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
                    historyMsgDb.saveHistoryMsg(historyMsg);
                }
                thirdHistoryMsgList(pageNo + 1);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    historyMsgList.clear();
                    realmResults = historyMsgDb.getRealmResults();
                    historyMsgList.addAll(historyMsgDb.getLimitList(realmResults, pagerNo * pageSize, pageSize));
                    adapter.notifyDataSetChanged();
                }
            }
        }, activity).setOtherUserId(otherUserId).setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void toDetail(View view) {
        ActivityUtils.getCardMemberDetail(otherUserId);
    }

    @Override
    public void toImageVideo(HistoryMsg historyMsg) {
        if (historyMsg.getMsgType() == 2) {
            ArrayList<String> imageList = new ArrayList<>();
            imageList.add(historyMsg.getResLink());
            MNImage.imageBrowser(activity, mBinding.getRoot(), imageList, 0);
        } else {
            ActivityUtils.getCameraVideoPlay(resFileDb.getResFile(historyMsg.getResLink()).getFilePath());
        }
    }


    @Override
    public void toVoice(View view, HistoryMsg historyMsg, int direction) {

        // direction 0 左  1右
        stopVoiceDrawable();
        preImageView = (ImageView) view;
        preDirection = direction;

        preImageView.setImageResource(direction == 0 ? R.drawable.voice_chat_anim_left : R.drawable.voice_chat_anim_right);
        drawable = (AnimationDrawable) preImageView.getDrawable();
        drawable.start();

        soundView.soundPlayer(resFileDb.getResFile(historyMsg.getResLink()).getFilePath(), view);
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
        mBinding.setIsVoice(!mBinding.getIsVoice());
    }

    @Override
    public void toCamera(View view) {
        getPermissions();
    }

    @Override
    public void toEmoji(View view) {

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
                sendChatMessage(3, "", o.getUrl(), resTime,  "【语音】");
                soundView.setResTime(0);
            }
        }, activity).setFile(file);
        HttpChatUploadManager.getInstance().doHttpDeal(api);
    }

    /**
     * 权限
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            performCodeWithPermission("虾菇需要访问读外部存储权限", new BaseActivity.PermissionCallback() {
                @Override
                public void hasPermission() {
                    setPermissions();
                }

                @Override
                public void noPermission() {
                }
            }, Manifest.permission.READ_EXTERNAL_STORAGE);
        } else {
            setPermissions();
        }
    }

    private void setPermissions() {
        ActivityUtils.getCameraMain(activity, false,false);
    }

    /**
     * 上传图片
     *
     * @param fileName
     */
    public void uploadImage(String fileName) {
        photoManager.addFileUpload(0, new File(fileName));
    }

    /**
     * 清空用户消息
     */
    private void readOverHistoryMsg() {
        readOverHistoryMsgApi api = new readOverHistoryMsgApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                thirdHistoryMsgList(0);
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
        YWMessageBody body = new CustomMessageBody(msgType, stanza, resLink, resTime, BaseActivity.userId, otherUserId, summary);
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
        historyMsgList.add(historyMsg);
        adapter.notifyDataSetChanged();
        mBinding.chatList.scrollToPosition(adapter.getItemCount() - 1);
        historyMsgDb.saveHistoryMsg(historyMsg);

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
        chatListDb.saveChatList(chatList);
    }
}
