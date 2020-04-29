package com.zb.lib_base.utils;

import android.view.Gravity;
import android.widget.Toast;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.BR;
import com.zb.lib_base.R;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

public class SCToastUtil {
    private static Toast toast = null;
    private static ViewDataBinding mBinding;

    public static void showToast(RxAppCompatActivity activity, CharSequence text) {
        mBinding = DataBindingUtil.inflate(activity.getLayoutInflater(), R.layout.toast_view, null, false);
        mBinding.setVariable(BR.content, text.toString());
        toast = new Toast(activity);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(mBinding.getRoot()); //添加视图文件
        toast.setGravity(Gravity.TOP, 0, 100);
        toast.show();
    }

    public static void showToastBlack(RxAppCompatActivity activity, CharSequence text) {
        mBinding = DataBindingUtil.inflate(activity.getLayoutInflater(), R.layout.toast_view_black, null, false);
        mBinding.setVariable(BR.content, text.toString());
        toast = new Toast(activity);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(mBinding.getRoot()); //添加视图文件
        toast.setGravity(Gravity.TOP, 0, 100);
        toast.show();
    }

}
