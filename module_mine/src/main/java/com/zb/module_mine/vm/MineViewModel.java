package com.zb.module_mine.vm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.view.View;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.adapter.ViewPagerAdapter;
import com.zb.lib_base.api.myInfoApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DisplayUtils;
import com.zb.lib_base.utils.FragmentUtils;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.SelectorPW;
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
    private List<Fragment> fragments = new ArrayList<>();
    private List<String> selectorList = new ArrayList<>();
    private boolean createFragment = false;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (MineFragBinding) binding;
        playAnimator(mBinding.circleView);
        goAnimator(mBinding.ivGo);
        selectorList.add("发布照片");
        selectorList.add("发布小视频");
        mBinding.setMineNewsCount(MineApp.mineNewsCount);
        mBinding.setContactNum(MineApp.contactNum);
        mBinding.setHasNewBeLike(MineApp.contactNum.getBeLikeCount() > PreferenceUtil.readIntValue(activity, "beLikeCount" + BaseActivity.userId));
        mBinding.setHasNewVisitor(MineApp.contactNum.getVisitorCount() > PreferenceUtil.readIntValue(activity, "visitorCount" + BaseActivity.userId));

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

        MineApp.getApp().getFixedThreadPool().execute(() -> {
            SystemClock.sleep(300);
            activity.runOnUiThread(() -> {
                int height = DisplayUtils.dip2px(30) - mBinding.topLinear.getHeight();
                mBinding.appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) ->
                        mBinding.setShowBg(verticalOffset <= height));
            });
        });
    }

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
    }

    @Override
    public void publishDiscover(View view) {
        getPermissions();
    }

    @Override
    public void openVip(View view) {
        if (MineApp.vipInfoList.size() > 0)
            ActivityUtils.getMineOpenVip(false);
    }

    @Override
    public void openShare(View view) {
        ActivityUtils.getMineWeb("邀请好友赚钱", HttpManager.BASE_URL + "mobile/Share_marketingInfo" +
                "?userId=" + BaseActivity.userId + "&sessionId=" + BaseActivity.sessionId +
                "&pfDevice=Android&pfAppType=203&pfAppVersion=" + MineApp.versionName);
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
    public void contactNumDetail(int position) {
        if (position == 2) {
            if (MineApp.mineInfo.getMemberType() == 2) {
                PreferenceUtil.saveIntValue(activity, "beLikeCount" + BaseActivity.userId, MineApp.contactNum.getBeLikeCount());
                mBinding.setVariable(BR.hasNewBeLike, false);
                ActivityUtils.getMineFCL(2);
                return;
            }
            new VipAdPW(mBinding.getRoot(), 4, "");
        } else {
            if (position == 3) {
                PreferenceUtil.saveIntValue(activity, "visitorCount" + BaseActivity.userId, MineApp.contactNum.getVisitorCount());
                mBinding.setVariable(BR.hasNewVisitor, false);
            }
            ActivityUtils.getMineFCL(position);
        }
    }

    @Override
    public void myInfo() {
        myInfoApi api = new myInfoApi(new HttpOnNextListener<MineInfo>() {
            @Override
            public void onNext(MineInfo o) {
                MineApp.mineInfo = o;
                mBinding.setMineInfo(MineApp.mineInfo);
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    /**
     * 权限
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            performCodeWithPermission("虾菇需要访问读写外部存储权限及相机权限", new BaseActivity.PermissionCallback() {
                        @Override
                        public void hasPermission() {
                            setPermissions();
                        }

                        @Override
                        public void noPermission() {
                        }
                    }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO);
        } else {
            setPermissions();
        }
    }

    private void setPermissions() {
        MineApp.toPublish = true;
        MineApp.toContinue = false;
        new SelectorPW(mBinding.getRoot(), selectorList, position1 -> {
            if (position1 == 0) {
                ActivityUtils.getCameraMain(activity, true, true, false);
            } else {
                ActivityUtils.getCameraVideo(false);
            }
        });
    }
}
