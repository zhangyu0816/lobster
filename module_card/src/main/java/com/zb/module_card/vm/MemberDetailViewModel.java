package com.zb.module_card.vm;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.app.abby.xbanner.Ads;
import com.app.abby.xbanner.XBanner;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.adapter.FragmentAdapter;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.model.MemberInfo;
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
import java.util.List;

import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

public class MemberDetailViewModel extends BaseViewModel implements MemberDetailVMInterface {
    private CardMemberDetailBinding detailBinding;
    public long userId = 0;
    public MemberInfo memberInfo;
    public CardAdapter tagAdapter;
    private List<String> tagList = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();
    private List<String> selectorList = new ArrayList<>();

    public MemberDetailViewModel() {
        selectorList.add("举报");
        selectorList.add("分享");

        tagList.add("电影");
        tagList.add("电影");
        tagList.add("电影电影电影");
        tagList.add("电影");
        tagList.add("电影");
        tagList.add("电影电影电影");
        tagList.add("电影电影电影电影电影");
        tagList.add("电影电影");
        tagList.add("电影");
        tagList.add("电影");
    }

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        detailBinding = (CardMemberDetailBinding) binding;
        mBinding.setVariable(BR.baseInfo, "");
        AdapterBinding.viewSize(detailBinding.bannerLayout.banner, MineApp.W, ObjectUtils.getLogoHeight(1.0f));
        setAdapter();
        initFragments();

    }
    // 显示相册
    private void showBanner(List<Ads> adList) {
        detailBinding.bannerLayout.banner.setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setAds(adList)
                .setImageLoader((context, ads, image, position) -> AdapterBinding.loadImage(image, ads.getSmallImage(), 0,
                        ObjectUtils.getDefaultRes(), MineApp.W, ObjectUtils.getLogoHeight(1.0f),
                        false, false, 0, false, 0))
                .setBannerTypes(XBanner.CIRCLE_INDICATOR_TITLE)
                .setIndicatorGravity(XBanner.INDICATOR_START)
                .setDelay(3000)
                .setUpIndicators(R.drawable.banner_circle_pressed, R.drawable.banner_circle_unpressed)
                .setUpIndicatorSize(20, 20)
                .isAutoPlay(true)
                .start();
    }
    @Override
    public void setAdapter() {
        tagAdapter = new CardAdapter<>(activity, R.layout.item_card_tag, tagList, this);
    }

    private void initFragments() {
        fragments.clear();
        fragments.add(FragmentUtils.getCardMemberDiscoverFragment());
        fragments.add(FragmentUtils.getCardMemberVideoFragment());
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
            detailBinding.bannerLayout.tvFollow.setText("取消关注");
            detailBinding.bannerLayout.tvFollow.setTextColor(activity.getResources().getColor(R.color.black_827));
        } else {
            detailBinding.bannerLayout.tvFollow.setText("关注");
            detailBinding.bannerLayout.tvFollow.setTextColor(activity.getResources().getColor(R.color.black_4d4));
        }

    }




}
