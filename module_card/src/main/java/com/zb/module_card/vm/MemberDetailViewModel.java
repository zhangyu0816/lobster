package com.zb.module_card.vm;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.app.abby.xbanner.Ads;
import com.app.abby.xbanner.XBanner;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.adapter.FragmentAdapter;
import com.zb.lib_base.api.attentionOtherApi;
import com.zb.lib_base.api.attentionStatusApi;
import com.zb.lib_base.api.cancelAttentionApi;
import com.zb.lib_base.api.otherInfoApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.AreaDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.utils.DateUtil;
import com.zb.lib_base.utils.FragmentUtils;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.SelectorPW;
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
    private CardMemberDetailBinding detailBinding;
    public long otherUserId = 0;
    public MemberInfo memberInfo;
    public CardAdapter tagAdapter;
    private List<String> tagList = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();
    private List<String> selectorList = new ArrayList<>();
    private AreaDb areaDb;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        areaDb = new AreaDb(Realm.getDefaultInstance());
        selectorList.add("举报");
        selectorList.add("分享");
        detailBinding = (CardMemberDetailBinding) binding;
        mBinding.setVariable(BR.baseInfo, "");
        AdapterBinding.viewSize(detailBinding.bannerLayout.banner, MineApp.W, ObjectUtils.getLogoHeight(1.0f));
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
        detailBinding.viewPage.setAdapter(new FragmentAdapter(activity.getSupportFragmentManager(), fragments));
        initTabLayout(new String[]{"动态", "小视频"}, detailBinding.tabLayout, detailBinding.viewPage, R.color.black_4d4, R.color.black_c3b);
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

            }
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void attentionStatus() {
        attentionStatusApi api = new attentionStatusApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                detailBinding.bannerLayout.tvFollow.setText("取消关注");
                detailBinding.bannerLayout.tvFollow.setTextColor(activity.getResources().getColor(R.color.black_827));
            }
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void attentionOther() {
        attentionOtherApi api = new attentionOtherApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                detailBinding.bannerLayout.tvFollow.setText("取消关注");
                detailBinding.bannerLayout.tvFollow.setTextColor(activity.getResources().getColor(R.color.black_827));
            }
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void cancelAttention() {
        cancelAttentionApi api = new cancelAttentionApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                detailBinding.bannerLayout.tvFollow.setText("关注");
                detailBinding.bannerLayout.tvFollow.setTextColor(activity.getResources().getColor(R.color.black_4d4));
            }
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void superLike(View view) {
        super.superLike(view);
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

            } else if (position == 1) {

            }
        });
    }

    @Override
    public void follow(View view) {
        super.follow(view);
        if (detailBinding.bannerLayout.tvFollow.getText().toString().equals("关注")) {
            attentionOther();
        } else {
            cancelAttention();
        }
    }

    // 显示相册
    private void showBanner(List<Ads> adList) {
        detailBinding.bannerLayout.banner.setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setAds(adList)
                .setImageLoader((context, ads, image, position) -> AdapterBinding.loadImage(image, ads.getSmallImage(), 0,
                        ObjectUtils.getDefaultRes(), MineApp.W, ObjectUtils.getLogoHeight(1.0f),
                        false, false, 0, false, 0,false))
                .setBannerTypes(XBanner.CIRCLE_INDICATOR_TITLE)
                .setIndicatorGravity(XBanner.INDICATOR_START)
                .setDelay(3000)
                .setUpIndicators(R.drawable.banner_circle_pressed, R.drawable.banner_circle_unpressed)
                .setUpIndicatorSize(20, 20)
                .isAutoPlay(true)
                .start();
    }
}
