package com.zb.module_card.vm;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.app.abby.xbanner.Ads;
import com.app.abby.xbanner.XBanner;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.maning.imagebrowserlibrary.MNImage;
import com.umeng.socialize.media.UMImage;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.api.attentionOtherApi;
import com.zb.lib_base.api.attentionStatusApi;
import com.zb.lib_base.api.cancelAttentionApi;
import com.zb.lib_base.api.makeEvaluateApi;
import com.zb.lib_base.api.memberInfoConfApi;
import com.zb.lib_base.api.otherInfoApi;
import com.zb.lib_base.api.otherRentInfoApi;
import com.zb.lib_base.api.personOtherDynApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.AreaDb;
import com.zb.lib_base.db.LikeDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.AttentionInfo;
import com.zb.lib_base.model.CollectID;
import com.zb.lib_base.model.DiscoverInfo;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.model.RentInfo;
import com.zb.lib_base.model.ShareInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DateUtil;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.SCToastUtil;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import io.realm.Realm;

public class MemberDetailViewModel extends BaseViewModel implements MemberDetailVMInterface {
    private CardMemberDetailBinding mBinding;
    public long otherUserId = 0;
    public boolean showLike;
    public MemberInfo memberInfo;
    public CardAdapter tagAdapter;
    public CardAdapter discoverAdapter;
    private List<String> tagList = new ArrayList<>();
    private List<DiscoverInfo> discoverInfoList = new ArrayList<>();
    private AreaDb areaDb;
    private MineInfo mineInfo;
    private LikeDb likeDb;
    private BaseReceiver attentionReceiver;
    private int bannerWidth = MineApp.W;
    private int bannerHeight = MineApp.W;
    private int mainW = 0;
    private int mainH = 0;
    private List<Ads> adsList = new ArrayList<>();
    private boolean isLike = false;
    private int likeType = 0;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        areaDb = new AreaDb(Realm.getDefaultInstance());
        likeDb = new LikeDb(Realm.getDefaultInstance());
        mineInfo = mineInfoDb.getMineInfo();
        likeType = likeTypeDb.getType(otherUserId);
        mBinding = (CardMemberDetailBinding) binding;
        mBinding.setConstellation("");
        mBinding.setIsAttention(false);
        mBinding.setInfo("");
        mBinding.setLikeType(likeType);

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
    public void setAdapter() {
        tagAdapter = new CardAdapter<>(activity, R.layout.item_card_tag, tagList, this);

        discoverAdapter = new CardAdapter<>(activity, R.layout.item_card_discover_image, discoverInfoList, this);
    }

