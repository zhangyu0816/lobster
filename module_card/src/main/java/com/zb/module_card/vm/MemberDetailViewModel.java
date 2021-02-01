package com.zb.module_card.vm;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.zb.lib_base.model.ShareInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DateUtil;
import com.zb.lib_base.utils.MNImage;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.views.xbanner.XUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.FunctionPW;
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
    private boolean isLike = false;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (CardMemberDetailBinding) binding;
        mBinding.setConstellation("-");
        mBinding.setDistrict("-");
        mBinding.setCityName("-");
        mBinding.setDistant("0.0");
        mBinding.setJob("-");
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
        otherUserInfoVisit();
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

    @Override
    public void dislike(View view) {
        if (memberInfo == null) {
            SCToastUtil.showToast(activity, "网络异常，请检查网络是否链接", true);
            return;
        }
        if (showLike) {
            Intent data = new Intent("lobster_card");
            data.putExtra("direction", 0);
            activity.sendBroadcast(data);
        }
        activity.finish();
    }

    @Override
    public void like(View view) {
        if (memberInfo == null) {
            SCToastUtil.showToast(activity, "网络异常，请检查网络是否链接", true);
            return;
        }
        if (showLike) {
            activity.finish();
            Intent data = new Intent("lobster_card");
            data.putExtra("direction", 1);
            activity.sendBroadcast(data);
        } else {
            isLike(mBinding.ivLike);
            if (PreferenceUtil.readIntValue(activity, "toLikeCount_" + BaseActivity.userId + "_" + DateUtil.getNow(DateUtil.yyyy_MM_dd), -1) == 0 && MineApp.mineInfo.getMemberType() == 1) {
                new VipAdPW(mBinding.getRoot(), 6, "");
                SCToastUtil.showToast(activity, "今日喜欢次数已用完", true);
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
                mBinding.setDistant(distant);
                String cityName = AreaDb.getInstance().getCityName(memberInfo.getCityId()).replace("市", "");
                String districtName = AreaDb.getInstance().getDistrictName(memberInfo.getDistrictId());
                mBinding.setCityName(cityName);
                mBinding.setDistrict(cityName + (districtName.isEmpty() ? "" : " ") + districtName);
                Runnable ra = () -> {
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

                    activity.runOnUiThread(() -> {
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
                                        MNImage.imageBrowser(activity, mBinding.getRoot(), imageList, position, false, null),
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
                    });

                };
                MineApp.getApp().getFixedThreadPool().execute(ra);

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
                activity.sendBroadcast(new Intent("lobster_attentionList"));
                Intent intent = new Intent("lobster_attention");
                intent.putExtra("isAttention", mBinding.getIsAttention());
                activity.sendBroadcast(intent);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.ERROR) {
                    if (e.getMessage().equals("已经关注过")) {
                        mBinding.setIsAttention(true);
                        AttentionDb.getInstance().saveAttention(new AttentionInfo(otherUserId, memberInfo.getNick(), memberInfo.getImage(), true, BaseActivity.userId));
                        activity.sendBroadcast(new Intent("lobster_attentionList"));
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
                activity.sendBroadcast(new Intent("lobster_attentionList"));
                Intent intent = new Intent("lobster_attention");
                intent.putExtra("isAttention", mBinding.getIsAttention());
                activity.sendBroadcast(intent);
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
        //  likeOtherStatus  0 不喜欢  1 喜欢  2.超级喜欢 （非会员提示开通会员）
        makeEvaluateApi api = new makeEvaluateApi(new HttpOnNextListener<Integer>() {
            @Override
            public void onNext(Integer o) {
                String myHead = MineApp.mineInfo.getImage();
                String otherHead = memberInfo.getImage();
                // 1喜欢成功 2匹配成功 3喜欢次数用尽
                if (o == 1) {
                    // 不喜欢成功  喜欢成功  超级喜欢成功
                    if (likeOtherStatus == 0) {
                        activity.finish();
                    } else if (likeOtherStatus == 1) {
                        LikeDb.getInstance().saveLike(new CollectID(otherUserId));
                        activity.sendBroadcast(new Intent("lobster_isLike"));
                        activity.sendBroadcast(new Intent("lobster_updateFCL"));
                        LikeTypeDb.getInstance().setType(otherUserId, 1);
                        closeBtn(mBinding.ivLike);
                        closeBtn(mBinding.ivDislike);
                        isLike = true;
                        SCToastUtil.showToast(activity, "已喜欢成功", true);
                    } else if (likeOtherStatus == 2) {
                        if (showLike) {
                            activity.finish();
                            Intent data = new Intent("lobster_card");
                            data.putExtra("direction", 2);
                            activity.sendBroadcast(data);
                        } else {
                            if (!isLike) {
                                closeBtn(mBinding.ivLike);
                                closeBtn(mBinding.ivDislike);
                            }
                            LikeTypeDb.getInstance().setType(otherUserId, 2);
                            closeBtn(mBinding.ivSuperLike);
                            activity.sendBroadcast(new Intent("lobster_updateFCL"));
                            new SuperLikePW(mBinding.getRoot(), myHead, otherHead, MineApp.mineInfo.getSex(), memberInfo.getSex());
                        }
                    }
                } else if (o == 2) {
                    // 匹配成功
                    LikeDb.getInstance().saveLike(new CollectID(otherUserId));
                    new SuperLikePW(mBinding.getRoot(), myHead, otherHead, MineApp.mineInfo.getSex(), memberInfo.getSex(), memberInfo.getNick(),
                            () -> ActivityUtils.getChatActivity(otherUserId, false));
                    activity.sendBroadcast(new Intent("lobster_pairList"));
                    activity.sendBroadcast(new Intent("lobster_isLike"));
                    activity.sendBroadcast(new Intent("lobster_updateFCL"));
                    if (LikeTypeDb.getInstance().getType(otherUserId) != 1) {
                        closeBtn(mBinding.ivLike);
                        closeBtn(mBinding.ivDislike);
                    }
                    closeBtn(mBinding.ivSuperLike);
                    LikeTypeDb.getInstance().setType(otherUserId, 2);
                } else if (o == 3) {
                    // 喜欢次数用尽
                    new VipAdPW(mBinding.getRoot(), 6, "");
                    SCToastUtil.showToast(activity, "今日喜欢次数已用完", true);
                } else if (o == 4) {
                    // 超级喜欢时，非会员或超级喜欢次数用尽
                    if (MineApp.mineInfo.getMemberType() == 2) {
                        SCToastUtil.showToast(activity, "今日超级喜欢次数已用完", true);
                    } else {
                        new VipAdPW(mBinding.getRoot(), 3, otherHead);
                    }
                } else {
                    if (likeOtherStatus == 0) {
                        activity.finish();
                    } else if (likeOtherStatus == 1) {
                        LikeTypeDb.getInstance().setType(otherUserId, 1);
                        closeBtn(mBinding.ivLike);
                        closeBtn(mBinding.ivDislike);
                        isLike = true;
                        SCToastUtil.showToast(activity, "已喜欢成功", true);
                    } else if (likeOtherStatus == 2) {
                        LikeTypeDb.getInstance().setType(otherUserId, 2);
                        closeBtn(mBinding.ivLike);
                        closeBtn(mBinding.ivDislike);
                        closeBtn(mBinding.ivSuperLike);
                        SCToastUtil.showToast(activity, "你已超级喜欢过对方", true);
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
                    content = "兴趣：" + content.replace("#", ",");
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
