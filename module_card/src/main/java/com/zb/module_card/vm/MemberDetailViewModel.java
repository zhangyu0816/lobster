package com.zb.module_card.vm;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.app.abby.xbanner.Ads;
import com.app.abby.xbanner.XBanner;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.adapter.FragmentAdapter;
import com.zb.lib_base.api.attentionOtherApi;
import com.zb.lib_base.api.attentionStatusApi;
import com.zb.lib_base.api.cancelAttentionApi;
import com.zb.lib_base.api.makeEvaluateApi;
import com.zb.lib_base.api.otherInfoApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.AreaDb;
import com.zb.lib_base.db.AttentionDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.CollectID;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DateUtil;
import com.zb.lib_base.utils.FragmentUtils;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.CountUsedPW;
import com.zb.lib_base.windows.SelectorPW;
import com.zb.module_card.BR;
import com.zb.module_card.R;
import com.zb.module_card.adapter.CardAdapter;
import com.zb.module_card.databinding.CardMemberDetailBinding;
import com.zb.module_card.iv.MemberDetailVMInterface;
import com.zb.module_card.windows.VipAdPW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import io.realm.Realm;

public class MemberDetailViewModel extends BaseViewModel implements MemberDetailVMInterface {
    private CardMemberDetailBinding mBinding;
    public long otherUserId = 0;
    public MemberInfo memberInfo;
    public CardAdapter tagAdapter;
    private List<String> tagList = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();
    private List<String> selectorList = new ArrayList<>();
    private AreaDb areaDb;
    private AttentionDb attentionDb;
    private MineInfo mineInfo;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        areaDb = new AreaDb(Realm.getDefaultInstance());
        attentionDb = new AttentionDb(Realm.getDefaultInstance());
        mineInfo = mineInfoDb.getMineInfo();
        if (otherUserId != BaseActivity.userId)
            selectorList.add("举报");
        selectorList.add("分享");
        mBinding = (CardMemberDetailBinding) binding;
        mBinding.setVariable(BR.baseInfo, "");
        AdapterBinding.viewSize(mBinding.bannerLayout.banner, MineApp.W, ObjectUtils.getLogoHeight(1.0f));
        setAdapter();
        initFragments();
        otherInfo();
        attentionStatus();
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
        activity.finish();
        Intent data = new Intent("lobster_card");
        data.putExtra("direction", 0);
        activity.sendBroadcast(data);
    }

    @Override
    public void like(View view) {
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
                String sex = memberInfo.getSex() == 0 ? "女/" : "男/";
                String constellation = DateUtil.getConstellations(memberInfo.getBirthday()) + "/";
                String cityName = areaDb.getCityName(memberInfo.getCityId()) + " ";
                String districtName = areaDb.getDistrictName(memberInfo.getDistrictId());
                mBinding.setVariable(BR.baseInfo, distant + sex + constellation + cityName + districtName);

                if (!memberInfo.getMoreImages().isEmpty()) {
                    List<Ads> adsList = new ArrayList<>();
                    String[] images = memberInfo.getMoreImages().split("#");
                    for (String image : images) {
                        if (!image.isEmpty()) {
                            Ads ads = new Ads();
                            ads.setSmallImage(image);
                            adsList.add(ads);
                        }
                    }
                    showBanner(adsList);
                }
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
                if (!o.toString().isEmpty()) {
                    mBinding.bannerLayout.tvFollow.setText("取消关注");
                    mBinding.bannerLayout.tvFollow.setTextColor(activity.getResources().getColor(R.color.black_827));
                    attentionDb.saveAttention(new CollectID(otherUserId));
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
                mBinding.bannerLayout.tvFollow.setText("取消关注");
                mBinding.bannerLayout.tvFollow.setTextColor(activity.getResources().getColor(R.color.black_827));
                attentionDb.saveAttention(new CollectID(otherUserId));
            }
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void cancelAttention() {
        cancelAttentionApi api = new cancelAttentionApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                mBinding.bannerLayout.tvFollow.setText("关注");
                mBinding.bannerLayout.tvFollow.setTextColor(activity.getResources().getColor(R.color.black_4d4));
                attentionDb.deleteAttention(otherUserId);
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
                    // 不喜欢成功  喜欢成功  超级喜欢成功
                    activity.finish();
                    Intent data = new Intent("lobster_card");
                    data.putExtra("direction", 2);
                    activity.sendBroadcast(data);
                } else {
                    // 超级喜欢时，非会员或超级喜欢次数用尽
                    if (mineInfo.getMemberType() == 2) {
                        new CountUsedPW(activity, mBinding.getRoot(), 2);
                    } else {
                        new VipAdPW(activity, mBinding.getRoot());
                    }
                }
            }
        }, activity).setOtherUserId(otherUserId).setLikeOtherStatus(2);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void superLike(View view) {
        super.superLike(view);
        if (mineInfo.getMemberType() == 2) {
            makeEvaluate();
        } else {
            new VipAdPW(activity, mBinding.getRoot());
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
                    ActivityUtils.getHomeReport(otherUserId);
                } else {

                }
            } else if (position == 1) {

            }
        });
    }

    @Override
    public void follow(View view) {
        super.follow(view);
        if (mBinding.bannerLayout.tvFollow.getText().toString().equals("关注")) {
            attentionOther();
        } else {
            cancelAttention();
        }
    }

    // 显示相册
    private void showBanner(List<Ads> adList) {
        mBinding.bannerLayout.banner.setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setAds(adList)
                .setImageLoader((context, ads, image, position) -> AdapterBinding.loadImage(image, ads.getSmallImage(), 0,
                        ObjectUtils.getDefaultRes(), MineApp.W, ObjectUtils.getLogoHeight(1.0f),
                        false, false, 0, false, 0, false))
                .setBannerTypes(XBanner.CIRCLE_INDICATOR_TITLE)
                .setIndicatorGravity(XBanner.INDICATOR_START)
                .setDelay(3000)
                .setUpIndicators(R.drawable.banner_circle_pressed, R.drawable.banner_circle_unpressed)
                .setUpIndicatorSize(20, 20)
                .isAutoPlay(true)
                .start();
    }
}