    public void onDestroy() {
        attentionReceiver.unregisterReceiver();
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
            activity.finish();
        } else {
            activity.finish();
        }
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
            if (PreferenceUtil.readIntValue(activity, "toLikeCount_" + BaseActivity.userId + "_" + DateUtil.getNow(DateUtil.yyyy_MM_dd), -1) == 0 && mineInfo.getMemberType() == 1) {
                new VipAdPW(activity, mBinding.getRoot(), false, 6, "");
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
    public void otherInfo() {
        otherInfoApi api = new otherInfoApi(new HttpOnNextListener<MemberInfo>() {
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

                String distant = "";
                if (!memberInfo.getDistance().isEmpty()) {
                    distant = String.format("%.1f", Float.parseFloat(memberInfo.getDistance()) / 1000f) + "km";
                }

                String cityName = areaDb.getCityName(memberInfo.getCityId()) + " ";
                String districtName = areaDb.getDistrictName(memberInfo.getDistrictId());
                if (cityName.trim().isEmpty())
                    mBinding.setInfo(distant + (memberInfo.getSex() == 0 ? "/女" : "/男"));
                else
                    mBinding.setInfo(distant + (memberInfo.getSex() == 0 ? "/女/" : "/男/") + cityName + " " + districtName);

                new Thread(() -> {
                    if (!memberInfo.getMoreImages().isEmpty()) {
                        String[] images = memberInfo.getMoreImages().split("#");
                        for (String image : images) {
                            if (!image.isEmpty()) {
                                Ads ads = new Ads();
                                ads.setSmallImage(image);
                                adsList.add(ads);
                                try {
                                    Bitmap bitmap = Glide.with(activity).asBitmap().load(ads.getSmallImage()).into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                                    int h = bitmap.getHeight();
                                    int w = bitmap.getWidth();
                                    if (mainW < w && mainH < h) {
                                        mainW = w;
                                        mainH = h;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else {
                        Ads ads = new Ads();
                        ads.setSmallImage(memberInfo.getSingleImage().isEmpty() ? memberInfo.getImage() : memberInfo.getSingleImage());
                        adsList.add(ads);
                        try {
                            Bitmap bitmap = Glide.with(activity).asBitmap().load(ads.getSmallImage()).into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                            int h = bitmap.getHeight();
                            int w = bitmap.getWidth();
                            if (mainW < w && mainH < h) {
                                mainW = w;
                                mainH = h;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    handler.sendEmptyMessage(0);
                }).start();
                attentionStatus();
                personOtherDyn();
                mBinding.setVariable(BR.viewModel, MemberDetailViewModel.this);
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

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            bannerWidth = MineApp.W;
            bannerHeight = (int) (MineApp.W * (float) mainH / (float) mainW);
            if (bannerHeight > ObjectUtils.getLogoHeight(1f)) {
                bannerHeight = ObjectUtils.getLogoHeight(1f);
            }
            AdapterBinding.viewSize(mBinding.banner, bannerWidth, bannerHeight);
            showBanner(adsList);
            return false;
        }
    });

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
                    attentionDb.saveAttention(new AttentionInfo(otherUserId, memberInfo.getNick(), memberInfo.getImage(), true, BaseActivity.userId));
                } else {
                    attentionDb.saveAttention(new AttentionInfo(otherUserId, memberInfo.getNick(), memberInfo.getImage(), false, BaseActivity.userId));
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
                attentionDb.saveAttention(new AttentionInfo(otherUserId, memberInfo.getNick(), memberInfo.getImage(), true, BaseActivity.userId));
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
                        attentionDb.saveAttention(new AttentionInfo(otherUserId, memberInfo.getNick(), memberInfo.getImage(), true, BaseActivity.userId));
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
                attentionDb.saveAttention(new AttentionInfo(otherUserId, memberInfo.getNick(), memberInfo.getImage(), false, BaseActivity.userId));
                activity.sendBroadcast(new Intent("lobster_attentionList"));
                Intent intent = new Intent("lobster_attention");
                intent.putExtra("isAttention", mBinding.getIsAttention());
                activity.sendBroadcast(intent);
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
                String myHead = mineInfo.getImage();
                String otherHead = memberInfo.getImage();
                // 1喜欢成功 2匹配成功 3喜欢次数用尽
                if (o == 1) {
                    // 不喜欢成功  喜欢成功  超级喜欢成功
                    if (likeOtherStatus == 0) {
                        activity.finish();
                    } else if (likeOtherStatus == 1) {
                        likeDb.saveLike(new CollectID(otherUserId));
                        activity.sendBroadcast(new Intent("lobster_isLike"));
                        likeTypeDb.setType(otherUserId, 1);
                        closeBtn(mBinding.ivLike);
                        closeBtn(mBinding.ivDislike);
                        isLike = true;
                        SCToastUtil.showToast(activity, "已喜欢成功", true);
                    } else if (likeOtherStatus == 2) {
                        if (showLike) {
                            // 不喜欢成功  喜欢成功  超级喜欢成功
                            activity.finish();
                            Intent data = new Intent("lobster_card");
                            data.putExtra("direction", 2);
                            activity.sendBroadcast(data);
                        } else {
                            if (!isLike) {
                                closeBtn(mBinding.ivLike);
                                closeBtn(mBinding.ivDislike);
                            }
                            likeTypeDb.setType(otherUserId, 2);
                            closeBtn(mBinding.ivSuperLike);
                            new SuperLikePW(activity, mBinding.getRoot(), myHead, otherHead, false, mineInfo.getSex(), memberInfo.getSex(), null);
                        }
                    }
                } else if (o == 2) {
                    // 匹配成功
                    likeDb.saveLike(new CollectID(otherUserId));
                    new SuperLikePW(activity, mBinding.getRoot(), myHead, otherHead, true, mineInfo.getSex(), memberInfo.getSex(),
                            () -> ActivityUtils.getChatActivity(otherUserId));
                    activity.sendBroadcast(new Intent("lobster_pairList"));
                    activity.sendBroadcast(new Intent("lobster_isLike"));
                    likeTypeDb.setType(otherUserId, 1);
                    mBinding.setLikeType(1);
                } else if (o == 3) {
                    // 喜欢次数用尽
                    new VipAdPW(activity, mBinding.getRoot(), false, 6, "");
                    SCToastUtil.showToast(activity, "今日喜欢次数已用完", true);
                } else if (o == 4) {
                    // 超级喜欢时，非会员或超级喜欢次数用尽
                    if (mineInfo.getMemberType() == 2) {
                        closeBtn(mBinding.ivLike);
                        closeBtn(mBinding.ivDislike);
                        closeBtn(mBinding.ivSuperLike);
                        SCToastUtil.showToast(activity, "今日超级喜欢次数已用完", true);
                    } else {
                        new VipAdPW(activity, mBinding.getRoot(), false, 3, otherHead);
                    }
                } else {
                    if (likeOtherStatus == 0) {
                        activity.finish();
                    } else if (likeOtherStatus == 1) {
                        likeTypeDb.setType(otherUserId, 1);
                        closeBtn(mBinding.ivLike);
                        closeBtn(mBinding.ivDislike);
                        isLike = true;
                        SCToastUtil.showToast(activity, "已喜欢成功", true);
                    } else if (likeOtherStatus == 2) {
                        likeTypeDb.setType(otherUserId, 2);
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

    private ObjectAnimator scaleX, scaleY, translateY;
    private AnimatorSet animatorSet = new AnimatorSet();

    private void isLike(View view) {
        scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1, 1.1f, 1, 1.2f, 1).setDuration(500);
        scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1, 1.1f, 1, 1.2f, 1).setDuration(500);
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.play(scaleX).with(scaleY);
        animatorSet.start();
        new Handler().postDelayed(() -> {
            scaleX = null;
            scaleY = null;
        }, 500);
    }

    private void closeBtn(View view) {
        translateY = ObjectAnimator.ofFloat(view, "translationY", 0, 1000).setDuration(500);
        translateY.start();
        new Handler().postDelayed(() -> {
            translateY = null;
            mBinding.setLikeType(view == mBinding.ivSuperLike ? 2 : 1);
        }, 500);
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
                UMImage umImage = new UMImage(activity, memberInfo.getImage().replace("YM0000", "430X430"));
                String content = "";
                if (memberInfo.getServiceTags().isEmpty()) {
                    content = o.getText();
                } else {
                    content = memberInfo.getServiceTags().substring(1, memberInfo.getServiceTags().length() - 1);
                    content = "兴趣：" + content.replace("#", ",");
                }

                new FunctionPW(activity, mBinding.getRoot(), umImage, sharedName, content, sharedUrl,
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
    public void superLike(View view) {
        super.superLike(view);
        if (mineInfo.getMemberType() == 2) {
            makeEvaluate(2);
        } else {
            if (memberInfo != null)
                new VipAdPW(activity, mBinding.getRoot(), false, 3, memberInfo.getImage());
        }
    }

    @Override
    public void back(View view) {
        super.back(view);
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

    // 显示相册
    private void showBanner(List<Ads> adList) {
        ArrayList<String> imageList = new ArrayList<>();
        for (Ads item : adList) {
            imageList.add(item.getSmallImage());
        }
        try {
            mBinding.banner.setImageScaleType(ImageView.ScaleType.FIT_CENTER)
                    .setAds(adList)
                    .setImageLoader((context, ads, image, position) ->
                            Glide.with(activity).asBitmap().load(ads.getSmallImage()).into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    int w = resource.getWidth();
                                    int h = resource.getHeight();
                                    if (w >= h) {
                                        AdapterBinding.viewSize(image, bannerWidth, (int) ((float) bannerWidth * h / w));
                                    } else {
                                        AdapterBinding.viewSize(image, (int) ((float) bannerHeight * w / h), bannerHeight);
                                    }
                                    image.setImageBitmap(resource);
                                }
                            })
                    )
                    .setBannerPageListener(item -> MNImage.imageBrowser(activity, mBinding.getRoot(), imageList, item, false, null))
                    .setBannerTypes(XBanner.CIRCLE_INDICATOR_TITLE)
                    .setIndicatorGravity(XBanner.INDICATOR_START)
                    .setDelay(3000)
                    .setUpIndicators(R.drawable.banner_circle_pressed, R.drawable.banner_circle_unpressed)
                    .setUpIndicatorSize(20, 20)
                    .isAutoPlay(false)
                    .start();
        } catch (Exception e) {

        }
    }
}
