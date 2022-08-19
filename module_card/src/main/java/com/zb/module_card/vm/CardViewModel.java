package com.zb.module_card.vm;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.CycleInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.api.joinPairPoolApi;
import com.zb.lib_base.api.makeEvaluateApi;
import com.zb.lib_base.api.modifyMemberInfoForNoVerifyApi;
import com.zb.lib_base.api.myInfoApi;
import com.zb.lib_base.api.prePairListApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.AreaDb;
import com.zb.lib_base.db.LikeDb;
import com.zb.lib_base.db.LikeTypeDb;
import com.zb.lib_base.http.CustomProgressDialog;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.iv.SuperLikeInterface;
import com.zb.lib_base.model.CityInfo;
import com.zb.lib_base.model.CollectID;
import com.zb.lib_base.model.DistrictInfo;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.model.PairInfo;
import com.zb.lib_base.model.ProvinceInfo;
import com.zb.lib_base.utils.AMapLocation;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DateUtil;
import com.zb.lib_base.utils.DisplayUtils;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.utils.SimulateNetAPI;
import com.zb.lib_base.utils.StatusBarUtil;
import com.zb.lib_base.views.MyRecyclerView;
import com.zb.lib_base.views.card.CardConfig;
import com.zb.lib_base.views.card.CardItemTouchHelperCallback;
import com.zb.lib_base.views.card.OnSwipeListener;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.SuperLikePW;
import com.zb.lib_base.windows.TextPW;
import com.zb.lib_base.windows.VipAdPW;
import com.zb.module_card.BR;
import com.zb.module_card.R;
import com.zb.module_card.adapter.CardAdapter;
import com.zb.module_card.databinding.CardFragBinding;
import com.zb.module_card.iv.CardVMInterface;

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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import io.realm.Realm;

public class CardViewModel extends BaseViewModel implements CardVMInterface, OnSwipeListener<PairInfo>, SuperLikeInterface {

    public CardAdapter adapter;
    private List<PairInfo> pairInfoList = new ArrayList<>();
    private PairInfo pairInfo;
    private CardFragBinding mBinding;
    private CardItemTouchHelperCallback<PairInfo> cardCallback;
    private View currentView;
    private AMapLocation aMapLocation;
    private BaseReceiver cardReceiver;
    private BaseReceiver locationReceiver;
    private BaseReceiver openVipReceiver;
    private BaseReceiver isLikeReceiver;
    private BaseReceiver animatorStatusReceiver;
    private List<PairInfo> disLikeList = new ArrayList<>();
    private CardAdapter disListAdapter;
    private List<String> imageList = new ArrayList<>();
    private boolean canReturn = false;

    private int superLikeStatus = 0;
    private int likeCount = 30;
    private Handler mHandler = new Handler();
    private Runnable ra = new Runnable() {
        @Override
        public void run() {
            startAnim();
            mHandler.postDelayed(ra, 5000);
        }
    };
    private ObjectAnimator animator;
    private ObjectAnimator animatorUI;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        aMapLocation = new AMapLocation(activity);
        mBinding = (CardFragBinding) binding;
        mBinding.setPairInfo(new PairInfo());
        if (PreferenceUtil.readIntValue(activity, "toLikeCount_" + BaseActivity.userId + "_" + DateUtil.getNow(DateUtil.yyyy_MM_dd), -1) == -1)
            likeCount = MineApp.mineInfo.getSurplusToDayLikeNumber();
        else
            likeCount = PreferenceUtil.readIntValue(activity, "toLikeCount_" + BaseActivity.userId + "_" + DateUtil.getNow(DateUtil.yyyy_MM_dd), -1);

        mBinding.setLikeCount(likeCount);
        mBinding.setShowCount(false);

