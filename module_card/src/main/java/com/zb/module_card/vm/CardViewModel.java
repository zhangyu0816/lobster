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
import com.zb.lib_base.api.makeEvaluateApi;
import com.zb.lib_base.api.myInfoApi;
import com.zb.lib_base.api.prePairListApi;
import com.zb.lib_base.api.superExposureApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.AreaDb;
import com.zb.lib_base.db.MineInfoDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.CityInfo;
import com.zb.lib_base.model.DistrictInfo;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.model.PairInfo;
import com.zb.lib_base.model.ProvinceInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.utils.SCToastUtil;
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
import com.zb.module_card.windows.SuperLikePW;

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
    private MineInfo mineInfo;
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
    public BaseReceiver locationReceiver;
    public BaseReceiver openVipReceiver;
    private boolean isAnimation = true;
    private AnimatorSet animatorSet;
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
        mineInfo = mineInfoDb.getMineInfo();
        cardFragBinding = (CardFragBinding) binding;
        // 详情页操作后滑动卡片
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

        // 位置漫游
        locationReceiver = new BaseReceiver(activity, "lobster_location") {
            @Override
            public void onReceive(Context context, Intent intent) {
                MineApp.cityName = intent.getStringExtra("cityName");
                prePairList(true);
            }
        };

        // 开通会员
        openVipReceiver = new BaseReceiver(activity, "lobster_openVip") {
            @Override
            public void onReceive(Context context, Intent intent) {
                myInfo();
            }
        };

        RelativeLayout.LayoutParams paramsS = (RelativeLayout.LayoutParams) cardFragBinding.ivDislike.getLayoutParams();
        paramsS.setMarginStart(0 - ObjectUtils.getViewSizeByWidthFromMax(200));
        cardFragBinding.ivDislike.setLayoutParams(paramsS);

        RelativeLayout.LayoutParams paramsE = (RelativeLayout.LayoutParams) cardFragBinding.ivLike.getLayoutParams();
        paramsE.setMarginEnd(0 - ObjectUtils.getViewSizeByWidthFromMax(200));
        cardFragBinding.ivLike.setLayoutParams(paramsE);

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
        prePairList(true);
    }

    @Override
    public void selectCard(View currentView, int position) {
        this.currentView = currentView;
        ActivityUtils.getCardMemberDetail(pairInfoList.get(position).getOtherUserId());
    }

    @Override
    public void returnView(View view) {
        if (mineInfo.getMemberType() == 2) {
            if (canReturn && disLikeList.size() > 0) {
                canReturn = false;
                PairInfo pairInfo = disLikeList.remove(0);
                setCardAnimationLeftToRight(pairInfo);
            }
            // 反悔次数用尽
            // new CountUsedPW(activity, mBinding.getRoot(), 1);
        } else {
            new VipAdPW(activity, mBinding.getRoot());
        }
    }

    @Override
    public void superLike(PairInfo pairInfo) {
        if (mineInfo.getMemberType() == 2) {
            makeEvaluate(pairInfo, 2);
        } else {
            new VipAdPW(activity, mBinding.getRoot());
        }
    }

    @Override
    public void exposure(View view) {
        if (mineInfo.getMemberType() == 2) {
            superExposureApi api = new superExposureApi(new HttpOnNextListener() {
                @Override
                public void onNext(Object o) {
                    SCToastUtil.showToast(activity, "曝光成功");
                }
            }, activity);
            HttpManager.getInstance().doHttpDeal(api);
        } else {
            new VipAdPW(activity, mBinding.getRoot());
        }
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

    @Override
    public void selectCity(View view) {
        ActivityUtils.getMineLocation();
    }

    @Override
    public void prePairList(boolean needProgress) {
//        prePairListApi api = new prePairListApi(new HttpOnNextListener<List<PairInfo>>() {
//            @Override
//            public void onNext(List<PairInfo> o) {
//                if (needProgress) {
//                    pairInfoList.clear();
//                    adapter.notifyDataSetChanged();
//                }
//                pairInfoList.addAll(o);
//                adapter.notifyDataSetChanged();
//            }
//        }, activity)
//                .setSex(mineInfo.getSex() == 0 ? 1 : 0)
//                .setMaxAge(100)
//                .setMinAge(0);
//        api.setShowProgress(needProgress);
//        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void makeEvaluate(PairInfo pairInfo, int likeOtherStatus) {
        //  likeOtherStatus  0 不喜欢  1 喜欢  2.超级喜欢 （非会员提示开通会员）
        makeEvaluateApi api = new makeEvaluateApi(new HttpOnNextListener<Integer>() {
            @Override
            public void onNext(Integer o) {
                // 1喜欢成功 2匹配成功 3喜欢次数用尽
                String myHead = mineInfo.getMoreImages().split("#")[0];
                String otherHead = pairInfo.getMoreImages().split("#")[0];
                if (o == 1) {
                    if (likeOtherStatus == 2) {
                        new SuperLikePW(activity, mBinding.getRoot(), myHead, otherHead, false, mineInfo.getSex(), pairInfo.getSex());
                    }
                } else if (o == 2) {
                    new SuperLikePW(activity, mBinding.getRoot(), myHead, otherHead, true, mineInfo.getSex(), pairInfo.getSex());
                } else if (o == 3) {
                    if (likeOtherStatus == 1) {
                        SCToastUtil.showToast(activity, "今日喜欢次数已用完");
                    } else if (likeOtherStatus == 2) {
                        new CountUsedPW(activity, mBinding.getRoot(), 2);
                    }
                }
            }
        }, activity).setOtherUserId(pairInfo.getOtherUserId()).setLikeOtherStatus(likeOtherStatus);
        HttpManager.getInstance().doHttpDeal(api);
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

    private void startAnimation(View view, float x, int duration) {
        translate = ObjectAnimator.ofFloat(view, "translationX", 0, x);
        alphaA = ObjectAnimator.ofFloat(view, "alpha", 0, 1);
        animatorSet = new AnimatorSet();
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
    public void onSwiping(View view, float ratio, int direction) {
        if (_direction != direction) {
            _direction = direction;
            if (animatorSet != null)
                animatorSet.cancel();
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


    @Override
    public void onSwiped(View view, PairInfo pairInfo, int direction) {
        initAnimation();
        int likeOtherStatus = 1;
        if (direction == CardConfig.SWIPED_LEFT) {
            canReturn = true;
            likeOtherStatus = 0;
            disLikeList.add(0, pairInfo);
        }
        makeEvaluate(pairInfo, likeOtherStatus);
    }

    @Override
    public void onSwipedClear() {
        prePairList(false);
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

    @Override
    public void myInfo() {
        myInfoApi api = new myInfoApi(new HttpOnNextListener<MineInfo>() {
            @Override
            public void onNext(MineInfo o) {
                mineInfo = o;
                mineInfoDb.saveMineInfo(o);
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

}
