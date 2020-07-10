package com.zb.module_card.vm;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.app.abby.xbanner.Ads;
import com.app.abby.xbanner.XBanner;
import com.umeng.socialize.media.UMImage;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.adapter.FragmentAdapter;
import com.zb.lib_base.api.attentionOtherApi;
import com.zb.lib_base.api.attentionStatusApi;
import com.zb.lib_base.api.cancelAttentionApi;
import com.zb.lib_base.api.makeEvaluateApi;
import com.zb.lib_base.api.memberInfoConfApi;
import com.zb.lib_base.api.otherInfoApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.AreaDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.AttentionInfo;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.model.ShareInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DateUtil;
import com.zb.lib_base.utils.DisplayUtils;
import com.zb.lib_base.utils.FragmentUtils;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.CountUsedPW;
import com.zb.lib_base.windows.SelectorPW;
import com.zb.lib_base.windows.SharePW;
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
import androidx.fragment.app.Fragment;
import io.realm.Realm;

public class MemberDetailViewModel extends BaseViewModel implements MemberDetailVMInterface {
    private CardMemberDetailBinding mBinding;
    public long otherUserId = 0;
    public boolean showLike;
    public MemberInfo memberInfo;
    public CardAdapter tagAdapter;
    private List<String> tagList = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();
    private List<String> selectorList = new ArrayList<>();
    private AreaDb areaDb;
    private MineInfo mineInfo;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        areaDb = new AreaDb(Realm.getDefaultInstance());
        mineInfo = mineInfoDb.getMineInfo();
        if (otherUserId != BaseActivity.userId) {
            selectorList.add("超级喜欢");
            selectorList.add("举报");
        }

        selectorList.add("分享");
        mBinding = (CardMemberDetailBinding) binding;
        mBinding.setVariable(BR.baseInfo, "");
        AdapterBinding.viewSize(mBinding.banner, MineApp.W, ObjectUtils.getLogoHeight(1.0f));