        // 详情页操作后滑动卡片
        cardReceiver = new BaseReceiver(activity, "lobster_card") {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (currentView == null) return;
                int direction = intent.getIntExtra("direction", 0);
                ImageView ivLike = currentView.findViewById(R.id.iv_like);
                ImageView ivDislike = currentView.findViewById(R.id.iv_dislike);
                if (direction == 0) {
                    // 不喜欢
                    ivDislike.setVisibility(View.VISIBLE);
                    currentView.startAnimation(AnimationUtils.loadAnimation(activity,
                            R.anim.view_right_out));
                    currentView.postDelayed(() -> {
                        cardCallback.swiped(currentView, ItemTouchHelper.RIGHT);
                        ivDislike.setVisibility(View.GONE);
                    }, 800);
                } else if (direction == 1) {
                    // 喜欢
                    ivLike.setVisibility(View.VISIBLE);
                    currentView.startAnimation(AnimationUtils.loadAnimation(activity,
                            R.anim.view_left_out));
                    currentView.postDelayed(() -> {
                        cardCallback.swiped(currentView, ItemTouchHelper.LEFT);
                        ivLike.setVisibility(View.GONE);
                    }, 800);
                } else {
                    // 超级喜欢
                    ivLike.setVisibility(View.VISIBLE);
                    currentView.startAnimation(AnimationUtils.loadAnimation(activity,
                            R.anim.view_left_out));
                    superLikeStatus = 2;
                    currentView.postDelayed(() -> {
                        cardCallback.swiped(currentView, ItemTouchHelper.LEFT);
                        ivLike.setVisibility(View.GONE);
                    }, 800);
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

        isLikeReceiver = new BaseReceiver(activity, "lobster_isLike") {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (MineApp.mineInfo.getMemberType() == 1) {
                    likeCount--;
                    if (pairInfoList.size() > 0)
                        updateCount(likeCount);
                }
            }
        };

        animatorStatusReceiver = new BaseReceiver(activity, "lobster_animatorStatus") {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isPlay = intent.getBooleanExtra("isPlay", false);
                if (mBinding.cardRelative.getAlpha() == 1 && TextUtils.equals(mBinding.getPairInfo().getSingleImage(), "card_progress_icon")) {
                    mBinding.setIsPlay(isPlay);
                }
                if (isPlay) {
                    startAnim();
                    mHandler.postDelayed(ra, 5000);
                } else {
                    mHandler.removeCallbacks(ra);
                    stopAnim();
                }
            }
        };
        playExposure();
        startAnim();
        mHandler.postDelayed(ra, 5000);
        initArea();
        setAdapter();

