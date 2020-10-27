package com.zb.module_mine.vm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.view.View;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.adapter.FragmentAdapter;
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
import com.zb.lib_base.windows.VipAdPW;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.databinding.MineFragBinding;
import com.zb.module_mine.iv.MineVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MineViewModel extends BaseViewModel implements MineVMInterface {

    public FragmentManager fm;
    private MineFragBinding mBinding;
    private BaseReceiver updateMineInfoReceiver;
    private BaseReceiver newsCountReceiver;
    private BaseReceiver openVipReceiver;
    private BaseReceiver updateChatTypeReceiver;
    private BaseReceiver visitorReceiver;
    private BaseReceiver attentionListReceiver;
    private List<Fragment> fragments = new ArrayList<>();

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (MineFragBinding) binding;
        playAnimator(mBinding.circleView);
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

        initFragments();

        new Handler().postDelayed(() -> {
            int height = DisplayUtils.dip2px(30) - mBinding.topLinear.getHeight();
            mBinding.appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) ->
                    mBinding.setShowBg(verticalOffset <= height));
        }, 300);
    }

    public void onResume() {
        mBinding.setMineInfo(MineApp.mineInfo);
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

    private void initFragments() {
        fragments.clear();
        fragments.add(FragmentUtils.getCardMemberDiscoverFragment(1));
        fragments.add(FragmentUtils.getCardMemberVideoFragment(1));
        mBinding.viewPage.setAdapter(new FragmentAdapter(fm, fragments));
        initTabLayout(new String[]{"动态", "小视频"}, mBinding.tabLayout, mBinding.viewPage, R.color.black_252, R.color.black_827, 0);
    }

    @Override
    public void publishDiscover(View view) {
        getPermissions();
    }

    @Override
    public void openVip(View view) {
        if (MineApp.vipInfoList.size() > 0)
            ActivityUtils.getMineOpenVip();
    }

    @Override
    public void openShare(View view) {
        ActivityUtils.getMineWeb("邀请好友赚钱", HttpManager.BASE_URL + "testShare.html" +
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
        if (mBinding.viewPage.getCurrentItem() == 1) {
            ActivityUtils.getCameraVideo(false);
        } else {
            ActivityUtils.getCameraMain(activity, true, true, false);
        }
    }
}
