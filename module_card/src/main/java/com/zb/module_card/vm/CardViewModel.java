package com.zb.module_card.vm;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.CycleInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.api.joinPairPoolApi;
import com.zb.lib_base.api.makeEvaluateApi;
import com.zb.lib_base.api.myInfoApi;
import com.zb.lib_base.api.prePairListApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.AreaDb;
import com.zb.lib_base.db.LikeDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.CityInfo;
import com.zb.lib_base.model.CollectID;
import com.zb.lib_base.model.DistrictInfo;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.model.PairInfo;
import com.zb.lib_base.model.ProvinceInfo;
import com.zb.lib_base.utils.AMapLocation;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DateUtil;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.utils.SimulateNetAPI;
import com.zb.lib_base.views.MyRecyclerView;
import com.zb.lib_base.views.card.CardConfig;
import com.zb.lib_base.views.card.CardItemTouchHelperCallback;
import com.zb.lib_base.views.card.OnSwipeListener;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.CountUsedPW;
import com.zb.lib_base.windows.SuperLikePW;
import com.zb.lib_base.windows.VipAdPW;
import com.zb.module_card.BR;
import com.zb.module_card.R;
import com.zb.module_card.adapter.CardAdapter;
import com.zb.module_card.databinding.CardFragBinding;
import com.zb.module_card.iv.CardVMInterface;
import com.zb.module_card.windows.ExposurePW;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.ItemTouchHelper;
import io.realm.Realm;

