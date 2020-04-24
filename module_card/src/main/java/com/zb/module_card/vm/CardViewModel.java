package com.zb.module_card.vm;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.CycleInterpolator;
import android.widget.RelativeLayout;

import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.AreaDb;
import com.zb.lib_base.model.CityInfo;
import com.zb.lib_base.model.DistrictInfo;
import com.zb.lib_base.model.PairInfo;
import com.zb.lib_base.model.ProvinceInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.utils.SimulateNetAPI;
import com.zb.lib_base.views.MyRecyclerView;
import com.zb.lib_base.views.RoundImageView;
import com.zb.lib_base.views.card.CardConfig;
import com.zb.lib_base.views.card.CardItemTouchHelperCallback;
import com.zb.lib_base.views.card.OnSwipeListener;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.VipAdPW;
import com.zb.module_card.BR;
import com.zb.module_card.R;
import com.zb.module_card.adapter.CardAdapter;
import com.zb.module_card.databinding.CardFragBinding;
import com.zb.module_card.iv.CardVMInterface;
import com.zb.module_card.windows.CountUsedPW;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.ItemTouchHelper;
import io.realm.Realm;

public class CardViewModel extends BaseViewModel implements CardVMInterface, OnSwipeListener<PairInfo> {
    public AreaDb areaDb;
    public CardAdapter adapter;
    private List<PairInfo> pairInfoList = new ArrayList<>();
    private CardFragBinding cardFragBinding;
    private CardItemTouchHelperCallback<PairInfo> cardCallback;
    private View currentView;
    private Handler handler = new Handler(msg -> {
        if (msg.what == 1) {
            adapter.notifyDataSetChanged();
        }
        return false;
    });
    public BaseReceiver cardReceiver;
    private boolean isAnimation = true;
    private ObjectAnimator translate = null;
    private ObjectAnimator alphaA = null;
    private int _direction = 0;
    private List<PairInfo> disLikeList = new ArrayList<>();
    private CardAdapter disListAdapter;
    private List<String> imageList = new ArrayList<>();
    private boolean canReturn = false;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        areaDb = new AreaDb(Realm.getDefaultInstance());
        cardFragBinding = (CardFragBinding) binding;
        cardReceiver = new BaseReceiver(activity, "lobster_card") {
            @Override
            public void onReceive(Context context, Intent intent) {
                int direction = intent.getIntExtra("direction", 0);
                if (direction == 0) {
                    // 不喜欢
                    currentView.startAnimation(AnimationUtils.loadAnimation(activity,
                            R.anim.view_left_out));
                    startAnimation(cardFragBinding.ivDislike, MineApp.W / 2f + ObjectUtils.getViewSizeByWidthFromMax(200) / 2f, 280);
                    currentView.postDelayed(() -> cardCallback.swiped(currentView, ItemTouchHelper.LEFT), 800);
                } else {
                    // 喜欢
                    currentView.startAnimation(AnimationUtils.loadAnimation(activity,
                            R.anim.view_right_out));
                    startAnimation(cardFragBinding.ivLike, 0 - MineApp.W / 2f - ObjectUtils.getViewSizeByWidthFromMax(200) / 2f, 280);
                    currentView.postDelayed(() -> cardCallback.swiped(currentView, ItemTouchHelper.RIGHT), 800);
                }
            }
        };

        RelativeLayout.LayoutParams paramsS = (RelativeLayout.LayoutParams) cardFragBinding.ivDislike.getLayoutParams();
        paramsS.setMarginStart(0 - ObjectUtils.getViewSizeByWidthFromMax(200));
        cardFragBinding.ivDislike.setLayoutParams(paramsS);

        RelativeLayout.LayoutParams paramsE = (RelativeLayout.LayoutParams) cardFragBinding.ivLike.getLayoutParams();
        paramsE.setMarginEnd(0 - ObjectUtils.getViewSizeByWidthFromMax(200));
        cardFragBinding.ivLike.setLayoutParams(paramsE);

        for (int i = 0; i < 5; i++) {
            PairInfo pairInfo = new PairInfo();
            pairInfo.setNick("组我吧" + i);
            pairInfo.getImageList().add("http://img01.zuwo.la/img/A/YMXXXX919714-206348_YM0000.jpg");
            pairInfo.getImageList().add("http://img01.zuwo.la/img/A/YMXXXX2350392-sgjdwurnll_YM0000.jpg");
            pairInfo.getImageList().add("http://img01.zuwo.la/img/A/YMXXXX919714-206348_YM0000.jpg");
            pairInfo.getImageList().add("http://img01.zuwo.la/img/A/YMXXXX2350392-sgjdwurnll_YM0000.jpg");
            pairInfo.getImageList().add("http://img01.zuwo.la/img/A/YMXXXX919714-206348_YM0000.jpg");
            pairInfo.getImageList().add("http://img01.zuwo.la/img/A/YMXXXX2350392-sgjdwurnll_YM0000.jpg");
            pairInfo.getImageList().add("http://img01.zuwo.la/img/A/YMXXXX919714-206348_YM0000.jpg");
            pairInfo.setPersonalitySign("文科积分为了附件为了看积分为了就访问路径访问了就放了假法律文件弗兰克就法律文件放了假放了假发");
            pairInfoList.add(pairInfo);
        }

