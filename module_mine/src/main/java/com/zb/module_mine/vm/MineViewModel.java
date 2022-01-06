package com.zb.module_mine.vm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.adapter.ViewPagerAdapter;
import com.zb.lib_base.api.getAiApi;
import com.zb.lib_base.api.myInfoApi;
import com.zb.lib_base.api.personOtherDynApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.DiscoverInfo;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DisplayUtils;
import com.zb.lib_base.utils.FragmentUtils;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.SelectorPW;
import com.zb.lib_base.windows.TextPW;
import com.zb.lib_base.windows.VipAdPW;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.databinding.MineFragBinding;
import com.zb.module_mine.iv.MineVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;

public class MineViewModel extends BaseViewModel implements MineVMInterface {

    private MineFragBinding mBinding;
    private BaseReceiver updateMineInfoReceiver;
    private BaseReceiver newsCountReceiver;
    private BaseReceiver openVipReceiver;
    private BaseReceiver updateChatTypeReceiver;
    private BaseReceiver visitorReceiver;
    private BaseReceiver attentionListReceiver;
    private BaseReceiver isFirstOpenReceiver;
    private BaseReceiver publishReceiver;
    private List<Fragment> fragments = new ArrayList<>();
    private List<String> selectorList = new ArrayList<>();
    private boolean createFragment = false;
    private String[] per;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (MineFragBinding) binding;
        playAnimator(mBinding.circleView);
        goAnimator(mBinding.ivGo, 0.8f, 1.0f, 800L);
        selectorList.add("发布照片");
        selectorList.add("发布小视频");
        mBinding.setMineNewsCount(MineApp.mineNewsCount);
        mBinding.setContactNum(MineApp.contactNum);
        mBinding.setHasNewBeLike(MineApp.contactNum.getBeLikeCount() > PreferenceUtil.readIntValue(activity, "beLikeCount" + BaseActivity.userId));
        mBinding.setHasNewVisitor(MineApp.contactNum.getVisitorCount() > PreferenceUtil.readIntValue(activity, "visitorCount" + BaseActivity.userId));
        mBinding.setShowAi(false);

        updateMineInfoReceiver = new BaseReceiver(activity, "lobster_updateMineInfo") {
            @Override
            public void onReceive(Context context, Intent intent) {
                mBinding.setMineInfo(MineApp.mineInfo);
            }
        };
        newsCountReceiver = new BaseReceiver(activity, "lobster_newsCount") {
            @Override
            public void onReceive(Context context, Intent intent) {
                mBinding.setMineNewsCount(MineApp.mineNewsCount);
            }
        };

        // 开通会员
        openVipReceiver = new BaseReceiver(activity, "lobster_openVip") {
            @Override
            public void onReceive(Context context, Intent intent) {
                myInfo();
            }
        };

        updateChatTypeReceiver = new BaseReceiver(activity, "lobster_updateChatType") {
            @Override
            public void onReceive(Context context, Intent intent) {
                int chatType = intent.getIntExtra("chatType", 0);
                if (chatType == 1) {
                    mBinding.setContactNum(MineApp.contactNum);
                    mBinding.setHasNewBeLike(MineApp.contactNum.getBeLikeCount() > PreferenceUtil.readIntValue(activity, "beLikeCount" + BaseActivity.userId));
                }
            }
        };

        visitorReceiver = new BaseReceiver(activity, "lobster_visitor") {
            @Override
            public void onReceive(Context context, Intent intent) {
                mBinding.setContactNum(MineApp.contactNum);
                mBinding.setHasNewVisitor(MineApp.contactNum.getVisitorCount() > PreferenceUtil.readIntValue(activity, "visitorCount" + BaseActivity.userId));
            }
        };

