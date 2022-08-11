package com.zb.lib_base.vm;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.SystemClock;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.R;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.adapter.BaseAdapter;
import com.zb.lib_base.api.bottleHistoryMsgListApi;
import com.zb.lib_base.api.dynDetailApi;
import com.zb.lib_base.api.flashHistoryMsgListApi;
import com.zb.lib_base.api.flashReadOverHistoryMsgApi;
import com.zb.lib_base.api.historyMsgListApi;
import com.zb.lib_base.api.myBottleApi;
import com.zb.lib_base.api.myInfoApi;
import com.zb.lib_base.api.otherImAccountInfoApi;
import com.zb.lib_base.api.otherInfoApi;
import com.zb.lib_base.api.readOverDriftBottleHistoryMsgApi;
import com.zb.lib_base.api.readOverHistoryMsgApi;
import com.zb.lib_base.api.thirdHistoryMsgListApi;
import com.zb.lib_base.api.thirdReadChatApi;
import com.zb.lib_base.api.uploadSoundApi;
import com.zb.lib_base.api.uploadVideoApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.databinding.BaseChatBinding;
import com.zb.lib_base.db.AreaDb;
import com.zb.lib_base.db.BottleCacheDb;
import com.zb.lib_base.db.ChatListDb;
import com.zb.lib_base.db.HistoryMsgDb;
import com.zb.lib_base.emojj.EmojiHandler;
import com.zb.lib_base.emojj.EmojiUtil;
import com.zb.lib_base.http.HttpChatUploadManager;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.iv.BaseChatVMInterface;
import com.zb.lib_base.mimc.CustomMessageBody;
import com.zb.lib_base.mimc.UserManager;
import com.zb.lib_base.model.BottleCache;
import com.zb.lib_base.model.BottleInfo;
import com.zb.lib_base.model.ChatList;
import com.zb.lib_base.model.DiscoverInfo;
import com.zb.lib_base.model.HistoryMsg;
import com.zb.lib_base.model.ImAccount;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.model.PrivateMsg;
import com.zb.lib_base.model.ResourceUrl;
import com.zb.lib_base.model.StanzaInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DataCleanManager;
import com.zb.lib_base.utils.DateUtil;
import com.zb.lib_base.utils.DownLoad;
import com.zb.lib_base.utils.KeyboardStateObserver;
import com.zb.lib_base.utils.MNImage;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.utils.OpenNotice;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.utils.SoftHideKeyBoardUtil;
import com.zb.lib_base.utils.glide.BlurTransformation;
import com.zb.lib_base.utils.uploadImage.PhotoManager;
import com.zb.lib_base.views.SoundView;
import com.zb.lib_base.windows.BottleVipPW;
import com.zb.lib_base.windows.SelectorPW;
import com.zb.lib_base.windows.TextPW;
import com.zb.lib_base.windows.VipAdPW;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import io.realm.RealmResults;

public class BaseChatViewModel extends BaseViewModel implements BaseChatVMInterface, OnRefreshListener {
    public long otherUserId = 0;
    public long driftBottleId = 0;
    public long flashTalkId = 0;
    public boolean isNotice = false;
    public int msgChannelType = 0; // 1：普通聊天  2：漂流瓶  3：闪聊
    public boolean isLockImage = true;

    public BaseAdapter adapter;
    private List<HistoryMsg> historyMsgList = new ArrayList<>();
    private RealmResults<HistoryMsg> realmResults;
    private int pagerNo = 0;
    private int pageSize = 20;
    private long historyMsgId = 0;
    private boolean updateAll = false;

    public BaseAdapter emojiAdapter;
    private List<Integer> emojiList = new ArrayList<>();

    private BaseChatBinding mBinding;
    public MemberInfo memberInfo;
    private BottleInfo bottleInfo;
    private boolean isFirst = true;
    private PhotoManager photoManager;
    private SoundView soundView;
    private int soundPosition = -1;

    private ImageView ivPlay;
    private ImageView ivProgress;
    private ObjectAnimator animator;
    private AnimationDrawable drawable; // 语音播放
    private ImageView preImageView;
    private int preDirection;
    private int myChatCount;
    private int otherChatCount;

    private BaseReceiver cameraReceiver;
    private BaseReceiver chatReceiver;
    private BaseReceiver bottleChatReceiver;
    private BaseReceiver flashChatReceiver;
    private BaseReceiver openVipReceiver;

    private String miUserId = "";
    private String[] pre;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (BaseChatBinding) binding;
        mBinding.setViewModel(this);
        mBinding.setContent("");
        mBinding.setIsVoice(false);
        mBinding.setIsEmoji(false);
        mBinding.setMsgChannelType(msgChannelType);
        mBinding.setMemberInfo(new MemberInfo());

        // 基础配置
        initTool();
        // 广播
        initReceiver();
        // 发送
        mBinding.edContent.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                if (msgChannelType == 2) {
                    if (bottleInfo == null)
                        return false;
                    if (bottleInfo.getDestroyType() != 0) {
                        SCToastUtil.showToast(activity, "该漂流瓶已被对方销毁", true);
                        return true;
                    }
                }