        initArea();
        setAdapter();
    }

    @Override
    public void setAdapter() {
        adapter = new CardAdapter<>(activity, R.layout.item_card, pairInfoList, this);
        cardCallback = new CardItemTouchHelperCallback<>(adapter, pairInfoList);
        cardCallback.setOnSwipedListener(this);
        mBinding.setVariable(BR.cardCallback, cardCallback);

        disListAdapter = new CardAdapter<>(activity, R.layout.item_card_image, imageList, this);
        mBinding.setVariable(BR.adapter, disListAdapter);
    }

    @Override
    public void selectCard(View currentView, int position) {
        this.currentView = currentView;
        ActivityUtils.getCardMemberDetail(pairInfoList.get(position).getOtherUserId());
    }

    @Override
    public void returnView(View view) {
//        if (canReturn && disLikeList.size() > 0) {
//            canReturn = false;
//            PairInfo pairInfo = disLikeList.remove(0);
//            setCardAnimationLeftToRight(pairInfo);
//        }
        new CountUsedPW( activity, mBinding.getRoot(), 1);
    }

    @Override
    public void exposure(View view) {
        new VipAdPW(activity, mBinding.getRoot());
    }

    @Override
    public void leftBtn(View currentView, CardAdapter imageAdapter, int position) {
        if (imageAdapter.getSelectImageIndex() > 0) {
            int preIndex = imageAdapter.getSelectImageIndex();
            int selectIndex = preIndex - 1;
            updateAdapterUI(currentView, imageAdapter, preIndex, selectIndex, pairInfoList.get(position).getImageList());
        }
    }

    @Override
    public void rightBtn(View currentView, CardAdapter imageAdapter, int position) {
        if (imageAdapter.getSelectImageIndex() < pairInfoList.get(position).getImageList().size() - 1) {
            int preIndex = imageAdapter.getSelectImageIndex();
            int selectIndex = preIndex + 1;
            updateAdapterUI(currentView, imageAdapter, preIndex, selectIndex, pairInfoList.get(position).getImageList());
        }
    }

    // 更新adapterUI
    private void updateAdapterUI(View view, CardAdapter imageAdapter, int preIndex, int selectIndex, List<String> imageList) {
        imageAdapter.setSelectImageIndex(selectIndex);
        imageAdapter.notifyItemChanged(preIndex);
        imageAdapter.notifyItemChanged(selectIndex);

        RoundImageView imageView = view.findViewById(R.id.iv_big_image);
        MyRecyclerView imageListView = view.findViewById(R.id.image_list);

        Objects.requireNonNull(imageListView.getLayoutManager()).scrollToPosition(selectIndex);
        AdapterBinding.loadImage(imageView, imageList.get(selectIndex),
                0, ObjectUtils.getDefaultRes(), ObjectUtils.getViewSizeByWidth(0.92f),
                ObjectUtils.getLogoHeight(0.92f), false, false, 0,
                false, 0);
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotationY", 0, 1);
        animator.setInterpolator(new CycleInterpolator(1));
        animator.setRepeatCount(1);
        animator.setDuration(100);
        animator.start();
    }

    @Override
    public void superLike(View view) {
        super.superLike(view);
//        new SuperLikePW(activity, mBinding.getRoot());
        new CountUsedPW( activity, mBinding.getRoot(), 2);
    }


    @Override
    public void onSwiping(View view, float ratio, int direction) {
        if (_direction != direction) {
            _direction = direction;
            initAnimation();
        }

        if (direction == CardConfig.SWIPING_LEFT) {
            if (isAnimation) {
                startAnimation(cardFragBinding.ivDislike, MineApp.W / 2f + ObjectUtils.getViewSizeByWidthFromMax(200) / 2f, 100);
            }
        } else if (direction == CardConfig.SWIPING_RIGHT) {
            if (isAnimation) {
                startAnimation(cardFragBinding.ivLike, 0 - MineApp.W / 2f - ObjectUtils.getViewSizeByWidthFromMax(200) / 2f, 100);
            }
        } else {
            initAnimation();
        }
    }

    private void startAnimation(View view, float x, int duration) {
        translate = ObjectAnimator.ofFloat(view, "translationX", 0, x);
        alphaA = ObjectAnimator.ofFloat(view, "alpha", 0, 1);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(duration);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        //播放多条动画
        animatorSet.playTogether(translate, alphaA);
        animatorSet.start();

        isAnimation = false;
    }

    private void initAnimation() {
        isAnimation = true;
        cardFragBinding.ivDislike.setAlpha(0f);
        cardFragBinding.ivLike.setAlpha(0f);
    }

    @Override
    public void onSwiped(View view, PairInfo pairInfo, int direction) {
        initAnimation();
        if (direction == 1) {
            canReturn = true;
            disLikeList.add(0, pairInfo);
            if (disLikeList.size() > 10) {
                disLikeList.remove(disLikeList.size() - 1);
            }

        }

    }

    @Override
    public void onSwipedClear() {
        for (int i = 0; i < 5; i++) {
            PairInfo pairInfo = new PairInfo();
            pairInfo.setNick("组我吧");
            pairInfo.getImageList().add("http://img01.zuwo.la/img/A/YMXXXX919714-206348_YM0000.jpg");
            pairInfo.getImageList().add("http://img01.zuwo.la/img/A/YMXXXX2350392-sgjdwurnll_YM0000.jpg");
            pairInfo.getImageList().add("http://img01.zuwo.la/img/A/YMXXXX919714-206348_YM0000.jpg");
            pairInfo.getImageList().add("http://img01.zuwo.la/img/A/YMXXXX2350392-sgjdwurnll_YM0000.jpg");
            pairInfo.getImageList().add("http://img01.zuwo.la/img/A/YMXXXX919714-206348_YM0000.jpg");
            pairInfo.getImageList().add("http://img01.zuwo.la/img/A/YMXXXX2350392-sgjdwurnll_YM0000.jpg");
            pairInfo.getImageList().add("http://img01.zuwo.la/img/A/YMXXXX919714-206348_YM0000.jpg");
            pairInfo.setPersonalitySign("文科积分为了附件为了看积分为了就访问路径访问了就放了假法律文件弗兰克就法律文件放了假放了假发");
            pairInfoList.add(pairInfo);
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 出现
     */
    private void setCardAnimationLeftToRight(PairInfo pairInfo) {
        mBinding.setVariable(BR.pairInfo, pairInfo);
        for (String image : pairInfo.getImageList()) {
            imageList.add(image);
        }
        disListAdapter.notifyDataSetChanged();

        cardFragBinding.cardRelative.startAnimation(AnimationUtils.loadAnimation(activity,
                R.anim.card_left_out));

        cardFragBinding.cardRelative.postDelayed(() -> {
            cardFragBinding.cardRelative.setAlpha(1f);
            cardFragBinding.cardRelative.startAnimation(AnimationUtils.loadAnimation(activity,
                    R.anim.card_left_in));

            cardFragBinding.cardRelative.postDelayed(() -> {
                cardFragBinding.cardRelative.setAlpha(0f);

                cardFragBinding.cardRelative.postDelayed(() -> {
                    canReturn = true;
                    pairInfoList.add(0, pairInfo);
                    adapter.notifyDataSetChanged();
                }, 10);
            }, 300);
        }, 50);

    }


    // 省市信息
    private void initArea() {
        if (!areaDb.hasProvince()) {
            String data = SimulateNetAPI.getOriginalFundData(activity, "cityData.json");
            new Thread(() -> {
                AreaDb areaDb = new AreaDb(Realm.getDefaultInstance());
                try {
                    JSONArray array = new JSONArray(data);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject provinceJSON = array.optJSONObject(i);
                        // 省
                        ProvinceInfo provinceInfo = new ProvinceInfo();
                        provinceInfo.setProvinceId(Long.parseLong(provinceJSON.optString("value")));
                        provinceInfo.setProvinceName(provinceJSON.optString("label"));
                        areaDb.saveProvince(provinceInfo);

                        // 市
                        JSONArray cityArray = provinceJSON.optJSONArray("children");
                        if (cityArray != null) {
                            for (int j = 0; j < cityArray.length(); j++) {
                                JSONObject cityJSON = cityArray.optJSONObject(j);
                                CityInfo cityInfo = new CityInfo();
                                cityInfo.setProvinceId(provinceInfo.getProvinceId());
                                cityInfo.setCityId(Long.parseLong(cityJSON.optString("value")));
                                cityInfo.setCityName(cityJSON.optString("label"));
                                areaDb.saveCity(cityInfo);
                                // 地区
                                JSONArray districtArray = cityJSON.optJSONArray("children");
                                if (districtArray != null) {
                                    for (int k = 0; k < districtArray.length(); k++) {
                                        JSONObject districtJSON = districtArray.optJSONObject(k);
                                        DistrictInfo districtInfo = new DistrictInfo();
                                        districtInfo.setCityId(cityInfo.getCityId());
                                        districtInfo.setDistrictId(Long.parseLong(districtJSON.optString("value")));
                                        districtInfo.setDistrictName(districtJSON.optString("label"));
                                        areaDb.saveDistrictInfo(districtInfo);
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(1);
            }).start();
        } else {
            handler.sendEmptyMessage(1);
        }
    }


}
