package com.zb.lib_base.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.launcher.ARouter;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {
    private View rootView;
    public RxAppCompatActivity activity;
    public ViewDataBinding mBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (RxAppCompatActivity) getActivity();
        ARouter.getInstance().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            mBinding = DataBindingUtil.inflate(inflater, getRes(), container, false);
            rootView = mBinding.getRoot();
            initUI();
        }
        return rootView;
    }

    public abstract int getRes();

    public abstract void initUI();
}