                if (mBinding.getContent().trim().isEmpty()) {
                    SCToastUtil.showToast(activity, "请输入回复内容", true);
                    return false;
                }
                closeImplicit(mBinding.edContent);
                UserManager.getInstance().sendMsg(
                        miUserId, UserManager.TEXT, 1, mBinding.getContent(), "",
                        0, "【文字】", driftBottleId, flashTalkId, msgChannelType
                );
                mBinding.setContent("");
            }
            return true;
        });

        setAdapter();
    }

    public void onDestroy() {
        try {
            cameraReceiver.unregisterReceiver();
            if (chatReceiver != null)
                chatReceiver.unregisterReceiver();
            if (bottleChatReceiver != null)
                bottleChatReceiver.unregisterReceiver();
            if (flashChatReceiver != null)
                flashChatReceiver.unregisterReceiver();
            if (openVipReceiver != null)
                openVipReceiver.unregisterReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void back(View view) {
        super.back(view);
        hintKeyBoard();
        DataCleanManager.deleteFile(new File(activity.getCacheDir(), "videos"));
        DataCleanManager.deleteFile(new File(activity.getCacheDir(), "images"));
        activity.finish();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        if (updateAll) {
            mBinding.refresh.finishRefresh();
            return;
        }
        pagerNo++;
        List<HistoryMsg> tempList = HistoryMsgDb.getInstance().getLimitList(realmResults, pagerNo * pageSize, pageSize);
        updateAll = tempList.size() == 0;
        if (!updateAll) {
            Collections.reverse(tempList);
            historyMsgList.addAll(0, tempList);
            updateTime();
            adapter.notifyDataSetChanged();
        }
        mBinding.refresh.finishRefresh();
    }

    @Override
    public void setAdapter() {
        if (msgChannelType == 1) {
            isLockImage = false;
        } else if (msgChannelType == 2) {
            isLockImage = MineApp.mineInfo.getMemberType() == 1;
        } else if (msgChannelType == 3) {
            myChatCount = Math.min(HistoryMsgDb.getInstance().getMyChatCount(otherUserId, flashTalkId), 10);
            otherChatCount = Math.min(HistoryMsgDb.getInstance().getOtherChatCount(otherUserId, flashTalkId), 10);
            isLockImage = (myChatCount + otherChatCount) < 20;
        }
        mBinding.setIsLockImage(isLockImage);
        if (otherUserId != 0) {
            realmResults = HistoryMsgDb.getInstance().getRealmResults(otherUserId, msgChannelType, driftBottleId, flashTalkId);
            historyMsgList.addAll(HistoryMsgDb.getInstance().getLimitList(realmResults, pagerNo * pageSize, pageSize));
            Collections.reverse(historyMsgList);
            updateTime();
        }
        adapter = new BaseAdapter<>(activity, R.layout.item_chat, historyMsgList, this);
        mBinding.refresh.setEnableLoadMore(false);
        MineApp.getApp().getFixedThreadPool().execute(() -> {
            SystemClock.sleep(500);
            activity.runOnUiThread(() -> mBinding.chatList.scrollToPosition(adapter.getItemCount() - 1));
        });

        for (int i = 1; i < EmojiHandler.maxEmojiCount; i++) {
            emojiList.add(EmojiHandler.sCustomizeEmojisMap.get(i));
        }
        emojiAdapter = new BaseAdapter<>(activity, R.layout.item_emoji, emojiList, BaseChatViewModel.this);
        if (msgChannelType == 1) {
            mBinding.bottomLayout.setVisibility(otherUserId < 10010 ? View.GONE : View.VISIBLE);
            otherInfo();
        } else if (msgChannelType == 2) {
            myBottle();
        } else if (msgChannelType == 3) {
            otherInfo();
        }
    }

    @Override
    public void toDetail(View view) {
        hintKeyBoard();
        if (msgChannelType == 1) {
            if (otherUserId > 10010)
                ActivityUtils.getCardMemberDetail(otherUserId, false);
        } else if (msgChannelType == 2) {
            if (MineApp.mineInfo.getMemberType() == 2) {
                ActivityUtils.getCardMemberDetail(otherUserId, false);
                return;
            }
            if (MineApp.isFirstOpen)
                new VipAdPW(mBinding.getRoot(), 100, "");
            else
                new BottleVipPW(mBinding.getRoot());
        } else if (msgChannelType == 3) {
            if (!isLockImage)
                ActivityUtils.getCardMemberDetail(otherUserId, false);
            else
                SCToastUtil.showToast(activity, "每人发10句可以解锁资料哦～", true);
        }
    }

    @Override
    public void toImageVideo(View view, HistoryMsg historyMsg, int direction) {
        if (historyMsg.getMsgType() == 2) {
            ArrayList<String> imageList = new ArrayList<>();
            imageList.add(historyMsg.getResLink());
            MNImage.imageBrowser(activity, mBinding.getRoot(), historyMsg.getOtherUserId(), imageList, 0, false, null);
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
                public void success(String filePath, Bitmap bitmap) {
                    ivPlay.setVisibility(View.VISIBLE);
                    ivProgress.setVisibility(View.GONE);
                    if (animator != null)
                        animator.cancel();
                    ActivityUtils.getCameraVideoPlay(filePath, false, false);
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
        DownLoad.getFilePath(historyMsg.getResLink(), BaseActivity.getDownloadFile(".amr").getAbsolutePath(), (filePath, bitmap) -> {
            soundPosition = position;
            // direction 0 左  1右
            stopVoiceDrawable();
            ImageView voiceView;
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
    public void check(StanzaInfo stanzaInfo) {
        if (stanzaInfo.getLink().contains("person_detail")) {
            ActivityUtils.getCardMemberDetail(Long.parseLong(stanzaInfo.getLink().replace("zw://appview/person_detail?userId=", "")), false);
        } else {
            dynDetail(Long.parseLong(stanzaInfo.getLink().replace("zw://appview/dynamic_detail?friendDynId=", "")));
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

    @Override
    public void myBottle() {
        myBottleApi api = new myBottleApi(new HttpOnNextListener<BottleInfo>() {
            @Override
            public void onNext(BottleInfo o) {
                bottleInfo = o;
                otherUserId = bottleInfo.getUserId() == BaseActivity.userId ? bottleInfo.getOtherUserId() : bottleInfo.getUserId();
                realmResults = HistoryMsgDb.getInstance().getRealmResults(otherUserId, msgChannelType, driftBottleId, flashTalkId);
                historyMsgList.addAll(HistoryMsgDb.getInstance().getLimitList(realmResults, pagerNo * pageSize, pageSize));
                Collections.reverse(historyMsgList);
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
                historyMsgList.add(0, historyMsg);
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
                otherImAccountInfo();
                if (msgChannelType == 1 || msgChannelType == 3) {
                    if (isLockImage) {
                        setMemberUI();
                    }
                    MineApp.getApp().getFixedThreadPool().execute(() -> {
                        RequestOptions cropOptions = new RequestOptions();
                        MultiTransformation<Bitmap> multiTransformation = new MultiTransformation<>(new BlurTransformation());
                        cropOptions.transform(multiTransformation);
                        activity.runOnUiThread(() -> Glide.with(activity).asBitmap().load(memberInfo.getSingleImage()).apply(cropOptions).into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                mBinding.ivBg.setImageBitmap(resource);
                            }
                        }));
                    });

                    if (msgChannelType == 1)
                        historyMsgList(1);
                    else
                        flashHistoryMsgList(1);
                } else if (msgChannelType == 2) {
                    bottleHistoryMsgList(1);
                }
            }
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void otherImAccountInfo() {
        otherImAccountInfoApi api = new otherImAccountInfoApi(new HttpOnNextListener<ImAccount>() {
            @Override
            public void onNext(ImAccount o) {
                miUserId = o.getImUserId();
            }
        }, activity);
        api.setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void historyMsgList(int pageNo) {
        historyMsgListApi api = new historyMsgListApi(new HttpOnNextListener<List<PrivateMsg>>() {
            @Override
            public void onNext(List<PrivateMsg> o) {
                for (PrivateMsg privateMsg : o) {
                    HistoryMsgDb.getInstance().saveHistoryMsg(HistoryMsg.createHistoryForPrivate(privateMsg, otherUserId, 1, 0));
                }
                historyMsgList(pageNo + 1);
                if (historyMsgId == 0)
                    historyMsgId = o.get(0).getId();
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
        thirdHistoryMsgListApi api = new thirdHistoryMsgListApi(new HttpOnNextListener<List<PrivateMsg>>() {
            @Override
            public void onNext(List<PrivateMsg> o) {
                for (PrivateMsg privateMsg : o) {
                    HistoryMsgDb.getInstance().saveHistoryMsg(HistoryMsg.createHistoryForPrivate(privateMsg, otherUserId, 1, 0));
                }
                thirdHistoryMsgList(pageNo + 1);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    thirdReadChat();
                    historyMsgList.clear();
                    realmResults = HistoryMsgDb.getInstance().getRealmResults(otherUserId, msgChannelType, driftBottleId, flashTalkId);
                    historyMsgList.addAll(HistoryMsgDb.getInstance().getLimitList(realmResults, pagerNo * pageSize, pageSize));
                    Collections.reverse(historyMsgList);
                    updateTime();
                    adapter.notifyDataSetChanged();
                    mBinding.chatList.scrollToPosition(adapter.getItemCount() - 1);

                    if (historyMsgList.size() > 0) {
                        HistoryMsg historyMsg = historyMsgList.get(historyMsgList.size() - 1);
                        setChatList(historyMsg, otherUserId == BaseActivity.dynUserId ? 5 : 4, true);
                    } else {
                        ChatListDb.getInstance().updateMember(otherUserId, memberInfo.getImage(), memberInfo.getNick(), otherUserId == BaseActivity.dynUserId ? 5 : 4, new ChatListDb.CallBack() {
                            @Override
                            public void success() {
                                Intent data = new Intent("lobster_updateChat");
                                data.putExtra("userId", otherUserId);
                                data.putExtra("updateImage", true);
                                LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(data);
                                LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_unReadCount"));
                            }

                            @Override
                            public void fail() {
                                setMemberUI();
                            }
                        });
                    }

                }
            }
        }, activity).setOtherUserId(otherUserId).setPageNo(pageNo);
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
                        readOverDriftBottleHistoryMsg();
                    historyMsgList.clear();
                    realmResults = HistoryMsgDb.getInstance().getRealmResults(otherUserId, msgChannelType, driftBottleId, flashTalkId);
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
                        LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(data);

                        LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_bottleNum"));
                    } else {
                        BottleCacheDb.getInstance().updateBottleCache(driftBottleId, memberInfo.getImage(), memberInfo.getNick(), new BottleCacheDb.CallBack() {
                            @Override
                            public void success() {
                                // 更新会话列表
                                Intent data = new Intent("lobster_singleBottleCache");
                                data.putExtra("driftBottleId", driftBottleId);
                                LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(data);
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
                                LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(data);

                                LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_bottleNum"));
                            }
                        });
                    }
                }
            }
        }, activity).setDriftBottleId(driftBottleId).setOtherUserId(otherUserId).setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void flashHistoryMsgList(int pageNo) {
        flashHistoryMsgListApi api = new flashHistoryMsgListApi(new HttpOnNextListener<List<PrivateMsg>>() {
            @Override
            public void onNext(List<PrivateMsg> o) {
                for (PrivateMsg privateMsg : o) {
                    HistoryMsgDb.getInstance().saveHistoryMsg(HistoryMsg.createHistoryForPrivate(privateMsg, otherUserId, flashTalkId));
                }
                if (historyMsgId == 0)
                    historyMsgId = o.get(0).getId();
                flashHistoryMsgList(pageNo + 1);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    if (historyMsgId > 0)
                        flashReadOverHistoryMsg();
                    historyMsgList.clear();
                    realmResults = HistoryMsgDb.getInstance().getRealmResults(otherUserId, msgChannelType, driftBottleId, flashTalkId);
                    historyMsgList.addAll(HistoryMsgDb.getInstance().getLimitList(realmResults, pagerNo * pageSize, pageSize));
                    Collections.reverse(historyMsgList);
                    myChatCount = Math.min(HistoryMsgDb.getInstance().getMyChatCount(otherUserId, flashTalkId), 10);
                    otherChatCount = Math.min(HistoryMsgDb.getInstance().getOtherChatCount(otherUserId, flashTalkId), 10);
                    isLockImage = (myChatCount + otherChatCount) < 20;
                    updateTime();
                    if (isLockImage)
                        setMemberUI();
                    adapter.notifyDataSetChanged();
                    mBinding.chatList.scrollToPosition(adapter.getItemCount() - 1);

                    if (historyMsgList.size() > 0) {
                        HistoryMsg historyMsg = historyMsgList.get(historyMsgList.size() - 1);
                        setChatList(historyMsg, 6, true);
                    } else {
                        ChatListDb.getInstance().updateMember(otherUserId, memberInfo.getImage(), memberInfo.getNick(), 6, new ChatListDb.CallBack() {
                            @Override
                            public void success() {
                                Intent data = new Intent("lobster_updateChat");
                                data.putExtra("userId", otherUserId);
                                data.putExtra("updateImage", true);
                                data.putExtra("flashTalkId", flashTalkId);
                                LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(data);
                                LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_unReadCount"));
                            }

                            @Override
                            public void fail() {
                                HistoryMsg historyMsg = new HistoryMsg();
                                historyMsg.setStanza("每人发10句可以解锁资料哦~");
                                historyMsg.setCreationDate(DateUtil.getNow(DateUtil.yyyy_MM_dd_HH_mm_ss));
                                historyMsg.setMsgType(1);
                                setChatList(historyMsg, 6, false);
                            }
                        });
                    }
                }
            }
        }, activity).setOtherUserId(otherUserId).setFlashTalkId(flashTalkId).setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void setMemberUI() {
        String address = "";
        float temp = 0f;
        if (!memberInfo.getDistance().isEmpty())
            temp = Float.parseFloat(memberInfo.getDistance());

        @SuppressLint("DefaultLocale")
        String distance = temp == 0f ? "" : String.format("%.1f" + (temp < 1000 ? "m" : "km"), temp < 1000 ? temp : temp / 1000f);
        if (memberInfo.getDistrictId() != 0)
            address = AreaDb.getInstance().getDistrictName(memberInfo.getDistrictId());
        else if (memberInfo.getCityId() != 0)
            address = AreaDb.getInstance().getCityName(memberInfo.getCityId());
        else if (memberInfo.getProvinceId() != 0) {
            address = AreaDb.getInstance().getProvinceName(memberInfo.getProvinceId());
        }
        if (!distance.isEmpty())
            address = address + "(" + distance + ")";

        memberInfo.setTitle(memberInfo.getNick() + "，" + DateUtil.getAge(memberInfo.getBirthday(), memberInfo.getAge()) + "，" + address + (memberInfo.getJob().isEmpty() ? "" : ("，" + memberInfo.getJob())));
        String[] colors = new String[]{"#FF3158", "#7A44F5"};

        String htmlStr = "<font color='#C3BDCD'>谢谢你喜欢了我！我们一起聊聊</font>";
        if (memberInfo.getServiceTags().length() > 0) {
            String[] tags = memberInfo.getServiceTags().substring(1, memberInfo.getServiceTags().length() - 1).split("#");
            for (int i = 0; i < Math.min(tags.length, 2); i++) {
                if (i < Math.min(tags.length, 2) - 1)
                    htmlStr = htmlStr + "<font color='" + colors[i % 2] + "'>" + tags[i] + "</font>" + "<font color='#C3BDCD'>、</font>";
                else
                    htmlStr = htmlStr + "<font color='" + colors[i % 2] + "'>" + tags[i] + "</font>";
            }
        }
        htmlStr = htmlStr + "<font color='#C3BDCD'>吧。</font>";

        memberInfo.setContent(Html.fromHtml(htmlStr));
        HistoryMsg historyMsg = new HistoryMsg();
        historyMsg.setFromId(memberInfo.getUserId());
        historyMsg.setToId(BaseActivity.userId);
        historyMsg.setMainUserId(BaseActivity.userId);
        historyMsg.setCreationDate(DateUtil.getNow(DateUtil.yyyy_MM_dd_HH_mm_ss));
        historyMsg.setMsgType(1000);
        historyMsg.setShowTime(historyMsgList.size() == 0);
        historyMsgList.add(0, historyMsg);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void closeNotice(View view) {
        mBinding.noticeLayout.setVisibility(View.GONE);
        PreferenceUtil.saveIntValue(activity, "chat_notice_" + BaseActivity.userId, 1);
    }

    @Override
    public void openNotice(View view) {
        OpenNotice.gotoSet(activity);
        mBinding.noticeLayout.setVisibility(View.GONE);
        PreferenceUtil.saveIntValue(activity, "chat_notice_" + BaseActivity.userId, 1);
    }

    @Override
    public void toVoiceKeyboard(View view) {
        hintKeyBoard();
        mBinding.setIsEmoji(false);
        if (!mBinding.getIsVoice()) {
            pre = new String[]{Manifest.permission.RECORD_AUDIO};
            if (checkPermissionGranted(activity, pre)) {
                setPermissions(2);
            } else {
                if (PreferenceUtil.readIntValue(activity, "audioPermission") == 0) {
                    PreferenceUtil.saveIntValue(activity, "audioPermission", 1);
                    new TextPW(activity, mBinding.getRoot(), "权限说明",
                            "聊天室发送语音信息时需要使用录音功能，我们将会申请麦克风权限：" +
                                    "\n 1、申请麦克风权限--聊天时获取录制音频功能，" +
                                    "\n 2、若您点击“同意”按钮，我们方可正式申请上述权限，以便开启录音功能，发送语音信息，" +
                                    "\n 3、若您点击“拒绝”按钮，我们将不再主动弹出该提示，您也无法使用语音聊天功能，不影响使用其他的虾菇功能/服务，" +
                                    "\n 4、您也可以通过“手机设置--应用--虾菇--权限”或app内“我的--设置--权限管理--权限”，手动开启或关闭麦克风权限。",
                            "同意", false, true, () -> getPermissions(2));
                } else
                    SCToastUtil.showToast(activity, "你未开启麦克风权限，请前往我的--设置--权限管理--权限进行设置", true);
            }
        } else {
            mBinding.setIsVoice(false);
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
        pre = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};
        if (checkPermissionGranted(activity, pre)) {
            setPermissions(1);
        } else {
            if (PreferenceUtil.readIntValue(activity, "publishPermission") == 0) {
                PreferenceUtil.saveIntValue(activity, "publishPermission", 1);
                new TextPW(activity, mBinding.getRoot(), "权限说明",
                        "在使用聊天功能发送图片、视频时，我们将会申请相机、存储、麦克风权限：" +
                                "\n 1、申请相机权限--聊天时获取拍摄照片，录制视频功能，" +
                                "\n 2、申请存储权限--聊天时获取保存和读取图片、视频，" +
                                "\n 3、申请麦克风权限--聊天时获取录制视频音频功能，" +
                                "\n 4、若您点击“同意”按钮，我们方可正式申请上述权限，以便发送图片、视频等聊天内容，" +
                                "\n 5、若您点击“拒绝”按钮，我们将不再主动弹出该提示，您也无法发送图片、视频等聊天内容，不影响使用其他的虾菇功能/服务，" +
                                "\n 6、您也可以通过“手机设置--应用--虾菇--权限”或app内“我的--设置--权限管理--权限”，手动开启或关闭相机、存储、麦克风权限。",
                        "同意", false, true, () -> getPermissions(1));
            } else {
                if (!checkPermissionGranted(activity, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    SCToastUtil.showToast(activity, "你未开启存储权限，请前往我的--设置--权限管理--权限进行设置", true);
                } else if (!checkPermissionGranted(activity, Manifest.permission.CAMERA)) {
                    SCToastUtil.showToast(activity, "你未开启相机权限，请前往我的--设置--权限管理--权限进行设置", true);
                } else if (!checkPermissionGranted(activity, Manifest.permission.RECORD_AUDIO)) {
                    SCToastUtil.showToast(activity, "你未开启麦克风权限，请前往我的--设置--权限管理--权限进行设置", true);
                }
            }
        }
    }

    @Override
    public void toEmoji(View view) {
        if (mBinding.getIsEmoji()) {
            mBinding.setIsEmoji(false);
        } else {
            mBinding.setIsVoice(false);
            hintKeyBoard();
            MineApp.getApp().getFixedThreadPool().execute(() -> {
                SystemClock.sleep(300);
                activity.runOnUiThread(() -> {
                    mBinding.edContent.setFocusable(true);
                    mBinding.edContent.setFocusableInTouchMode(true);
                    mBinding.edContent.requestFocus();
                    mBinding.edContent.findFocus();
                    mBinding.setIsEmoji(true);
                });
            });
        }
    }

    @Override
    public void sendMsg(View view) {
        if (msgChannelType == 2) {
            if (bottleInfo == null)
                return;
            if (bottleInfo.getDestroyType() != 0) {
                SCToastUtil.showToast(activity, "该漂流瓶已被对方销毁", true);
                return;
            }
        }

        if (mBinding.getContent().trim().isEmpty()) {
            SCToastUtil.showToast(activity, "请输入回复内容", true);
            return;
        }
        closeImplicit(mBinding.edContent);
        UserManager.getInstance().sendMsg(
                miUserId, UserManager.TEXT, 1, mBinding.getContent(), "",
                0, "【文字】", driftBottleId, flashTalkId, msgChannelType
        );
        mBinding.setContent("");

    }

    @Override
    public void deleteContent(View view) {
        mBinding.edContent.onKeyDown(KeyEvent.KEYCODE_DEL, new KeyEvent(R.id.ed_content, KeyEvent.ACTION_DOWN));
    }

    @Override
    public void uploadSound(File file, int resTime) {
        uploadSoundApi api = new uploadSoundApi(new HttpOnNextListener<ResourceUrl>() {
            @Override
            public void onNext(ResourceUrl o) {
                UserManager.getInstance().sendMsg(
                        miUserId, UserManager.PIC_FILE, 3, "", o.getUrl(),
                        resTime, "【语音】", driftBottleId, flashTalkId, msgChannelType
                );
                soundView.setResTime(0);
            }
        }, activity).setFile(file);
        HttpChatUploadManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void stopPlayer() {
        soundView.stopPlayer();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initTool() {

        SoftHideKeyBoardUtil.assistActivity(activity, true);
        EmojiUtil.setProhibitEmoji(mBinding.edContent);
        KeyboardStateObserver.getKeyboardStateObserver(activity).
                setKeyboardVisibilityListener(height -> {
                    mBinding.setIsVoice(false);
                    mBinding.setIsEmoji(false);
                    PreferenceUtil.saveIntValue(activity, "keyboardHeight", height);
                    AdapterBinding.viewSize(mBinding.emojiList, MineApp.W, height);
                }, false);

        AdapterBinding.viewSize(mBinding.emojiList, MineApp.W,
                PreferenceUtil.readIntValue(activity, "keyboardHeight") == 0 ? MineApp.H / 3 : PreferenceUtil.readIntValue(activity, "keyboardHeight"));

        mBinding.chatList.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_MOVE && isSoftShowing() && isFirst) {
                isFirst = false;
                hintKeyBoard();
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                isFirst = true;
            }
            return false;
        });
        if (OpenNotice.isNotNotification(activity))
            mBinding.noticeLayout.setVisibility(PreferenceUtil.readIntValue(activity, "chat_notice_" + BaseActivity.userId) == 1 ? View.GONE : View.VISIBLE);

        photoManager = new PhotoManager(activity, () -> {
            UserManager.getInstance().sendMsg(
                    miUserId, UserManager.PIC_FILE, 2, "", photoManager.jointWebUrl(","),
                    0, "【图片】", driftBottleId, flashTalkId, msgChannelType
            );
            photoManager.deleteAllFile();
        });
        photoManager.setChat(true);

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
                    mBinding.tvSpeak.setText("松开结束");
                    soundView.start();
                    stopPlayer();
                    stopVoiceDrawable();
                    return true;
                case MotionEvent.ACTION_UP:
                    mBinding.audioBtn.setVisibility(View.GONE);
                    soundView.stop();
                    mBinding.tvSpeak.setText("按住说话");
                    break;
            }
            return false;
        });
    }

    @Override
    public void myInfo() {
        myInfoApi api = new myInfoApi(new HttpOnNextListener<MineInfo>() {
            @Override
            public void onNext(MineInfo o) {
                MineApp.mineInfo = o;
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void initReceiver() {
        // 开通会员
        openVipReceiver = new BaseReceiver(activity, "lobster_openVip") {
            @Override
            public void onReceive(Context context, Intent intent) {
                myInfo();
            }
        };
        cameraReceiver = new BaseReceiver(activity, "lobster_camera") {
            @Override
            public void onReceive(Context context, Intent intent) {
                MineApp.isChat = false;
                int cameraType = intent.getIntExtra("cameraType", 0);
                if (cameraType == 0 || cameraType == 2)
                    uploadImage(intent.getStringExtra("filePath"));
                else if (cameraType == 1) {
                    uploadVideo(intent.getStringExtra("filePath"), intent.getLongExtra("time", 0));
                }
            }
        };
        if (msgChannelType == 1)
            chatReceiver = new BaseReceiver(activity, "lobster_upMessage/friend=" + otherUserId) {
                @Override
                public void onReceive(Context context, Intent intent) {
                    CustomMessageBody body = (CustomMessageBody) intent.getSerializableExtra("customMessageBody");
                    String msgId = intent.getStringExtra("msgId");
                    HistoryMsg historyMsg = HistoryMsg.createHistory(msgId, body, otherUserId, 1, 0);

                    ChatListDb.getInstance().updateMember(otherUserId, memberInfo == null ? "" : memberInfo.getImage(), memberInfo == null ? "" : memberInfo.getNick(), otherUserId == BaseActivity.dynUserId ? 5 : 4,
                            new ChatListDb.CallBack() {
                                @Override
                                public void success() {
                                    MineApp.getApp().getFixedThreadPool().execute(() -> {
                                        SystemClock.sleep(500);
                                        Intent data = new Intent("lobster_updateChat");
                                        data.putExtra("userId", otherUserId);
                                        LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(data);
                                    });
                                }

                                @Override
                                public void fail() {
                                    if (memberInfo == null) {
                                        getOtherInfo(historyMsg, 4);
                                    } else {
                                        setChatList(historyMsg, 4, false);
                                    }
                                }
                            });
                    setChatList(historyMsg, 4, true);
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
                }
            };
        else if (msgChannelType == 2)
            bottleChatReceiver = new BaseReceiver(activity, "lobster_upMessage/driftBottleId=" + driftBottleId) {
                @Override
                public void onReceive(Context context, Intent intent) {
                    CustomMessageBody body = (CustomMessageBody) intent.getSerializableExtra("customMessageBody");
                    String msgId = intent.getStringExtra("msgId");
                    HistoryMsg historyMsg = HistoryMsg.createHistory(msgId, body, otherUserId, 2, driftBottleId);

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

                    BottleCacheDb.getInstance().updateBottleCache(driftBottleId, memberInfo == null ? "" : memberInfo.getImage(), memberInfo == null ? "" : memberInfo.getNick(),
                            new BottleCacheDb.CallBack() {
                                @Override
                                public void success() {
                                    MineApp.getApp().getFixedThreadPool().execute(() -> {
                                        SystemClock.sleep(500);
                                        Intent data = new Intent("lobster_singleBottleCache");
                                        data.putExtra("driftBottleId", body.getDriftBottleId());
                                        LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(data);
                                    });
                                }

                                @Override
                                public void fail() {
                                    if (memberInfo == null) {
                                        getOtherInfo(historyMsg, 2);
                                    } else {
                                        setBottleChatList(historyMsg.getCreationDate(), historyMsg.getStanza(), historyMsg.getMsgType());
                                    }
                                }
                            });
                }
            };
        else if (msgChannelType == 3)
            flashChatReceiver = new BaseReceiver(activity, "lobster_upMessage/flashTalkId=" + flashTalkId) {
                @Override
                public void onReceive(Context context, Intent intent) {
                    CustomMessageBody body = (CustomMessageBody) intent.getSerializableExtra("customMessageBody");
                    String msgId = intent.getStringExtra("msgId");

                    HistoryMsg historyMsg = HistoryMsg.createHistoryForFlash(msgId, body, otherUserId, msgChannelType, flashTalkId);
                    otherChatCount++;
                    isLockImage = (myChatCount + otherChatCount) < 20;
                    mBinding.setIsLockImage(isLockImage);
                    boolean updateAll = (myChatCount + otherChatCount) == 20;
                    ChatListDb.getInstance().updateMemberForFlash(otherUserId, memberInfo == null ? "" : memberInfo.getImage(), memberInfo == null ? "" : memberInfo.getNick(), flashTalkId, myChatCount, otherChatCount, new ChatListDb.CallBack() {
                        @Override
                        public void success() {
                            MineApp.getApp().getFixedThreadPool().execute(() -> {
                                SystemClock.sleep(500);
                                Intent data = new Intent("lobster_updateChat");
                                data.putExtra("userId", otherUserId);
                                LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(data);
                            });
                        }

                        @Override
                        public void fail() {
                            if (memberInfo == null) {
                                getOtherInfo(historyMsg, 6);
                            } else {
                                setChatList(historyMsg, 6, false);
                            }
                        }
                    });

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
                    }
                    updateTime();
                    if (updateAll) {
                        adapter.notifyDataSetChanged();
                    } else {
                        adapter.notifyItemChanged(adapter.getItemCount() - 1);
                    }
                    mBinding.chatList.scrollToPosition(adapter.getItemCount() - 1);
                }
            };
    }

    private void getOtherInfo(HistoryMsg historyMsg, int chatType) {
        otherInfoApi api = new otherInfoApi(new HttpOnNextListener<MemberInfo>() {
            @Override
            public void onNext(MemberInfo o) {
                memberInfo = o;
                if (chatType == 2)
                    setBottleChatList(historyMsg.getCreationDate(), historyMsg.getStanza(), historyMsg.getMsgType());
                else
                    setChatList(historyMsg, chatType, true);
            }
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void setChatList(HistoryMsg historyMsg, int chatType, boolean updateImage) {
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
        chatList.setChatType(chatType);
        chatList.setFlashTalkId(flashTalkId);
        chatList.setMyChatCount(myChatCount);
        chatList.setOtherChatCount(otherChatCount);
        chatList.setMainUserId(BaseActivity.userId);
        ChatListDb.getInstance().saveChatList(chatList);

        // 更新会话列表
        MineApp.getApp().getFixedThreadPool().execute(() -> {
            SystemClock.sleep(300);
            Intent data = new Intent("lobster_updateChat");
            data.putExtra("userId", otherUserId);
            data.putExtra("flashTalkId", flashTalkId);
            data.putExtra("updateImage", updateImage);
            LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(data);

            LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_unReadCount"));
        });
    }

    private void setBottleChatList(String creationDate, String stanza, int msgType) {
        BottleCache bottleCache = new BottleCache();
        bottleCache.setDriftBottleId(driftBottleId);
        bottleCache.setUserId(otherUserId);
        bottleCache.setNick(memberInfo == null ? "" : memberInfo.getNick());
        bottleCache.setImage(memberInfo == null ? "" : memberInfo.getImage());
        bottleCache.setCreationDate(creationDate);
        bottleCache.setStanza(stanza);
        bottleCache.setMsgType(msgType);
        bottleCache.setNoReadNum(0);
        bottleCache.setPublicTag("");
        bottleCache.setEffectType(1);
        bottleCache.setAuthType(1);
        bottleCache.setMainUserId(BaseActivity.userId);
        BottleCacheDb.getInstance().saveBottleCache(bottleCache);

        // 更新会话列表
        Intent data = new Intent("lobster_singleBottleCache");
        data.putExtra("driftBottleId", driftBottleId);
        LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(data);
    }

    /**
     * 上传图片
     *
     * @param fileName
     */
    public void uploadImage(String fileName) {
        photoManager.addFileUpload(0, new File(fileName));
    }

    public void uploadVideo(String fileName, long time) {
        uploadVideoApi api = new uploadVideoApi(new HttpOnNextListener<ResourceUrl>() {
            @Override
            public void onNext(ResourceUrl o) {
                UserManager.getInstance().sendMsg(
                        miUserId, UserManager.PIC_FILE, 4, "", o.getUrl(),
                        (int) (time / 1000), "【视频】", driftBottleId, flashTalkId, msgChannelType
                );
            }
        }, activity).setFile(new File(fileName));
        HttpChatUploadManager.getInstance().doHttpDeal(api);
    }

    public void toUrl(String text) {
        List<String> url = ObjectUtils.judgeString(text);
        if (url.size() > 0) {
            if (url.size() == 1) {
                String url0 = url.get(0);
                if (!url0.contains("http")) {
                    url0 = "https://" + url0;
                }
                ActivityUtils.getMineWeb("", url0);
            } else {
                new SelectorPW(mBinding.getRoot(), url, position1 -> {
                    String urlTemp = url.get(position1);
                    if (!urlTemp.contains("http")) {
                        urlTemp = "https://" + urlTemp;
                    }
                    ActivityUtils.getMineWeb("", urlTemp);
                });
            }
        }
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

            for (int i = historyMsgList.size()-2; i >=0; i--) {

            }
        }
    }

    /**
     * 权限
     */
    private void getPermissions(int type) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (type == 1)
                performCodeWithPermission("虾菇需要访问存储权限、相机权限及麦克风权限", new BaseActivity.PermissionCallback() {
                    @Override
                    public void hasPermission() {
                        setPermissions(type);
                    }

                    @Override
                    public void noPermission() {
                        PreferenceUtil.saveIntValue(activity, "publishPermission", 1);
                    }
                }, pre);
            else
                performCodeWithPermission("虾菇需要访问麦克风权限", new BaseActivity.PermissionCallback() {
                    @Override
                    public void hasPermission() {
                        setPermissions(type);
                    }

                    @Override
                    public void noPermission() {
                        PreferenceUtil.saveIntValue(activity, "audioPermission", 1);
                    }
                }, pre);
        } else {
            setPermissions(type);
        }
    }

    private void setPermissions(int type) {
        if (type == 1) {
            MineApp.isChat = true;
            MineApp.toPublish = false;
            ActivityUtils.getCameraMain(activity, false, true, true);
        } else if (type == 2) {
            mBinding.setIsVoice(true);
        }
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
     * 清空用户漂流瓶消息
     */
    private void readOverDriftBottleHistoryMsg() {
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

    private void flashReadOverHistoryMsg() {
        flashReadOverHistoryMsgApi api = new flashReadOverHistoryMsgApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {

            }
        }, activity).setOtherUserId(otherUserId).setFlashTalkId(flashTalkId).setMessageId(historyMsgId);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