        attentionListReceiver = new BaseReceiver(activity, "lobster_attentionList") {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isAdd = intent.getBooleanExtra("isAdd", false);
                MineApp.contactNum.setConcernCount(isAdd ? (MineApp.contactNum.getConcernCount() + 1) : (MineApp.contactNum.getConcernCount() - 1));
                mBinding.setContactNum(MineApp.contactNum);
            }
        };
        isFirstOpenReceiver = new BaseReceiver(activity, "lobster_isFirstOpen") {
            @Override
            public void onReceive(Context context, Intent intent) {
                mBinding.setIsFirstOpen(MineApp.isFirstOpen);
                mBinding.vipTitle.setVisibility(View.GONE);
                mBinding.vipInfo.setVisibility(View.GONE);
                if (!MineApp.isFirstOpen) {
                    mBinding.vipTitle.setVisibility(View.VISIBLE);
                    mBinding.vipInfo.setVisibility(View.VISIBLE);
                    mBinding.vipLinear.setBackgroundResource(R.drawable.mine_share_item_bg);
                } else
                    mHandler.postDelayed(ra, 500);
            }
        };
        publishReceiver = new BaseReceiver(activity, "lobster_publish") {
            @Override
            public void onReceive(Context context, Intent intent) {
                personOtherDyn();
            }
        };
        MineApp.getApp().getFixedThreadPool().execute(() -> {
            SystemClock.sleep(300);
            activity.runOnUiThread(() -> {
                int height = DisplayUtils.dip2px(30) - mBinding.topLinear.getHeight();
                mBinding.appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) ->
                        mBinding.setShowBg(verticalOffset <= height));
            });
        });
        mBinding.setIsFirstOpen(MineApp.isFirstOpen);
        mBinding.vipTitle.setVisibility(View.GONE);
        mBinding.vipInfo.setVisibility(View.GONE);
        if (!MineApp.isFirstOpen) {
            mBinding.vipTitle.setVisibility(View.VISIBLE);
            mBinding.vipInfo.setVisibility(View.VISIBLE);
            mBinding.vipLinear.setBackgroundResource(R.drawable.mine_share_item_bg);
        } else
            mHandler.postDelayed(ra, 500);

        getAi();
    }

    private Handler mHandler = new Handler();
    private Runnable ra = new Runnable() {
        @Override
        public void run() {
            index++;
            if (index % 2 == 0) {
                mBinding.vipLinear.setBackgroundResource(R.drawable.mine_big_vip_item_bg);
            } else
                mBinding.vipLinear.setBackgroundResource(R.drawable.mine_vip_item_bg);
            mHandler.postDelayed(ra, 500);
        }
    };
    private int index = 1;

    public void onResume() {
        mBinding.setMineInfo(MineApp.mineInfo);
        if (!createFragment) {
            createFragment = true;
            initFragments();
        }
    }

    public void onDestroy() {
        try {
            updateMineInfoReceiver.unregisterReceiver();
            newsCountReceiver.unregisterReceiver();
            openVipReceiver.unregisterReceiver();
            updateChatTypeReceiver.unregisterReceiver();
            visitorReceiver.unregisterReceiver();
            attentionListReceiver.unregisterReceiver();
            isFirstOpenReceiver.unregisterReceiver();
            publishReceiver.unregisterReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onStart() {
        if (adapter != null) {
            adapter.notifyItemChanged(mBinding.viewPage.getCurrentItem());
        }
    }

    private ViewPagerAdapter adapter;
    public FragmentManager fm;

    private void initFragments() {
        fragments.clear();
        fragments.add(FragmentUtils.getCardMemberDiscoverFragment(1));
        fragments.add(FragmentUtils.getCardMemberVideoFragment(1));

        adapter = new ViewPagerAdapter(fm, new Lifecycle() {
            @Override
            public void addObserver(@NonNull LifecycleObserver observer) {

            }

            @Override
            public void removeObserver(@NonNull LifecycleObserver observer) {

            }

            @NonNull
            @Override
            public State getCurrentState() {
                return null;
            }
        }, fragments);
        mBinding.viewPage.setSaveEnabled(false);
        mBinding.viewPage.setAdapter(adapter);
        initTabLayout(new String[]{"动态", "小视频"}, mBinding.tabLayout, mBinding.viewPage, R.color.black_252, R.color.black_827, 0, false);
        personOtherDyn();
    }

    private void personOtherDyn() {
        personOtherDynApi api = new personOtherDynApi(new HttpOnNextListener<List<DiscoverInfo>>() {
            @Override
            public void onNext(List<DiscoverInfo> o) {
                mBinding.setShowSubmit(true);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    mBinding.setShowSubmit(false);
                }
            }
        }, activity)
                .setDynType(0)
                .setOtherUserId(BaseActivity.userId)
                .setPageNo(1);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void getAi() {
        getAiApi api = new getAiApi(new HttpOnNextListener<Integer>() {
            @Override
            public void onNext(Integer o) {
                mBinding.setShowAi(o == 1);
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void publishDiscover(View view) {
        per = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};
        if (checkPermissionGranted(activity, per)) {
            setPermissions(1);
        } else {
            if (PreferenceUtil.readIntValue(activity, "publishPermission") == 0) {
                PreferenceUtil.saveIntValue(activity, "publishPermission", 1);
                new TextPW(activity, mBinding.getRoot(), "权限说明",
                        "在使用发布动态功能，包括图文、视频时，我们将会申请相机、存储、麦克风权限：" +
                                "\n 1、申请相机权限--发布动态时获取拍摄照片，录制视频功能，" +
                                "\n 2、申请存储权限--发布动态时获取保存和读取图片、视频，" +
                                "\n 3、申请麦克风权限--发布视频动态时获取录制视频音频功能，" +
                                "\n 4、若您点击“同意”按钮，我们方可正式申请上述权限，以便正常发布图文动态、视频动态，" +
                                "\n 5、若您点击“拒绝”按钮，我们将不再主动弹出该提示，您也无法使用发布动态功能，不影响使用其他的虾菇功能/服务，" +
                                "\n 6、您也可以通过“手机设置--应用--虾菇--权限”或app内“我的--设置--权限管理--权限”，手动开启或关闭相机、存储、麦克风权限。",
                        "同意", false, true, () -> getPermissions(1));
            } else {
                if (!checkPermissionGranted(activity, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    SCToastUtil.showToast(activity, "你未开启存储权限，请前往我的--设置--权限管理--权限进行设置", true);
                } else if (!checkPermissionGranted(activity, Manifest.permission.CAMERA)) {
                    SCToastUtil.showToast(activity, "你未开启相机权限，请前往我的--设置--权限管理--权限进行设置", true);
                } else if (!checkPermissionGranted(activity, Manifest.permission.RECORD_AUDIO)) {
                    SCToastUtil.showToast(activity, "你未开启麦克风权限，请前往我的--设置--权限管理--权限进行设置", true);
                }
            }
        }
    }

    @Override
    public void openVip(View view) {
        if (MineApp.vipInfoList.size() > 0)
            ActivityUtils.getMineOpenVip(false);
    }

    @Override
    public void openShare(int index) {
        if (index == 1 || !MineApp.isFirstOpen) {
            ActivityUtils.getMineWeb("邀请好友赚钱", HttpManager.BASE_URL + "mobile/Share_marketingInfo" +
                    "?userId=" + BaseActivity.userId + "&sessionId=" + BaseActivity.sessionId +
                    "&pfDevice=Android&pfAppType=203&pfAppVersion=" + MineApp.versionName);
        } else {
            new VipAdPW(mBinding.getRoot(), 100, "");
        }
    }

    @Override
    public void toEditMember(View view) {
        ActivityUtils.getMineEditMember();
    }

    @Override
    public void toNews(View view) {
        ActivityUtils.getMineNewsManager();
    }

    @Override
    public void toSetting(View view) {
        ActivityUtils.getMineSetting();
    }

    @Override
    public void toReward(View view) {
        ActivityUtils.getHomeRewardList(0, BaseActivity.userId);
    }

    @Override
    public void toPhotoStudio(View view) {
        per = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (checkPermissionGranted(activity, per)) {
            setPermissions(2);
        } else {
            if (PreferenceUtil.readIntValue(activity, "photoPermission") == 0) {
                PreferenceUtil.saveIntValue(activity, "photoPermission", 1);
                new TextPW(activity, mBinding.getRoot(), "权限说明",
                        "在使用照相馆功能时，我们将会申请相机、存储权限：" +
                                "\n 1、申请相机权限--绘制胶卷时获取拍摄照片功能，" +
                                "\n 2、申请存储权限--绘制胶卷时获取读取手机图片库功能，" +
                                "\n 3、若您点击“同意”按钮，我们方可正式申请上述权限，以便拍摄照片及选取照片，绘制图片胶卷，" +
                                "\n 4、若您点击“拒绝”按钮，我们将不再主动弹出该提示，您也无法使用照相馆功能，不影响使用其他的虾菇功能/服务，" +
                                "\n 5、您也可以通过“手机设置--应用--虾菇--权限”或app内“我的--设置--权限管理--权限”，手动开启或关闭相机、存储权限。",
                        "同意", false, true, () -> getPermissions(2));
            } else {
                if (!checkPermissionGranted(activity, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    SCToastUtil.showToast(activity, "你未开启存储权限，请前往我的--设置--权限管理--权限进行设置", true);
                } else if (!checkPermissionGranted(activity, Manifest.permission.CAMERA)) {
                    SCToastUtil.showToast(activity, "你未开启相机权限，请前往我的--设置--权限管理--权限进行设置", true);
                }
            }
        }
    }

    @Override
    public void toLove(View view) {
        ActivityUtils.getLoveHome();
    }

    @Override
    public void contactNumDetail(int position) {
        if (position == 2) {
            if (MineApp.mineInfo.getMemberType() == 2) {
                PreferenceUtil.saveIntValue(activity, "beLikeCount" + BaseActivity.userId, MineApp.contactNum.getBeLikeCount());
                mBinding.setVariable(BR.hasNewBeLike, false);
                ActivityUtils.getMineFCL(2, 0);
                return;
            }
            new VipAdPW(mBinding.getRoot(), 4, "");
        } else if (position == 4) {
            ActivityUtils.getMineNewsList(2);
        } else {
            if (position == 3) {
                PreferenceUtil.saveIntValue(activity, "visitorCount" + BaseActivity.userId, MineApp.contactNum.getVisitorCount());
                mBinding.setVariable(BR.hasNewVisitor, false);
            }
            ActivityUtils.getMineFCL(position, 0);
        }
    }

    @Override
    public void myInfo() {
        myInfoApi api = new myInfoApi(new HttpOnNextListener<MineInfo>() {
            @Override
            public void onNext(MineInfo o) {
                MineApp.mineInfo = o;
                mBinding.setMineInfo(MineApp.mineInfo);
                mBinding.vipTitle.setVisibility(View.GONE);
                mBinding.vipInfo.setVisibility(View.GONE);
                if (MineApp.mineInfo.getMemberType() == 2) {
                    MineApp.isFirstOpen = false;
                    mBinding.setIsFirstOpen(MineApp.isFirstOpen);
                    if (!MineApp.isFirstOpen) {
                        mBinding.vipTitle.setVisibility(View.VISIBLE);
                        mBinding.vipInfo.setVisibility(View.VISIBLE);
                    }
                    mHandler.removeCallbacks(ra);
                    mBinding.vipLinear.setBackgroundResource(R.drawable.mine_share_item_bg);
                }
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    /**
     * 权限
     */
    private void getPermissions(int type) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            performCodeWithPermission("虾菇需要访问存储权限及相机权限", new BaseActivity.PermissionCallback() {
                @Override
                public void hasPermission() {
                    setPermissions(type);
                }

                @Override
                public void noPermission() {
                    if (type == 1)
                        PreferenceUtil.saveIntValue(activity, "publishPermission", 1);
                    else
                        PreferenceUtil.saveIntValue(activity, "photoPermission", 1);
                }
            }, per);
        } else {
            setPermissions(type);
        }
    }

    private void setPermissions(int type) {
        if (type == 1) {
            MineApp.toPublish = true;
            MineApp.toContinue = false;
            new SelectorPW(mBinding.getRoot(), selectorList, position1 -> {
                if (position1 == 0) {
                    ActivityUtils.getCameraMain(activity, true, true, false);
                } else {
                    ActivityUtils.getCameraVideo(false);
                }
            });
        } else {
            ActivityUtils.getCameraPhotoStudio();
        }
    }
}
