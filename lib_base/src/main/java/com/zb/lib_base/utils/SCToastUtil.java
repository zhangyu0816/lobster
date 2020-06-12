package com.zb.lib_base.utils;

import android.animation.ObjectAnimator;
import android.os.Handler;
import android.view.Gravity;
import android.widget.Toast;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.R;
import com.zb.lib_base.databinding.ToastViewBinding;

import androidx.databinding.DataBindingUtil;

public class SCToastUtil {
    private static Toast toast = null;
    private static ToastViewBinding mBinding;

    public static void showToast(RxAppCompatActivity activity, CharSequence text, boolean isTop) {
        mBinding = DataBindingUtil.inflate(activity.getLayoutInflater(), R.layout.toast_view, null, false);
        mBinding.setContent(text.toString());
        toast = new Toast(activity);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(mBinding.getRoot()); //添加视图文件
        if (isTop) {
            toast.setGravity(Gravity.TOP, 0, -DisplayUtils.dip2px(75f));
            ObjectAnimator translateY = ObjectAnimator.ofFloat(mBinding.toastLinear, "translationY", -DisplayUtils.dip2px(75f), 0).setDuration(500);
            translateY.start();
            new Handler().postDelayed(translateY::cancel, 2000);
        } else {
            toast.setGravity(Gravity.CENTER, 0, 0);
        }
        toast.show();
    }
}
