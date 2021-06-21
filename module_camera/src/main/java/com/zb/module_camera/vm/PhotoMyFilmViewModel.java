package com.zb.module_camera.vm;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.adapter.ViewPagerAdapter;
import com.zb.lib_base.api.myInfoApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.utils.FragmentUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_camera.R;
import com.zb.module_camera.databinding.AcPhotoMyFilmBinding;
import com.zb.module_camera.iv.PhotoMyFilmVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;

public class PhotoMyFilmViewModel extends BaseViewModel implements PhotoMyFilmVMInterface {

    private AcPhotoMyFilmBinding mBinding;
    private List<Fragment> fragments = new ArrayList<>();
    private ViewPagerAdapter adapter;
    private BaseReceiver readFilmMsgReceiver;
    public int filmMsgCount = 0;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (AcPhotoMyFilmBinding) binding;
        myInfo();
        readFilmMsgReceiver = new BaseReceiver(activity, "lobster_readFilmMsg") {
            @Override
            public void onReceive(Context context, Intent intent) {
                filmMsgCount--;
                if (filmMsgCount > 0)
                    initTabLayout(new String[]{"胶卷", "消息-true-" + filmMsgCount}, mBinding.tabLayout, mBinding.viewPage, R.color.black_252, R.color.black_827, 1, false);
                else
                    initTabLayout(new String[]{"胶卷", "消息"}, mBinding.tabLayout, mBinding.viewPage, R.color.black_252, R.color.black_827, 1, false);
            }
        };
        initFragments();
        if (filmMsgCount > 0)
            initTabLayout(new String[]{"胶卷", "消息-true-" + filmMsgCount}, mBinding.tabLayout, mBinding.viewPage, R.color.black_252, R.color.black_827, 0, false);
        else
            initTabLayout(new String[]{"胶卷", "消息"}, mBinding.tabLayout, mBinding.viewPage, R.color.black_252, R.color.black_827, 0, false);
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            readFilmMsgReceiver.unregisterReceiver();
        } catch (Exception e) {
            e.printStackTrace();
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

    private void initFragments() {
        fragments.clear();
        fragments.add(FragmentUtils.getFilmFragment());
        fragments.add(FragmentUtils.getFilmNewsFragment());
        adapter = new ViewPagerAdapter(activity.getSupportFragmentManager(), new Lifecycle() {
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
        mBinding.viewPage.setCurrentItem(0);

    }
}
