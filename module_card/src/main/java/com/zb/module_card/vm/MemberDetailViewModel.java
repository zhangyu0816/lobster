package com.zb.module_card.vm;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.api.attentionOtherApi;
import com.zb.lib_base.api.attentionStatusApi;
import com.zb.lib_base.api.cancelAttentionApi;
import com.zb.lib_base.api.contactNumApi;
import com.zb.lib_base.api.makeEvaluateApi;
import com.zb.lib_base.api.memberInfoConfApi;
import com.zb.lib_base.api.otherInfoApi;
import com.zb.lib_base.api.otherRentInfoApi;
import com.zb.lib_base.api.otherUserInfoVisitApi;
import com.zb.lib_base.api.personOtherDynApi;
import com.zb.lib_base.api.seeUserGiftRewardsApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.AreaDb;
import com.zb.lib_base.db.AttentionDb;
import com.zb.lib_base.db.LikeDb;
import com.zb.lib_base.db.LikeTypeDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.iv.SuperLikeInterface;
import com.zb.lib_base.model.Ads;
import com.zb.lib_base.model.AttentionInfo;
import com.zb.lib_base.model.CollectID;
import com.zb.lib_base.model.ContactNum;
import com.zb.lib_base.model.DiscoverInfo;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.model.PairInfo;
import com.zb.lib_base.model.RentInfo;
import com.zb.lib_base.model.Reward;
import com.zb.lib_base.model.ShareInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DateUtil;
import com.zb.lib_base.utils.MNImage;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.views.xbanner.XUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.FunctionPW;
import com.zb.lib_base.windows.GiftPW;
import com.zb.lib_base.windows.GiftPayPW;
import com.zb.lib_base.windows.SuperLikePW;
import com.zb.lib_base.windows.VipAdPW;
import com.zb.module_card.BR;
import com.zb.module_card.R;
import com.zb.module_card.adapter.CardAdapter;
import com.zb.module_card.databinding.CardMemberDetailBinding;
import com.zb.module_card.iv.MemberDetailVMInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.databinding.ViewDataBinding;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MemberDetailViewModel extends BaseViewModel implements MemberDetailVMInterface, SuperLikeInterface {
    private CardMemberDetailBinding mBinding;
    public long otherUserId = 0;
    public boolean showLike;
    public MemberInfo memberInfo;
    public CardAdapter tagAdapter;
    public CardAdapter discoverAdapter;
    public CardAdapter imageAdapter;
    private List<String> tagList = new ArrayList<>();
    private List<DiscoverInfo> discoverInfoList = new ArrayList<>();
    private List<String> imageList = new ArrayList<>();
    private BaseReceiver attentionReceiver;
    private int bannerWidth = MineApp.W;
    private int bannerHeight = MineApp.W;
    private List<Ads> adsList = new ArrayList<>();

    public CardAdapter rewardAdapter;
    private List<Reward> rewardList = new ArrayList<>();

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (CardMemberDetailBinding) binding;
        mBinding.setConstellation("-");
        mBinding.setDistrict("-");
        mBinding.setCityName("-");
        mBinding.setDistant("0.0");
        mBinding.setJob("??????");
        mBinding.setHeight("??????");
        mBinding.setIsAttention(false);
        mBinding.setLikeType(LikeTypeDb.getInstance().getType(otherUserId));
        mBinding.setIsPlay(true);
        attentionReceiver = new BaseReceiver(activity, "lobster_attention") {
            @Override
            public void onReceive(Context context, Intent intent) {
                mBinding.setIsAttention(intent.getBooleanExtra("isAttention", false));
            }
        };

        setAdapter();
        otherInfo();
    }

    @Override
    public void back(View view) {
        super.back(view);
        mBinding.setIsPlay(false);
        activity.finish();
    }

    @Override
    public void more(View view) {
        super.more(view);
        memberInfoConf();
    }

    @Override
    public void follow(View view) {
        super.follow(view);
        if (!mBinding.getIsAttention()) {
            attentionOther();
        } else {
            cancelAttention();
        }
    }

    @Override
    public void setAdapter() {
        tagAdapter = new CardAdapter<>(activity, R.layout.item_card_tag, tagList, this);

        discoverAdapter = new CardAdapter<>(activity, R.layout.item_card_discover_image, discoverInfoList, this);

        imageAdapter = new CardAdapter<>(activity, R.layout.item_member_image, imageList, this);

        // ??????
        rewardAdapter = new CardAdapter<>(activity, R.layout.item_card_reward, rewardList, this);

        otherUserInfoVisit();
        giveOrReceiveForUserList(1);
    }

    private void otherUserInfoVisit() {
        otherUserInfoVisitApi api = new otherUserInfoVisitApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {

            }
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    public void onDestroy() {
        try {
            attentionReceiver.unregisterReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mBinding.banner.releaseBanner();
    }

    private String info = "";
    private String rewardInfo = "";
    private char[] temp;
    private int i = 0;
    private int rewardNum = 0;

    private void giveOrReceiveForUserList(int pageNo) {
        if (pageNo == 1) {
            rewardNum = 0;
        }
        seeUserGiftRewardsApi api = new seeUserGiftRewardsApi(new HttpOnNextListener<List<Reward>>() {
            @Override
            public void onNext(List<Reward> o) {
                rewardNum += o.size();
                if (rewardList.size() == 0)
                    for (int i = 0; i < Math.min(3, o.size()); i++) {
                        rewardList.add(o.get(i));
                    }
                giveOrReceiveForUserList(pageNo + 1);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    mBinding.setRewardNum(rewardNum);
                    if (mBinding.getRewardNum() == 0) {
                        rewardInfo = "???????????????????????????????????????";
                    } else {
                        if (mBinding.getRewardNum() == 1) {
                            rewardInfo = "??????CP?????????";
                        } else {
                            rewardInfo = "????????????";
                        }
                        rewardAdapter.notifyDataSetChanged();
                    }
                    temp = rewardInfo.toCharArray();
                    info = "";
                    i = 0;
                    mBinding.setRewardInfo(info);
                    mHandler.postDelayed(ra, 50);
                }
            }
        }, activity).setOtherUserId(otherUserId)
                .setRewardSortType(2)
                .setPageNo(pageNo)
                .setRow(10);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private Handler mHandler = new Handler();
    private Runnable ra = new Runnable() {
        @Override
        public void run() {
            if (i < temp.length) {
                info += temp[i];
                mBinding.setRewardInfo(info);
                i++;
                mHandler.postDelayed(ra, 50);
            } else {
                mHandler.removeCallbacks(ra);
            }
        }
    };


    @Override
    public void dislike(View view) {
        if (memberInfo == null) {
            SCToastUtil.showToast(activity, "??????????????????????????????????????????", true);
            return;
        }
        if (showLike) {
            Intent data = new Intent("lobster_card");
            data.putExtra("direction", 0);
            LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(data);
        }
        activity.finish();
    }

    @Override
    public void like(View view) {
        if (memberInfo == null) {
            SCToastUtil.showToast(activity, "??????????????????????????????????????????", true);
            return;
        }
        if (showLike) {
            activity.finish();
            Intent data = new Intent("lobster_card");
            data.putExtra("direction", 1);
            LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(data);
        } else {
            isLike(mBinding.ivLike);
            if (PreferenceUtil.readIntValue(activity, "toLikeCount_" + BaseActivity.userId + "_" + DateUtil.getNow(DateUtil.yyyy_MM_dd), -1) == 0 && MineApp.mineInfo.getMemberType() == 1) {
                new VipAdPW(mBinding.getRoot(), 6, "");
                SCToastUtil.showToast(activity, "???????????????????????????", true);
                return;
            }
            makeEvaluate(1);
        }

    }

    @Override
    public void toDiscoverList(View view) {
        ActivityUtils.getCardDiscoverList(otherUserId, mBinding.getIsAttention(), memberInfo);
    }

    @Override
    public void toDiscoverDetail(DiscoverInfo discoverInfo) {
        if (discoverInfo.getVideoUrl().isEmpty())
            ActivityUtils.getHomeDiscoverDetail(discoverInfo.getFriendDynId());
        else
            ActivityUtils.getHomeDiscoverVideo(discoverInfo.getFriendDynId());
    }

    private int preIndex = 0;

    @Override
    public void selectImage(int position) {
        if (preIndex != position) {
            imageAdapter.setSelectImageIndex(position);
            if (preIndex != -1) {
                imageAdapter.notifyItemChanged(preIndex);
            }
            imageAdapter.notifyItemChanged(position);
            preIndex = position;
            mBinding.banner.setCurrentItem(position + 1);
        }
    }

    @Override
    public void openVip(View view) {
        ActivityUtils.getMineOpenVip(true);
    }

    @Override
    public void selectGift(View view) {
        hintKeyBoard();
        if (otherUserId == BaseActivity.userId) {
            ActivityUtils.getHomeRewardList(0, otherUserId);
        } else
            new GiftPW(mBinding.getRoot(), giftInfo ->
                    new GiftPayPW(mBinding.getRoot(), giftInfo, 0, otherUserId, new GiftPayPW.CallBack() {
                        @Override
                        public void paySuccess() {
                            rewardList.clear();
                            rewardAdapter.notifyDataSetChanged();
                            giveOrReceiveForUserList(1);
                        }
                    }));
    }

    @Override
    public void toRewardList(View view) {
        ActivityUtils.getHomeRewardList(0, otherUserId);
    }

    @Override
    public void contactNumDetail(int position) {
        ActivityUtils.getMineFCL(position, otherUserId);
    }

    @Override
    public void otherInfo() {
        otherInfoApi api = new otherInfoApi(new HttpOnNextListener<MemberInfo>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onNext(MemberInfo o) {
                memberInfo = o;
                if (memberInfo.getServiceTags().length() > 0) {
                    String tags = memberInfo.getServiceTags().substring(1, memberInfo.getServiceTags().length() - 1);
                    tagList.addAll(Arrays.asList(tags.split("#")));
                    tagAdapter.notifyDataSetChanged();
                } else {
                    otherRentInfo();
                }
                mBinding.setConstellation(DateUtil.getConstellations(memberInfo.getBirthday()));

                String distant = "-";
                if (!memberInfo.getDistance().isEmpty()) {
                    distant = String.format("%.1f", Float.parseFloat(memberInfo.getDistance()) / 1000f);
                }
                mBinding.setJob(memberInfo.getJob().isEmpty() ? "-" : memberInfo.getJob());
                if (memberInfo.getHeight() > 0) {
                    mBinding.setHeight(memberInfo.getHeight() + "");
                }
                mBinding.setDistant(distant);
                String cityName = AreaDb.getInstance().getCityName(memberInfo.getCityId()).replace("???", "");
                String districtName = AreaDb.getInstance().getDistrictName(memberInfo.getDistrictId());
                mBinding.setCityName(cityName);
                mBinding.setDistrict(cityName + (districtName.isEmpty() ? "" : " ") + districtName);
                if (!memberInfo.getMoreImages().isEmpty()) {
                    String[] images = memberInfo.getMoreImages().split("#");
                    for (String image : images) {
                        if (!image.isEmpty()) {
                            Ads ads = new Ads();
                            ads.setSmallImage(image);
                            adsList.add(ads);
                            imageList.add(ads.getSmallImage());
                        }
                    }
                } else {
                    Ads ads = new Ads();
                    ads.setSmallImage(memberInfo.getSingleImage().isEmpty() ? memberInfo.getImage() : memberInfo.getSingleImage());
                    adsList.add(ads);
                    imageList.add(ads.getSmallImage());
                }
                imageAdapter.notifyDataSetChanged();
                int height = (int) (bannerHeight * 1.2f);
                AdapterBinding.viewSize(mBinding.banner, bannerWidth, height);
                XUtils.showBanner(mBinding.banner, adsList, 5,
                        (context, ads, image, position) -> {
                            AdapterBinding.loadImage(image, ads.getSmallImage(), 0, R.drawable.empty_bg, bannerWidth, height, false,
                                    true, 10, false, 0, false);
                        }
                        ,
                        (position, imageList) ->
                                MNImage.imageBrowser(activity, mBinding.getRoot(), otherUserId, imageList, position, false, null),
                        position -> {
                            if (position <= imageList.size()) {
                                position--;
                                if (preIndex != position) {
                                    imageAdapter.setSelectImageIndex(position);
                                    if (preIndex != -1) {
                                        imageAdapter.notifyItemChanged(preIndex);
                                    }
                                    imageAdapter.notifyItemChanged(position);
                                    preIndex = position;
                                }
                            }

                        });

                attentionStatus();
                personOtherDyn();
                contactNum();
                mBinding.setVariable(BR.viewModel, MemberDetailViewModel.this);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.ERROR) {
                    back(null);
                }
            }
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void otherRentInfo() {
        otherRentInfoApi api = new otherRentInfoApi(new HttpOnNextListener<RentInfo>() {
            @Override
            public void onNext(RentInfo o) {
                String tags = o.getServiceTags().substring(1, o.getServiceTags().length() - 1);
                tagList.addAll(Arrays.asList(tags.split("#")));
                tagAdapter.notifyDataSetChanged();
            }
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void personOtherDyn() {
        personOtherDynApi api = new personOtherDynApi(new HttpOnNextListener<List<DiscoverInfo>>() {
            @Override
            public void onNext(List<DiscoverInfo> o) {
                mBinding.discoverLayout.setVisibility(View.VISIBLE);
                discoverInfoList.addAll(o);
                discoverAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    mBinding.discoverLayout.setVisibility(View.GONE);
                }
            }
        }, activity)
                .setDynType(0)
                .setOtherUserId(otherUserId)
                .setPageNo(1);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void attentionStatus() {
        attentionStatusApi api = new attentionStatusApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                if (o == null) {
                    mBinding.setIsAttention(true);
                    AttentionDb.getInstance().saveAttention(new AttentionInfo(otherUserId, memberInfo.getNick(), memberInfo.getImage(), true, BaseActivity.userId));
                } else {
                    AttentionDb.getInstance().saveAttention(new AttentionInfo(otherUserId, memberInfo.getNick(), memberInfo.getImage(), false, BaseActivity.userId));
                }
            }
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void attentionOther() {
        attentionOtherApi api = new attentionOtherApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                mBinding.setIsAttention(true);
                AttentionDb.getInstance().saveAttention(new AttentionInfo(otherUserId, memberInfo.getNick(), memberInfo.getImage(), true, BaseActivity.userId));
                Intent data = new Intent("lobster_attentionList");
                data.putExtra("isAdd", true);
                LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(data);
                Intent intent = new Intent("lobster_attention");
                intent.putExtra("isAttention", mBinding.getIsAttention());
                LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(intent);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.ERROR) {
                    if (e.getMessage().equals("???????????????")) {
                        mBinding.setIsAttention(true);
                        AttentionDb.getInstance().saveAttention(new AttentionInfo(otherUserId, memberInfo.getNick(), memberInfo.getImage(), true, BaseActivity.userId));
                        Intent data = new Intent("lobster_attentionList");
                        data.putExtra("isAdd", true);
                        LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(data);
                    }
                }
            }
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void cancelAttention() {
        cancelAttentionApi api = new cancelAttentionApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                mBinding.setIsAttention(false);
                AttentionDb.getInstance().saveAttention(new AttentionInfo(otherUserId, memberInfo.getNick(), memberInfo.getImage(), false, BaseActivity.userId));
                LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_attentionList"));
                Intent intent = new Intent("lobster_attention");
                intent.putExtra("isAttention", mBinding.getIsAttention());
                LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(intent);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.ERROR) {
                    if (e.getMessage().equals("?????????????????????")) {
                        mBinding.setIsAttention(false);
                        AttentionDb.getInstance().saveAttention(new AttentionInfo(otherUserId, memberInfo.getNick(), memberInfo.getImage(), false, BaseActivity.userId));
                        LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_attentionList"));
                        Intent intent = new Intent("lobster_attention");
                        intent.putExtra("isAttention", mBinding.getIsAttention());
                        LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(intent);
                    }
                }
            }
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void contactNum() {
        contactNumApi api = new contactNumApi(new HttpOnNextListener<ContactNum>() {
            @Override
            public void onNext(ContactNum o) {
                mBinding.setContactNum(o);
            }
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void makeEvaluate(int likeOtherStatus) {
        //  likeOtherStatus  0 ?????????  1 ??????  2.???????????? ?????????????????????????????????
        makeEvaluateApi api = new makeEvaluateApi(new HttpOnNextListener<Integer>() {
            @Override
            public void onNext(Integer o) {
                String myHead = MineApp.mineInfo.getImage();
                String otherHead = memberInfo.getImage();
                // 1???????????? 2???????????? 3??????????????????
                if (o == 1) {
                    // ???????????????  ????????????  ??????????????????
                    if (likeOtherStatus == 0) {
                        activity.finish();
                    } else if (likeOtherStatus == 1) {
                        LikeDb.getInstance().saveLike(new CollectID(otherUserId));
                        LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_isLike"));
                        LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_updateFCL"));
                        LikeTypeDb.getInstance().setType(otherUserId, 1);
                        closeBtn(mBinding.likeLayout);
                        SCToastUtil.showToast(activity, "???????????????", true);
                    } else if (likeOtherStatus == 2) {
                        if (showLike) {
                            activity.finish();
                            Intent data = new Intent("lobster_card");
                            data.putExtra("direction", 2);
                            LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(data);
                        } else {
                            LikeTypeDb.getInstance().setType(otherUserId, 2);
                            closeBtn(mBinding.likeLayout);
                            LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_updateFCL"));
                            new SuperLikePW(mBinding.getRoot(), myHead, otherHead, MineApp.mineInfo.getSex(), memberInfo.getSex());
                        }
                    }
                } else if (o == 2) {
                    // ????????????
                    LikeDb.getInstance().saveLike(new CollectID(otherUserId));
                    new SuperLikePW(mBinding.getRoot(), myHead, otherHead, MineApp.mineInfo.getSex(), memberInfo.getSex(), memberInfo.getNick(),
                            () -> ActivityUtils.getChatActivity(otherUserId, false));
                    LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_pairList"));
                    LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_isLike"));
                    LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_updateFCL"));
                    closeBtn(mBinding.likeLayout);
                    LikeTypeDb.getInstance().setType(otherUserId, 2);
                } else if (o == 3) {
                    // ??????????????????
                    new VipAdPW(mBinding.getRoot(), 6, "");
                    SCToastUtil.showToast(activity, "???????????????????????????", true);
                } else if (o == 4) {
                    // ??????????????????????????????????????????????????????
                    if (MineApp.mineInfo.getMemberType() == 2) {
                        SCToastUtil.showToast(activity, "?????????????????????????????????", true);
                    } else {
                        new VipAdPW(mBinding.getRoot(), 3, otherHead);
                    }
                } else {
                    if (likeOtherStatus == 0) {
                        activity.finish();
                    } else if (likeOtherStatus == 1) {
                        LikeTypeDb.getInstance().setType(otherUserId, 1);
                        closeBtn(mBinding.likeLayout);
                        SCToastUtil.showToast(activity, "???????????????", true);
                    } else if (likeOtherStatus == 2) {
                        LikeTypeDb.getInstance().setType(otherUserId, 2);
                        closeBtn(mBinding.likeLayout);
                        SCToastUtil.showToast(activity, "???????????????????????????", true);
                    }
                }
            }
        }, activity).setOtherUserId(otherUserId).setLikeOtherStatus(likeOtherStatus);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private ObjectAnimator pvh, translateY;

    private void isLike(View view) {
        PropertyValuesHolder pvhSY = PropertyValuesHolder.ofFloat("scaleY", 1, 1.1f, 1, 1.2f, 1);
        PropertyValuesHolder pvhSX = PropertyValuesHolder.ofFloat("scaleX", 1, 1.1f, 1, 1.2f, 1);
        pvh = ObjectAnimator.ofPropertyValuesHolder(view, pvhSY, pvhSX).setDuration(500);
        pvh.start();
        MineApp.getApp().getFixedThreadPool().execute(() -> {
            SystemClock.sleep(500);
            activity.runOnUiThread(() -> {
                if (pvh != null)
                    pvh.cancel();
                pvh = null;
            });
        });
    }

    private void closeBtn(View view) {
        translateY = ObjectAnimator.ofFloat(view, "translationY", 0, 1000).setDuration(500);
        translateY.start();
        MineApp.getApp().getFixedThreadPool().execute(() -> {
            SystemClock.sleep(500);
            activity.runOnUiThread(() -> {
                translateY = null;
                mBinding.setLikeType(view == mBinding.ivSuperLike ? 2 : 1);
            });
        });
    }

    @Override
    public void memberInfoConf() {
        memberInfoConfApi api = new memberInfoConfApi(new HttpOnNextListener<ShareInfo>() {
            @Override
            public void onNext(ShareInfo o) {
                String sharedUrl = HttpManager.BASE_URL + "render/" + otherUserId + ".html?sharetextId="
                        + o.getSharetextId();
                String sharedName = o.getText().replace("{userId}", memberInfo.getUserId() + "");
                sharedName = sharedName.replace("{nick}", memberInfo.getNick());
                String content;
                if (memberInfo.getServiceTags().isEmpty()) {
                    content = o.getText();
                } else {
                    content = memberInfo.getServiceTags().substring(1, memberInfo.getServiceTags().length() - 1);
                    content = "?????????" + content.replace("#", ",");
                }

                new FunctionPW(mBinding.getRoot(), memberInfo.getImage().replace("YM0000", "430X430"), sharedName, content, sharedUrl,
                        otherUserId == BaseActivity.userId, false, false, false, new FunctionPW.CallBack() {
                    @Override
                    public void gift() {

                    }

                    @Override
                    public void delete() {
                    }

                    @Override
                    public void report() {
                        ActivityUtils.getHomeReport(otherUserId);
                    }

                    @Override
                    public void download() {

                    }

                    @Override
                    public void like() {
                        superLike(null);
                    }
                });
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void superLike(View view, PairInfo pairInfo) {
        if (MineApp.mineInfo.getMemberType() == 2) {
            makeEvaluate(2);
        } else {
            if (memberInfo != null)
                new VipAdPW(mBinding.getRoot(), 3, memberInfo.getImage());
        }
    }

    @Override
    public void returnBack() {

    }
}