        int height = StatusBarUtil.getStatusBarHeight(activity);
        MineApp.getApp().getFixedThreadPool().execute(() -> {
            SystemClock.sleep(500);
            activity.runOnUiThread(() -> {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mBinding.tvCity.getLayoutParams();
                params.setMargins(DisplayUtils.dip2px(15f), height + DisplayUtils.dip2px(15f), DisplayUtils.dip2px(15f), 0);
                mBinding.tvCity.setLayoutParams(params);

                RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) mBinding.ivExposure.getLayoutParams();
                params1.setMargins(DisplayUtils.dip2px(15f), height + DisplayUtils.dip2px(15f), DisplayUtils.dip2px(15f), 0);
                mBinding.ivExposure.setLayoutParams(params1);
            });
        });
    }

    private void playExposure() {
        if (MineApp.mineInfo.getMemberType() == 1) {
            animator = ObjectAnimator.ofFloat(mBinding.tvCity, "rotation", 0, -5, 0, 5).setDuration(400);
            animator.setRepeatMode(ValueAnimator.REVERSE);
            animator.setRepeatCount(3);
        }
    }

    private void startAnim() {
        if (animator != null && !animator.isRunning())
            animator.start();
    }

    private void stopAnim() {
        if (animator != null && animator.isRunning())
            animator.cancel();
    }

    public void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacks(ra);
        }
        mHandler = null;
        try {
            cardReceiver.unregisterReceiver();
            locationReceiver.unregisterReceiver();
            openVipReceiver.unregisterReceiver();
            isLikeReceiver.unregisterReceiver();
            animatorStatusReceiver.unregisterReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setAdapter() {

        adapter = new CardAdapter<>(activity, R.layout.item_card, pairInfoList, this);
        cardCallback = new CardItemTouchHelperCallback<>(adapter, pairInfoList);
        cardCallback.setOnSwipedListener(this);
        mBinding.setVariable(BR.cardCallback, cardCallback);
        disListAdapter = new CardAdapter<>(activity, R.layout.item_card_image, imageList, this);
        mBinding.setVariable(BR.adapter, disListAdapter);
        createProgress();
    }

    @Override
    public void selectCard(View currentView) {
        this.currentView = currentView;
        pairInfo = pairInfoList.get(0);
        ActivityUtils.getCardMemberDetail(pairInfo.getOtherUserId(), true);
    }

    @Override
    public void superLike(View currentView, PairInfo pairInfo) {
        if (MineApp.mineInfo.getMemberType() == 2) {
            this.currentView = currentView;
            this.pairInfo = pairInfo;
            makeEvaluate(pairInfo, 2);
        } else {
            new VipAdPW(mBinding.getRoot(), 3, pairInfo.getSingleImage());
        }
//          new SuperLikePW(mBinding.getRoot(), MineApp.mineInfo.getImage(), MineApp.mineInfo.getImage(), MineApp.mineInfo.getSex(), MineApp.mineInfo.getSex(), "哈哈哈",
//                        () -> SCToastUtil.showToast(activity, "聊天", true));
//        new SuperLikePW(mBinding.getRoot(), MineApp.mineInfo.getImage(), MineApp.mineInfo.getImage(), MineApp.mineInfo.getSex(), MineApp.mineInfo.getSex());
    }

    @Override
    public void returnBack() {
        if (MineApp.mineInfo.getMemberType() == 2) {
            if (canReturn && disLikeList.size() > 0) {
                canReturn = false;
                PairInfo pairInfo = disLikeList.remove(0);
                setCardAnimationLeftToRight(pairInfo);
            }
        } else {
            new VipAdPW(mBinding.getRoot(), 2, "");
        }
    }

    @Override
    public void exposure(View view) {
        if (MineApp.mineInfo.getMemberType() == 2) {
            new TextPW(mBinding.getRoot(), "VIP专享", "虾菇每日自动为你增加曝光度，让10的人优先看到你", "明白了", () -> {

            });
        } else {
            new VipAdPW(mBinding.getRoot(), 1, "");
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
    public void selectImage(int position) {
        View currentView = Objects.requireNonNull(mBinding.cardList.getLayoutManager()).findViewByPosition(0);
        assert currentView != null;
        MyRecyclerView imageList = currentView.findViewById(R.id.image_list);
        CardAdapter adapter = (CardAdapter) imageList.getAdapter();

        assert adapter != null;
        updateAdapterUI(currentView, adapter, adapter.getSelectImageIndex(), position, pairInfoList.get(0).getImageList());
    }

    @Override
    public void selectCity(View view) {
        if (MineApp.mineInfo.getMemberType() == 1) {
            new VipAdPW(mBinding.getRoot(), 5, "");
            return;
        }
        if (PreferenceUtil.readStringValue(activity, "latitude").equals("0")) {
            new TextPW(mBinding.getRoot(), "定位失败", "定位失败，无法选取地址，请重新定位", "重新定位", () -> {
                if (checkPermissionGranted(activity, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    CustomProgressDialog.showLoading(activity, "定位...");
                    setLocation(2);
                } else {
                    SCToastUtil.showToast(activity, "你未申请定位权限，请前往设置--权限管理--权限进行设置", true);
                }
            });
        } else {
            if (checkPermissionGranted(activity, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)) {
                setLocation(3);
            } else
                SCToastUtil.showToast(activity, "你未申请定位权限，请前往设置--权限管理--权限进行设置", true);
        }
    }

    private List<Long> userIdList = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void prePairList(boolean needProgress) {
        if (needProgress) {
            pairInfoList.clear();
            adapter.notifyDataSetChanged();
            createProgress();
            userIdList.clear();
        }
        prePairListApi api = new prePairListApi(new HttpOnNextListener<List<PairInfo>>() {
            @Override
            public void onNext(List<PairInfo> o) {
                MineApp.noDataCount = 0;
                mBinding.setShowRemind(false);
                int start = pairInfoList.size();
                for (PairInfo pairInfo : o) {
                    if (!userIdList.contains(pairInfo.getOtherUserId())) {
                        userIdList.add(pairInfo.getOtherUserId());
                        List<String> imageList = new ArrayList<>();
                        if (!pairInfo.getMoreImages().isEmpty()) {
                            imageList.addAll(Arrays.asList(pairInfo.getMoreImages().split("#")));
                        }
                        if (imageList.size() == 0) {
                            imageList.add(pairInfo.getSingleImage());
                        }
                        pairInfo.setImageList(imageList);
                        pairInfoList.add(pairInfo);
                    }
                }
                adapter.notifyItemRangeChanged(start, pairInfoList.size());

                MineApp.getApp().getFixedThreadPool().execute(() -> {
                    SystemClock.sleep(500);
                    activity.runOnUiThread(() -> {
                        mBinding.setIsPlay(false);
                        mBinding.cardRelative.setAlpha(0f);
                        if (MineApp.mineInfo.getMemberType() == 1) {
                            updateCount(likeCount);
                        }
                    });
                });
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    MineApp.noDataCount++;
                    userIdList.clear();
                    createProgress();
                    if (MineApp.noDataCount < 2) {
                        prePairList(needProgress);
                    } else {
                        mBinding.setShowRemind(true);
                    }
                } else if (e instanceof UnknownHostException || e instanceof SocketTimeoutException || e instanceof ConnectException) {
                    createOutLike();
                }
            }
        }, activity)
                .setSex(MineApp.sex)
                .setMaxAge(MineApp.maxAge)
                .setMinAge(MineApp.minAge);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void makeEvaluate(PairInfo pairInfo, int likeOtherStatus) {
        if (pairInfo.getOtherUserId() == 0)
            return;
        //  likeOtherStatus  0 不喜欢  1 喜欢  2.超级喜欢 （非会员提示开通会员）
        makeEvaluateApi api = new makeEvaluateApi(new HttpOnNextListener<Integer>() {
            @Override
            public void onNext(Integer o) {
                // 1喜欢成功 2匹配成功 3喜欢次数用尽
                String myHead = MineApp.mineInfo.getImage();
                String otherHead = pairInfo.getSingleImage();
                if (o == 1) {
                    // 不喜欢成功  喜欢成功  超级喜欢成功
                    if (likeOtherStatus == 1) {
                        LikeDb.getInstance().saveLike(new CollectID(pairInfo.getOtherUserId()));
                        LikeTypeDb.getInstance().setType(pairInfo.getOtherUserId(), 1);
                    } else if (likeOtherStatus == 2) {
                        Intent data = new Intent("lobster_card");
                        data.putExtra("direction", 2);
                        LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(data);
                        LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_pairList"));
                        LikeTypeDb.getInstance().setType(pairInfo.getOtherUserId(), 2);
                        new SuperLikePW(mBinding.getRoot(), myHead, otherHead, MineApp.mineInfo.getSex(), pairInfo.getSex());
                    }
                } else if (o == 2) {
                    // 匹配成功
                    LikeDb.getInstance().saveLike(new CollectID(pairInfo.getOtherUserId()));
                    new SuperLikePW(mBinding.getRoot(), myHead, otherHead, MineApp.mineInfo.getSex(), pairInfo.getSex(), pairInfo.getNick(),
                            () -> ActivityUtils.getChatActivity(pairInfo.getOtherUserId(), false));
                    LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_pairList"));
                    LikeTypeDb.getInstance().setType(pairInfo.getOtherUserId(), 1);
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
                    if (likeOtherStatus == 1) {
                        LikeDb.getInstance().saveLike(new CollectID(pairInfo.getOtherUserId()));
                        LikeTypeDb.getInstance().setType(pairInfo.getOtherUserId(), 1);
                    } else if (likeOtherStatus == 2) {
                        Intent data = new Intent("lobster_card");
                        data.putExtra("direction", 2);
                        LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(data);
                        LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_pairList"));
                        LikeTypeDb.getInstance().setType(pairInfo.getOtherUserId(), 2);
                        new SuperLikePW(mBinding.getRoot(), myHead, otherHead, MineApp.mineInfo.getSex(), pairInfo.getSex());
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof UnknownHostException || e instanceof SocketTimeoutException || e instanceof ConnectException) {
                    createOutLike();
                }
            }
        }, activity).setOtherUserId(pairInfo.getOtherUserId()).setLikeOtherStatus(likeOtherStatus);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void createOutLike() {
        pairInfoList.clear();
        adapter.notifyDataSetChanged();

        mBinding.cardRelative.setAlpha(1f);
        mBinding.setIsPlay(false);
        mBinding.setShowCount(false);

        PairInfo pairInfo = new PairInfo();
        pairInfo.setSingleImage("card_out_line_bg");
        mBinding.setPairInfo(pairInfo);
    }

    private void createProgress() {
        mBinding.cardRelative.setAlpha(1f);
        mBinding.setIsPlay(true);
        mBinding.setShowCount(false);
        PairInfo pairInfo = new PairInfo();
        pairInfo.setSingleImage("card_progress_icon");
        mBinding.setPairInfo(pairInfo);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onRefresh(View view) {
        pairInfoList.clear();
        adapter.notifyDataSetChanged();
        prePairList(true);
    }

    @Override
    public void joinPairPool(String longitude, String latitude, long provinceId, long cityId, long districtId) {
        // 加入匹配池
        joinPairPoolApi api = new joinPairPoolApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                prePairList(true);
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
                0, ObjectUtils.getDefaultRes(), ObjectUtils.getViewSizeByWidth(0.94f),
                -1, false, true, 10,
                false, 0, false);
        animatorUI = ObjectAnimator.ofFloat(view, "rotationY", 0, 1);
        animatorUI.setInterpolator(new CycleInterpolator(1));
        animatorUI.setRepeatCount(1);
        animatorUI.setDuration(100);
        animatorUI.start();
        MineApp.getApp().getFixedThreadPool().execute(() -> {
            SystemClock.sleep(200);
            activity.runOnUiThread(() -> {
                if (animatorUI != null)
                    animatorUI.cancel();
                animatorUI = null;
            });
        });
    }

    private ImageView ivLike, ivDislike;

    @Override
    public void onSwiping(View view, float ratio, int direction) {
        if (ivLike == null)
            ivLike = view.findViewById(R.id.iv_like);
        if (ivDislike == null)
            ivDislike = view.findViewById(R.id.iv_dislike);
        if (direction == CardConfig.SWIPING_LEFT) {
            ivDislike.setVisibility(View.GONE);
            ivLike.setVisibility(View.VISIBLE);
        } else if (direction == CardConfig.SWIPING_RIGHT) {
            ivDislike.setVisibility(View.VISIBLE);
            ivLike.setVisibility(View.GONE);
        } else {
            onReset();
        }
    }

    @Override
    public void onSwiped(View view, PairInfo pairInfo, int direction) {
        onReset();
        if (pairInfo.getOtherUserId() == 0)
            return;

        int likeOtherStatus = 1;
        if (direction == CardConfig.SWIPED_RIGHT) {
            canReturn = true;
            likeOtherStatus = 0;
            disLikeList.add(0, pairInfo);
        }
        if (superLikeStatus == 0 && likeOtherStatus == 1 && MineApp.mineInfo.getMemberType() == 1) {
            likeCount--;
            if (likeCount < 0)
                likeCount = 0;
            PreferenceUtil.saveIntValue(activity, "toLikeCount_" + BaseActivity.userId + "_" + DateUtil.getNow(DateUtil.yyyy_MM_dd), likeCount);
            updateCount(likeCount);
        }
        if (likeCount == 0 && likeOtherStatus == 1 && MineApp.mineInfo.getMemberType() == 1) {
            new VipAdPW(mBinding.getRoot(), 6, "");
            SCToastUtil.showToast(activity, "今日喜欢次数已用完", true);
            return;
        }
        if (superLikeStatus != 2)
            makeEvaluate(pairInfo, likeOtherStatus);
        superLikeStatus = 0;
    }

    private void updateCount(int likeCount) {
        if (likeCount >= 0) {
            mBinding.setLikeCount(likeCount);
            mBinding.setShowCount(likeCount > 0);
        }
    }

    @Override
    public void onSwipedClear() {
        prePairList(pairInfoList.size() == 0);
    }

    @Override
    public void onReset() {
        if (ivDislike != null)
            ivDislike.setVisibility(View.GONE);
        if (ivLike != null)
            ivLike.setVisibility(View.GONE);
        ivLike = null;
        ivDislike = null;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setCardAnimationLeftToRight(PairInfo pairInfo) {
        mBinding.setVariable(BR.pairInfo, pairInfo);
        imageList.clear();
        imageList.addAll(pairInfo.getImageList());
        disListAdapter.notifyDataSetChanged();

        PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat("rotation", 45, 0);
        PropertyValuesHolder pvhTX = PropertyValuesHolder.ofFloat("translationX", MineApp.W, 0);
        PropertyValuesHolder pvhA = PropertyValuesHolder.ofFloat("alpha", 0, 1);

        ObjectAnimator pvh_card = ObjectAnimator.ofPropertyValuesHolder(mBinding.cardRelative, pvhR, pvhTX, pvhA).setDuration(500);
        pvh_card.start();

        MineApp.getApp().getFixedThreadPool().execute(() -> {
            SystemClock.sleep(500);
            activity.runOnUiThread(() -> {
                canReturn = true;
                pairInfoList.add(0, pairInfo);
                adapter.notifyDataSetChanged();
            });
            SystemClock.sleep(100);
            mBinding.cardRelative.setAlpha(0f);
        });
    }

    // 省市信息
    private void initArea() {
        if (!AreaDb.getInstance().hasProvince()) {
            String data = SimulateNetAPI.getOriginalFundData(activity, "cityData.json");
            Runnable ra = () -> {
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
                activity.runOnUiThread(this::setLocation);
            };
            MineApp.getApp().getFixedThreadPool().execute(ra);
        } else {
            setLocation();
        }
    }

    private void setLocation() {
        if (checkPermissionGranted(activity, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)) {
            setLocation(1);
        } else {
            if (PreferenceUtil.readIntValue(activity, "locationPermission") == 0)
                new TextPW(activity, mBinding.getRoot(), "权限说明",
                        "当您浏览查看广场动态、广场视频、滑动卡片展示时，我们将会申请定位权限：" +
                                "\n 1、申请定位权限--设备所在位置相关信息（包括您授权的GPS位置以及WLAN接入点、蓝牙和基站等传感器信息），" +
                                "\n 2、若您点击“同意”按钮，我们方可正式申请上述权限，以便通过高德地图API获取经纬度及城市信息，也可通过左上角地址按钮进入地图，自主选择定位，" +
                                "\n 3、若您点击“拒绝”按钮，我们将不再主动弹出该提示，我们会获取全国范围内的动态、视频、卡片信息，不影响使用其他的虾菇功能/服务，" +
                                "\n 4、您也可以通过“手机设置--应用--虾菇--权限”或app内“我的--设置--权限管理--权限”，手动开启或关闭相机、存储、麦克风权限。",
                        "同意", false, true, new TextPW.CallBack() {
                    @Override
                    public void sure() {
                        PreferenceUtil.saveIntValue(activity, "locationPermission", 1);
                        getPermissions();
                    }

                    @Override
                    public void cancel() {
                        PreferenceUtil.saveIntValue(activity, "locationPermission", 1);
                        baseLocation();
                    }
                });
            else baseLocation();
        }
    }

    @Override
    public void myInfo() {
        myInfoApi api = new myInfoApi(new HttpOnNextListener<MineInfo>() {
            @Override
            public void onNext(MineInfo o) {
                MineApp.mineInfo = o;
                if (MineApp.mineInfo.getMemberType() == 2) {
                    mBinding.setShowCount(false);
                } else {
                    if (PreferenceUtil.readIntValue(activity, "toLikeCount_" + BaseActivity.userId + "_" + DateUtil.getNow(DateUtil.yyyy_MM_dd), -1) == -1)
                        likeCount = MineApp.mineInfo.getSurplusToDayLikeNumber();
                    else
                        likeCount = PreferenceUtil.readIntValue(activity, "toLikeCount_" + BaseActivity.userId + "_" + DateUtil.getNow(DateUtil.yyyy_MM_dd), -1);
                    updateCount(likeCount);
                }
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
                    setLocation(1);
                }

                @Override
                public void noPermission() {
                    PreferenceUtil.saveIntValue(activity, "locationPermission", 1);
                    baseLocation();
                }
            }, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            setLocation(1);
        }
    }

    private void setLocation(int type) {
        if (type == 1) {
            aMapLocation.start(activity, () -> {
                        mBinding.setCityName(PreferenceUtil.readStringValue(activity, "cityName"));
                        modifyMemberInfo();
                        joinPairPool(PreferenceUtil.readStringValue(activity, "longitude"),
                                PreferenceUtil.readStringValue(activity, "latitude"),
                                AreaDb.getInstance().getProvinceId(PreferenceUtil.readStringValue(activity, "provinceName")),
                                AreaDb.getInstance().getCityId(PreferenceUtil.readStringValue(activity, "cityName")),
                                AreaDb.getInstance().getDistrictId(PreferenceUtil.readStringValue(activity, "districtName")));
                    }
            );
        } else if (type == 2) {
            aMapLocation.start(activity, () -> {
                        CustomProgressDialog.stopLoading();
                        ActivityUtils.getMineLocation(false);
                    }
            );
        } else {
            ActivityUtils.getMineLocation(false);
        }
    }

    private void baseLocation() {
        PreferenceUtil.saveStringValue(activity, "longitude", "0");
        PreferenceUtil.saveStringValue(activity, "latitude", "0");
        PreferenceUtil.saveStringValue(activity, "cityName", "全国");
        PreferenceUtil.saveStringValue(activity, "provinceName", "全国");
        PreferenceUtil.saveStringValue(activity, "districtName", "全国");
        PreferenceUtil.saveStringValue(activity, "address", "全国");
        mBinding.setCityName("全国");
        joinPairPool("0", "0", 0, 0, 0);
        modifyMemberInfo();
    }

    private void modifyMemberInfo() {
        modifyMemberInfoForNoVerifyApi api = new modifyMemberInfoForNoVerifyApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
            }
        }, activity)
                .setProvinceId(AreaDb.getInstance().getProvinceId(PreferenceUtil.readStringValue(activity, "provinceName")))
                .setCityId(AreaDb.getInstance().getCityId(MineApp.cityName))
                .setDistrictId(AreaDb.getInstance().getDistrictId(PreferenceUtil.readStringValue(activity, "districtName")));
        api.setShowProgress(false);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