public class CardViewModel extends BaseViewModel implements CardVMInterface, OnSwipeListener<PairInfo> {
    public AreaDb areaDb;
    private LikeDb likeDb;
    private MineInfo mineInfo;
    public CardAdapter adapter;
    private List<PairInfo> pairInfoList = new ArrayList<>();
    private PairInfo pairInfo;
    private static CardFragBinding cardFragBinding;
    private CardItemTouchHelperCallback<PairInfo> cardCallback;
    private View currentView;
    private AMapLocation aMapLocation;
    private Handler handler = new Handler(msg -> {
        if (msg.what == 1) {
            getPermissions();
            adapter.notifyDataSetChanged();
        }
        return false;
    });
    public BaseReceiver cardReceiver;
    public BaseReceiver locationReceiver;
    public BaseReceiver openVipReceiver;
    public BaseReceiver mainSelectReceiver;
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
        likeDb = new LikeDb(Realm.getDefaultInstance());
        mineInfo = mineInfoDb.getMineInfo();
        aMapLocation = new AMapLocation(activity);
        cardFragBinding = (CardFragBinding) binding;
        // 详情页操作后滑动卡片
        cardReceiver = new BaseReceiver(activity, "lobster_card") {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (currentView == null) return;
                int direction = intent.getIntExtra("direction", 0);
                if (direction == 0) {
                    // 不喜欢
                    currentView.startAnimation(AnimationUtils.loadAnimation(activity,
                            R.anim.view_left_out));
                    startAnimation(cardFragBinding.ivDislike, MineApp.W / 2f + ObjectUtils.getViewSizeByWidthFromMax(200) / 2f, 280);
                    currentView.postDelayed(() -> cardCallback.swiped(currentView, ItemTouchHelper.LEFT), 800);
                } else if (direction == 1) {
                    // 喜欢
                    currentView.startAnimation(AnimationUtils.loadAnimation(activity,
                            R.anim.view_right_out));
                    startAnimation(cardFragBinding.ivLike, 0 - MineApp.W / 2f - ObjectUtils.getViewSizeByWidthFromMax(200) / 2f, 280);
                    currentView.postDelayed(() -> cardCallback.swiped(currentView, ItemTouchHelper.RIGHT), 800);
                } else {
                    // 超级喜欢
                    currentView.startAnimation(AnimationUtils.loadAnimation(activity,
                            R.anim.view_right_out));
                    startAnimation(cardFragBinding.ivLike, 0 - MineApp.W / 2f - ObjectUtils.getViewSizeByWidthFromMax(200) / 2f, 280);
                    currentView.postDelayed(() -> cardCallback.swiped(currentView, ItemTouchHelper.RIGHT), 800);
                    String myHead = mineInfo.getImage();
                    String otherHead = pairInfo.getMoreImages().split("#")[0];
                    new SuperLikePW(activity, mBinding.getRoot(), myHead, otherHead, false, mineInfo.getSex(), pairInfo.getSex(), null);
                }
            }
        };

        // 位置漫游
        locationReceiver = new BaseReceiver(activity, "lobster_location") {
            @Override
            public void onReceive(Context context, Intent intent) {
                mBinding.setVariable(BR.cityName, MineApp.cityName);
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
        mainSelectReceiver = new BaseReceiver(activity, "lobster_mainSelect") {
            @Override
            public void onReceive(Context context, Intent intent) {
                mineInfo = mineInfoDb.getMineInfo();
                prePairList(true);
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
    public void selectCard(View currentView) {
        this.currentView = currentView;
        pairInfo = pairInfoList.get(0);
        ActivityUtils.getCardMemberDetail(pairInfo.getOtherUserId(), true);
    }

    @Override
    public void returnView(View view) {
        if (mineInfo.getMemberType() == 2) {
            if (canReturn && disLikeList.size() > 0) {
                canReturn = false;
                PairInfo pairInfo = disLikeList.remove(0);
                setCardAnimationLeftToRight(pairInfo);
            }
        } else {
            new VipAdPW(activity, mBinding.getRoot(), false, 2);
        }
    }

    @Override
    public void superLike(View currentView, PairInfo pairInfo) {
        if (mineInfo.getMemberType() == 2) {
            this.currentView = currentView;
            this.pairInfo = pairInfo;
            makeEvaluate(pairInfo, 2);
        } else {
            new VipAdPW(activity, mBinding.getRoot(), false, 3);
        }
    }

    @Override
    public void exposure(View view) {
        if (mineInfo.getMemberType() == 2) {
            if (TextUtils.equals(PreferenceUtil.readStringValue(activity, "exposureTime"), DateUtil.getNow(DateUtil.yyyy_MM_dd))) {
                SCToastUtil.showToast(activity, "今日超级爆光权限已使用", true);
            } else {
                new ExposurePW(activity, mBinding.getRoot(), e -> {
                    if (e instanceof UnknownHostException || e instanceof SocketTimeoutException || e instanceof ConnectException) {
                        mBinding.setVariable(BR.isOutLine, true);
                    }
                });
            }
        } else {
            new VipAdPW(activity, mBinding.getRoot(), false, 1);
        }
    }

    @Override
    public void leftBtn(View currentView, CardAdapter imageAdapter) {
        if (imageAdapter.getSelectImageIndex() > 0) {
            int preIndex = imageAdapter.getSelectImageIndex();
            int selectIndex = preIndex - 1;
            updateAdapterUI(currentView, imageAdapter, preIndex, selectIndex, pairInfoList.get(0).getImageList());
        }
    }

    @Override
    public void rightBtn(View currentView, CardAdapter imageAdapter) {
        if (imageAdapter.getSelectImageIndex() < pairInfoList.get(0).getImageList().size() - 1) {
            int preIndex = imageAdapter.getSelectImageIndex();
            int selectIndex = preIndex + 1;
            updateAdapterUI(currentView, imageAdapter, preIndex, selectIndex, pairInfoList.get(0).getImageList());
        }
    }

    @Override
    public void selectImage(CardAdapter imageAdapter, int position) {
        int preIndex = imageAdapter.getSelectImageIndex();
        int selectIndex = position;
        updateAdapterUI(imageAdapter.getCurrentView(), imageAdapter, preIndex, selectIndex, pairInfoList.get(0).getImageList());
    }

    @Override
    public void selectCity(View view) {
        if (mineInfo.getMemberType() == 1) {
            new VipAdPW(activity, mBinding.getRoot(), false, 5);
            return;
        }
        ActivityUtils.getMineLocation(false);
    }

    @Override
    public void prePairList(boolean needProgress) {
        prePairListApi api = new prePairListApi(new HttpOnNextListener<List<PairInfo>>() {
            @Override
            public void onNext(List<PairInfo> o) {
                if (needProgress) {
                    pairInfoList.clear();
                    adapter.notifyDataSetChanged();
                }
                for (PairInfo pairInfo : o) {
                    if (!pairInfo.getMoreImages().isEmpty())
                        pairInfo.setImageList(Arrays.asList(pairInfo.getMoreImages().split("#")));
                    pairInfoList.add(pairInfo);
                }
                adapter.notifyDataSetChanged();
                cardFragBinding.setIsOutLine(false);
                cardFragBinding.setNoData(false);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    pairInfoList.clear();
                    adapter.notifyDataSetChanged();
                    cardFragBinding.setNoData(true);
                } else if (e instanceof UnknownHostException || e instanceof SocketTimeoutException || e instanceof ConnectException) {
                    cardFragBinding.setIsOutLine(true);
                }
            }
        }, activity)
                .setSex(mineInfo.getSex() == 0 ? 1 : 0)
                .setMaxAge(100)
                .setMinAge(0);
        api.setShowProgress(needProgress);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void makeEvaluate(PairInfo pairInfo, int likeOtherStatus) {
        //  likeOtherStatus  0 不喜欢  1 喜欢  2.超级喜欢 （非会员提示开通会员）
        makeEvaluateApi api = new makeEvaluateApi(new HttpOnNextListener<Integer>() {
            @Override
            public void onNext(Integer o) {
                // 1喜欢成功 2匹配成功 3喜欢次数用尽
                String myHead = mineInfo.getImage();
                String otherHead = pairInfo.getMoreImages().split("#")[0];
                if (o == 1) {
                    // 不喜欢成功  喜欢成功  超级喜欢成功
                    if (likeOtherStatus == 1) {
                        likeDb.saveLike(new CollectID(pairInfo.getOtherUserId()));
                    } else if (likeOtherStatus == 2) {
                        Intent data = new Intent("lobster_card");
                        data.putExtra("direction", 2);
                        activity.sendBroadcast(data);
                        activity.sendBroadcast(new Intent("lobster_pairList"));
                        new SuperLikePW(activity, mBinding.getRoot(), myHead, otherHead, false, mineInfo.getSex(), pairInfo.getSex(), null);
                    }
                } else if (o == 2) {
                    // 匹配成功
                    likeDb.saveLike(new CollectID(pairInfo.getOtherUserId()));
                    new SuperLikePW(activity, mBinding.getRoot(), myHead, otherHead, true, mineInfo.getSex(), pairInfo.getSex(), () -> ActivityUtils.getChatActivity(pairInfo.getOtherUserId()));
                    activity.sendBroadcast(new Intent("lobster_pairList"));
                } else if (o == 3) {
                    // 喜欢次数用尽
                    SCToastUtil.showToast(activity, "今日喜欢次数已用完", true);
                } else if (o == 4) {
                    // 超级喜欢时，非会员或超级喜欢次数用尽
                    if (mineInfo.getMemberType() == 2) {
                        new CountUsedPW(activity, mBinding.getRoot(), 2);
                    } else {
                        new VipAdPW(activity, mBinding.getRoot(), false, 3);
                    }
                } else {
                    if (likeOtherStatus == 2)
                        SCToastUtil.showToast(activity, "你已超级喜欢过对方", true);
                }
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof UnknownHostException || e instanceof SocketTimeoutException || e instanceof ConnectException) {
                    mBinding.setVariable(BR.isOutLine, true);
                }
            }
        }, activity).setOtherUserId(pairInfo.getOtherUserId()).setLikeOtherStatus(likeOtherStatus);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void onRefresh(View view) {
        pairInfoList.clear();
        adapter.notifyDataSetChanged();
        mBinding.setVariable(BR.isOutLine, false);
        prePairList(true);
    }

    @Override
    public void joinPairPool(String longitude, String latitude, long provinceId, long cityId, long districtId) {
        // 加入匹配池
        joinPairPoolApi api = new joinPairPoolApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {

            }
        }, activity).setLatitude(latitude).setLongitude(longitude).setProvinceId(provinceId).setCityId(cityId).setDistrictId(districtId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    // 更新adapterUI
    private void updateAdapterUI(View view, CardAdapter imageAdapter, int preIndex, int selectIndex, List<String> imageList) {
        if (imageList.size() == 0)
            return;
        imageAdapter.setSelectImageIndex(selectIndex);
        imageAdapter.notifyItemChanged(preIndex);
        imageAdapter.notifyItemChanged(selectIndex);

        ImageView imageView = view.findViewById(R.id.iv_big_image);
        MyRecyclerView imageListView = view.findViewById(R.id.image_list);

        Objects.requireNonNull(imageListView.getLayoutManager()).scrollToPosition(selectIndex);
        AdapterBinding.loadImage(imageView, imageList.get(selectIndex),
                0, ObjectUtils.getDefaultRes(), ObjectUtils.getViewSizeByWidth(0.92f),
                ObjectUtils.getLogoHeight(0.92f), false, true, 10,
                false, 0, false);
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
        Log.e("ratio", "ratio == " + ratio);
        if (_direction != direction) {
            Log.e("direction", "111111111111111");
            _direction = direction;
            if (animatorSet != null)
                animatorSet.cancel();
            initAnimation();
        }

        if (direction == CardConfig.SWIPING_LEFT) {
            if (isAnimation) {
                Log.e("direction", "222222222222222222");
                startAnimation(cardFragBinding.ivDislike, MineApp.W / 2f + ObjectUtils.getViewSizeByWidthFromMax(200) / 2f, 100);
            }
        } else if (direction == CardConfig.SWIPING_RIGHT) {
            if (isAnimation) {
                Log.e("direction", "333333333333333");
                startAnimation(cardFragBinding.ivLike, 0 - MineApp.W / 2f - ObjectUtils.getViewSizeByWidthFromMax(200) / 2f, 100);
            }
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


    /**
     * 权限
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            performCodeWithPermission("虾菇需要访问定位权限", new BaseActivity.PermissionCallback() {
                        @Override
                        public void hasPermission() {
                            setLocation();
                        }

                        @Override
                        public void noPermission() {
                            baseLocation();
                        }
                    }, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE);
        } else {
            setLocation();
        }
    }

    private void setLocation() {
        aMapLocation.start(location -> {
            if (location != null) {
                if (location.getErrorCode() == 0) {
                    MineApp.cityName = location.getCity();
                    String provinceName = location.getProvince();
                    String districtName = location.getDistrict();
                    String address = location.getAddress();
                    String longitude = location.getLongitude() + "";
                    String latitude = location.getLatitude() + "";

                    PreferenceUtil.saveStringValue(activity, "longitude", longitude);
                    PreferenceUtil.saveStringValue(activity, "latitude", latitude);
                    PreferenceUtil.saveStringValue(activity, "provinceName", provinceName);
                    PreferenceUtil.saveStringValue(activity, "cityName", MineApp.cityName);
                    PreferenceUtil.saveStringValue(activity, "districtName", districtName);
                    PreferenceUtil.saveStringValue(activity, "address", address);
                    joinPairPool(longitude, latitude, areaDb.getProvinceId(provinceName), areaDb.getCityId(MineApp.cityName), areaDb.getDistrictId(districtName));
                }
                aMapLocation.stop();
                aMapLocation.destroy();
            }
        });
    }

    private void baseLocation() {
        PreferenceUtil.saveStringValue(activity, "longitude", "120.641956");
        PreferenceUtil.saveStringValue(activity, "latitude", "28.021994");
        PreferenceUtil.saveStringValue(activity, "cityName", "温州市");
        PreferenceUtil.saveStringValue(activity, "provinceName", "浙江省");
        PreferenceUtil.saveStringValue(activity, "districtName", "鹿城区");
        PreferenceUtil.saveStringValue(activity, "address", "浙江省温州市鹿城区望江东路175号靠近温州银行(文化支行)");
        joinPairPool(PreferenceUtil.readStringValue(activity, "longitude"), PreferenceUtil.readStringValue(activity, "latitude"),
                areaDb.getProvinceId(PreferenceUtil.readStringValue(activity, "provinceName")),
                areaDb.getCityId(PreferenceUtil.readStringValue(activity, "cityName")),
                areaDb.getDistrictId(PreferenceUtil.readStringValue(activity, "districtName")));
    }
}