        new Handler().postDelayed(() -> {
            int height = DisplayUtils.dip2px(82) - mBinding.topLinear.getHeight();
            mBinding.appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> mBinding.setShowBg(verticalOffset <= height));
        }, 300);
        setAdapter();
        initFragments();
        otherInfo();
    }

    @Override
    public void setAdapter() {
        tagAdapter = new CardAdapter<>(activity, R.layout.item_card_tag, tagList, this);
    }

    private void initFragments() {
        fragments.clear();
        fragments.add(FragmentUtils.getCardMemberDiscoverFragment(otherUserId));
        fragments.add(FragmentUtils.getCardMemberVideoFragment(otherUserId));
        mBinding.viewPage.setAdapter(new FragmentAdapter(activity.getSupportFragmentManager(), fragments));
        initTabLayout(new String[]{"动态", "小视频"}, mBinding.tabLayout, mBinding.viewPage, R.color.black_4d4, R.color.black_c3b);
    }

    @Override
    public void dislike(View view) {
        if (memberInfo == null) {
            SCToastUtil.showToast(activity, "网络异常，请检查网络是否链接", true);
            return;
        }
        activity.finish();
        Intent data = new Intent("lobster_card");
        data.putExtra("direction", 0);
        activity.sendBroadcast(data);
    }

    @Override
    public void like(View view) {
        if (memberInfo == null) {
            SCToastUtil.showToast(activity, "网络异常，请检查网络是否链接", true);
            return;
        }
        activity.finish();
        Intent data = new Intent("lobster_card");
        data.putExtra("direction", 1);
        activity.sendBroadcast(data);
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
                }
                String distant = "";
                if (!memberInfo.getDistance().isEmpty()) {
                    distant = String.format("%.1f", Float.parseFloat(memberInfo.getDistance()) / 1000f) + "km/";
                }

                String sex = memberInfo.getSex() == 0 ? "女/" : "男/";
                String constellation = DateUtil.getConstellations(memberInfo.getBirthday()) + "/";
                String cityName = areaDb.getCityName(memberInfo.getCityId()) + " ";
                String districtName = areaDb.getDistrictName(memberInfo.getDistrictId());
                mBinding.setVariable(BR.baseInfo, distant + sex + constellation + cityName + districtName);

                List<Ads> adsList = new ArrayList<>();
                if (!memberInfo.getMoreImages().isEmpty()) {
                    String[] images = memberInfo.getMoreImages().split("#");
                    for (String image : images) {
                        if (!image.isEmpty()) {
                            Ads ads = new Ads();
                            ads.setSmallImage(image);
                            adsList.add(ads);
                        }
                    }
                } else {
                    Ads ads = new Ads();
                    ads.setSmallImage(memberInfo.getImage());
                    adsList.add(ads);
                }
                showBanner(adsList);
                attentionStatus();
                mBinding.setVariable(BR.viewModel, MemberDetailViewModel.this);
            }
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void attentionStatus() {
        attentionStatusApi api = new attentionStatusApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                if (o == null) {
                    mBinding.tvFollow.setText("取消关注");
                    mBinding.tvFollow.setTextColor(activity.getResources().getColor(R.color.black_827));
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
                mBinding.tvFollow.setText("取消关注");
                mBinding.tvFollow.setTextColor(activity.getResources().getColor(R.color.black_827));
                attentionDb.saveAttention(new AttentionInfo(otherUserId, memberInfo.getNick(), memberInfo.getImage(), true, BaseActivity.userId));
                activity.sendBroadcast(new Intent("lobster_attentionList"));
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.ERROR) {
                    if (e.getMessage().equals("已经关注过")) {
                        mBinding.tvFollow.setText("取消关注");
                        mBinding.tvFollow.setTextColor(activity.getResources().getColor(R.color.black_827));
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
                mBinding.tvFollow.setText("关注");
                mBinding.tvFollow.setTextColor(activity.getResources().getColor(R.color.black_4d4));
                attentionDb.saveAttention(new AttentionInfo(otherUserId, memberInfo.getNick(), memberInfo.getImage(), false, BaseActivity.userId));
                activity.sendBroadcast(new Intent("lobster_attentionList"));
            }
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void makeEvaluate() {
        //  likeOtherStatus  0 不喜欢  1 喜欢  2.超级喜欢 （非会员提示开通会员）
        makeEvaluateApi api = new makeEvaluateApi(new HttpOnNextListener<Integer>() {
            @Override
            public void onNext(Integer o) {
                // 1喜欢成功 2匹配成功 3喜欢次数用尽
                if (o == 1) {
                    if (showLike) {
                        // 不喜欢成功  喜欢成功  超级喜欢成功
                        activity.finish();
                        Intent data = new Intent("lobster_card");
                        data.putExtra("direction", 2);
                        activity.sendBroadcast(data);
                    } else {
                        String myHead = mineInfo.getImage();
                        String otherHead = memberInfo.getMoreImages().split("#")[0];
                        new SuperLikePW(activity, mBinding.getRoot(), myHead, otherHead, false, mineInfo.getSex(), memberInfo.getSex(), null);
                    }
                } else {
                    // 超级喜欢时，非会员或超级喜欢次数用尽
                    if (mineInfo.getMemberType() == 2) {
                        new CountUsedPW(activity, mBinding.getRoot(), 2);
                    } else {
                        new VipAdPW(activity, mBinding.getRoot(), false, 3);
                    }
                }
            }
        }, activity).setOtherUserId(otherUserId).setLikeOtherStatus(2);
        HttpManager.getInstance().doHttpDeal(api);
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
                String content = memberInfo.getServiceTags().substring(1, memberInfo.getServiceTags().length() - 1);
                content = "兴趣：" + content.replace("#", ",");
                new SharePW(activity, mBinding.getRoot(), umImage, sharedName, content, sharedUrl);
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void superLike(View view) {
        super.superLike(view);
        if (mineInfo.getMemberType() == 2) {
            makeEvaluate();
        } else {
            new VipAdPW(activity, mBinding.getRoot(), false, 3);
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
        new SelectorPW(activity, mBinding.getRoot(), selectorList, position -> {
            if (position == 0) {
                if (otherUserId != BaseActivity.userId) {
                    superLike(null);
                } else {
                    memberInfoConf();
                }
            } else if (position == 1) {
                ActivityUtils.getHomeReport(otherUserId);
            } else if (position == 2) {
                memberInfoConf();
            }
        });
    }

    @Override
    public void follow(View view) {
        super.follow(view);
        if (mBinding.tvFollow.getText().toString().equals("关注")) {
            attentionOther();
        } else {
            cancelAttention();
        }
    }

    // 显示相册
    private void showBanner(List<Ads> adList) {
        mBinding.banner.setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setAds(adList)
                .setImageLoader((context, ads, image, position) -> AdapterBinding.loadImage(image, ads.getSmallImage(), 0,
                        ObjectUtils.getDefaultRes(), MineApp.W, ObjectUtils.getLogoHeight(1.0f),
                        false, false, 0, false, 0, false))
                .setBannerTypes(XBanner.CIRCLE_INDICATOR_TITLE)
                .setIndicatorGravity(XBanner.INDICATOR_START)
                .setDelay(3000)
                .setUpIndicators(R.drawable.banner_circle_pressed, R.drawable.banner_circle_unpressed)
                .setUpIndicatorSize(20, 20)
                .isAutoPlay(false)
                .start();
    }
}
