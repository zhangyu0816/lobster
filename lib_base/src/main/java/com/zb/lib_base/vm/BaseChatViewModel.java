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
import android.os.Handler;
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
import com.zb.lib_base.http.HttpChatUploadManager;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.imcore.CustomMessageBody;
import com.zb.lib_base.imcore.ImUtils;
import com.zb.lib_base.iv.BaseChatVMInterface;
import com.zb.lib_base.model.BottleCache;
import com.zb.lib_base.model.BottleInfo;
import com.zb.lib_base.model.ChatList;
import com.zb.lib_base.model.DiscoverInfo;
import com.zb.lib_base.model.HistoryMsg;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.model.PrivateMsg;
import com.zb.lib_base.model.ResourceUrl;
import com.zb.lib_base.model.StanzaInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DataCleanManager;
import com.zb.lib_base.utils.DateUtil;
import com.zb.lib_base.utils.DownLoad;
import com.zb.lib_base.utils.KeyboardStateObserver;
import com.zb.lib_base.utils.MNImage;
import com.zb.lib_base.utils.OpenNotice;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.utils.SoftHideKeyBoardUtil;
import com.zb.lib_base.utils.glide.BlurTransformation;
import com.zb.lib_base.utils.uploadImage.PhotoManager;
import com.zb.lib_base.views.SoundView;
import com.zb.lib_base.windows.BottleVipPW;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
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

                ImUtils.getInstance().sendChatMessage(activity, 1, mBinding.getContent(), "", 0, "【文字】", driftBottleId, flashTalkId, msgChannelType);
                mBinding.setContent("");
                closeImplicit(mBinding.edContent);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onResume() {
        if (ImUtils.getInstance().getOtherUserId() > 0)
            ImUtils.getInstance().setChat(true, activity);
    }

    @Override
    public void back(View view) {
        super.back(view);
        hintKeyBoard();
        DataCleanManager.deleteFile(new File(activity.getCacheDir(), "videos"));
        DataCleanManager.deleteFile(new File(activity.getCacheDir(), "images"));
        ImUtils.getInstance().markRead();
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
        Collections.reverse(tempList);
        updateTime();
        historyMsgList.addAll(0, tempList);
        adapter.notifyItemRangeChanged(0, tempList.size());
        updateAll = tempList.size() == 0;
        mBinding.refresh.finishRefresh();
    }

    @Override
    public void setAdapter() {
        realmResults = HistoryMsgDb.getInstance().getRealmResults(otherUserId, msgChannelType, driftBottleId, flashTalkId);
        historyMsgList.addAll(HistoryMsgDb.getInstance().getLimitList(realmResults, pagerNo * pageSize, pageSize));
        if (msgChannelType == 1) {
            isLockImage = false;
        } else if (msgChannelType == 2) {
            isLockImage = MineApp.mineInfo.getMemberType() == 1;
        } else if (msgChannelType == 3) {
            myChatCount = Math.min(historyMsgList.size() == 0 ? 0 : historyMsgList.get(0).getMyChatCount(), 10);
            otherChatCount = Math.min(historyMsgList.size() == 0 ? 0 : historyMsgList.get(0).getOtherChatCount(), 10);
            isLockImage = (myChatCount + otherChatCount) != 20;
        }
        mBinding.setIsLockImage(isLockImage);

        Collections.reverse(historyMsgList);
        updateTime();
        adapter = new BaseAdapter<>(activity, R.layout.item_chat, historyMsgList, this);
        mBinding.chatList.scrollToPosition(adapter.getItemCount() - 1);
        mBinding.refresh.setEnableLoadMore(false);
        for (int i = 1; i < EmojiHandler.maxEmojiCount; i++) {
            emojiList.add(EmojiHandler.sCustomizeEmojisMap.get(i));
        }
        emojiAdapter = new BaseAdapter<>(activity, R.layout.item_emoji, emojiList, this);
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
            new BottleVipPW(mBinding.getRoot());
        } else if (msgChannelType == 3) {
            if (!isLockImage)
                ActivityUtils.getCardMemberDetail(otherUserId, false);
            else
                SCToastUtil.showToast(activity,"双方各发10句可以解锁头像哦",true);
        }
    }

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
                ImUtils.getInstance().setOtherUserId(otherUserId);
                ImUtils.getInstance().setChat(true, activity);

                if (msgChannelType == 1 || msgChannelType == 3) {
                    RequestOptions cropOptions = new RequestOptions();
                    MultiTransformation<Bitmap> multiTransformation = new MultiTransformation<>(new BlurTransformation());
                    cropOptions.transform(multiTransformation);
                    Glide.with(activity).asBitmap().load(memberInfo.getSingleImage()).apply(cropOptions).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            mBinding.ivBg.setImageBitmap(resource);
                        }
                    });
                    new Thread(() -> {
                        if (msgChannelType == 1)
                            historyMsgList(1);
                        else
                            flashHistoryMsgList(1);
                    }).start();
                } else if (msgChannelType == 2) {
                    new Thread(() -> bottleHistoryMsgList(1)).start();
                }
            }
        }, activity).setOtherUserId(otherUserId);
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
                if (historyMsgId == 0)
                    historyMsgId = o.get(0).getId();
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
                        chatList.setMainUserId(BaseActivity.userId);
                        ChatListDb.getInstance().saveChatList(chatList);
                        Intent data = new Intent("lobster_updateChat");
                        data.putExtra("userId", otherUserId);
                        data.putExtra("updateImage", true);
                        activity.sendBroadcast(data);
                        activity.sendBroadcast(new Intent("lobster_unReadCount"));
                    } else {
                        ChatListDb.getInstance().updateMember(otherUserId, memberInfo.getImage(), memberInfo.getNick(), otherUserId == BaseActivity.dynUserId ? 5 : 4, new ChatListDb.CallBack() {
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

    @Override
    public void flashHistoryMsgList(int pageNo) {
        flashHistoryMsgListApi api = new flashHistoryMsgListApi(new HttpOnNextListener<List<PrivateMsg>>() {
            @Override
            public void onNext(List<PrivateMsg> o) {
                for (PrivateMsg privateMsg : o) {
                    HistoryMsgDb.getInstance().saveHistoryMsg(HistoryMsg.createHistoryForPrivate(privateMsg, otherUserId));
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
                        chatList.setChatType(6);
                        chatList.setFlashTalkId(flashTalkId);
                        chatList.setMyChatCount(myChatCount);
                        chatList.setOtherChatCount(otherChatCount);
                        chatList.setMainUserId(BaseActivity.userId);
                        ChatListDb.getInstance().saveChatList(chatList);
                        Intent data = new Intent("lobster_updateChat");
                        data.putExtra("userId", otherUserId);
                        data.putExtra("updateImage", true);
                        data.putExtra("flashTalkId", flashTalkId);
                        activity.sendBroadcast(data);
                        activity.sendBroadcast(new Intent("lobster_unReadCount"));
                    } else {
                        ChatListDb.getInstance().updateMember(otherUserId, memberInfo.getImage(), memberInfo.getNick(), 6, new ChatListDb.CallBack() {
                            @Override
                            public void success() {
                                Intent data = new Intent("lobster_updateChat");
                                data.putExtra("userId", otherUserId);
                                data.putExtra("updateImage", true);
                                data.putExtra("flashTalkId", flashTalkId);
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
                                chatList.setStanza("");
                                chatList.setMsgType(1);
                                chatList.setNoReadNum(0);
                                chatList.setPublicTag("");
                                chatList.setEffectType(1);
                                chatList.setAuthType(1);
                                chatList.setChatType(6);
                                chatList.setFlashTalkId(flashTalkId);
                                chatList.setMyChatCount(myChatCount);
                                chatList.setOtherChatCount(otherChatCount);
                                chatList.setMainUserId(BaseActivity.userId);
                                ChatListDb.getInstance().saveChatList(chatList);
                                Intent data = new Intent("lobster_updateChat");
                                data.putExtra("userId", otherUserId);
                                data.putExtra("updateImage", false);
                                data.putExtra("flashTalkId", flashTalkId);
                                activity.sendBroadcast(data);
                                setMemberUI();
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
        historyMsg.setShowTime(true);
        historyMsgList.add(historyMsg);
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

        ImUtils.getInstance().sendChatMessage(activity, 1, mBinding.getContent(), "", 0, "【文字】", driftBottleId, flashTalkId, msgChannelType);
        mBinding.setContent("");
        closeImplicit(mBinding.edContent);
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
                ImUtils.getInstance().sendChatMessage(activity, 3, "", o.getUrl(), resTime, "【语音】", driftBottleId, flashTalkId, msgChannelType);
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
        ImUtils.getInstance().setCallBackForMsg(this::updateMySend);

        SoftHideKeyBoardUtil.assistActivity(activity, true);
        setProhibitEmoji(mBinding.edContent);
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
            ImUtils.getInstance().sendChatMessage(activity, 2, "", photoManager.jointWebUrl(","), 0, "【图片】", driftBottleId, flashTalkId, msgChannelType);
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
                    getPermissions(3);
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

    private void initReceiver() {
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
//                    ChatListDb.getInstance().updateMember(otherUserId, memberInfo == null ? "" : memberInfo.getImage(), memberInfo == null ? "" : memberInfo.getNick(), otherUserId == BaseActivity.dynUserId ? 5 : 4,
//                            () -> new Handler().postDelayed(() -> {
//                                Intent data = new Intent("lobster_updateChat");
//                                data.putExtra("userId", otherUserId);
//                                activity.sendBroadcast(data);
//                            }, 500));
                }
            };
        else if (msgChannelType == 2)
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

//                    BottleCacheDb.getInstance().updateBottleCache(driftBottleId, memberInfo == null ? "" : memberInfo.getImage(), memberInfo == null ? "" : memberInfo.getNick(), () -> new Handler().postDelayed(() -> {
//                        // 更新会话列表
//                        Intent data = new Intent("lobster_singleBottleCache");
//                        data.putExtra("driftBottleId", body.getDriftBottleId());
//                        activity.sendBroadcast(data);
//                    }, 500));
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
                    historyMsg.setOtherChatCount(otherChatCount);
                    HistoryMsgDb.getInstance().saveHistoryMsg(historyMsg);
                    isLockImage = (myChatCount + otherChatCount) != 20;
                    mBinding.setIsLockImage(isLockImage);
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
                    adapter.notifyItemChanged(adapter.getItemCount() - 1);
                    mBinding.chatList.scrollToPosition(adapter.getItemCount() - 1);

//                    ChatListDb.getInstance().updateMember(otherUserId, memberInfo == null ? "" : memberInfo.getImage(), memberInfo == null ? "" : memberInfo.getNick(), 6,
//                            () -> new Handler().postDelayed(() -> {
//                                Intent data = new Intent("lobster_updateChat");
//                                data.putExtra("userId", otherUserId);
//                                data.putExtra("flashTalkId", flashTalkId);
//                                activity.sendBroadcast(data);
//                            }, 500));
                }
            };

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
                ImUtils.getInstance().sendChatMessage(activity, 4, "", o.getUrl(), (int) (time / 1000), "【视频】", driftBottleId, flashTalkId, msgChannelType);
            }
        }, activity).setFile(new File(fileName));
        HttpChatUploadManager.getInstance().doHttpDeal(api);
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
            MineApp.isChat = true;
            MineApp.toPublish = false;
            ActivityUtils.getCameraMain(activity, false, true, true);
        } else if (type == 3) {
            soundView.start();
            stopPlayer();
            stopVoiceDrawable();
        }
    }

    private void updateMySend(String msgId, CustomMessageBody body) {
        // 记录我们发出去的消息
        body.setFromId(BaseActivity.userId);
        body.setToId(otherUserId);
        HistoryMsg historyMsg;

        if (msgChannelType == 3) {
            historyMsg = HistoryMsg.createHistoryForFlash(msgId, body, otherUserId, msgChannelType, flashTalkId);
            myChatCount++;
            historyMsg.setMyChatCount(myChatCount);
            isLockImage = (myChatCount + otherChatCount) != 20;
            mBinding.setIsLockImage(isLockImage);
        }else{
            historyMsg = HistoryMsg.createHistory(msgId, body, otherUserId, msgChannelType, driftBottleId);
        }
        HistoryMsgDb.getInstance().saveHistoryMsg(historyMsg);
        historyMsgList.add(historyMsg);
        updateTime();
        adapter.notifyItemChanged(adapter.getItemCount() - 1);
        mBinding.chatList.scrollToPosition(adapter.getItemCount() - 1);

        if (msgChannelType == 1) {
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
            chatList.setMainUserId(BaseActivity.userId);
            ChatListDb.getInstance().saveChatList(chatList);

            // 更新会话列表
            Intent data = new Intent("lobster_updateChat");
            data.putExtra("userId", otherUserId);
            activity.sendBroadcast(data);
        } else if (msgChannelType == 2) {
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
        } else if (msgChannelType == 3) {
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
            chatList.setChatType(6);
            chatList.setFlashTalkId(flashTalkId);
            chatList.setMyChatCount(myChatCount);
            chatList.setOtherChatCount(otherChatCount);
            chatList.setMainUserId(BaseActivity.userId);
            ChatListDb.getInstance().saveChatList(chatList);

            // 更新会话列表
            Intent data = new Intent("lobster_updateChat");
            data.putExtra("userId", otherUserId);
            data.putExtra("flashTalkId", flashTalkId);
            activity.sendBroadcast(data);
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
