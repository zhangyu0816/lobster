package com.zb.lib_base.utils;

import android.view.Gravity;
import android.widget.Toast;

import com.zb.lib_base.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;

public class SCToastUtil {
    private static Toast toast = null;
    private static ViewDataBinding mBinding;

    public static void showToast(AppCompatActivity activity, CharSequence text) {
        mBinding = DataBindingUtil.inflate(activity.getLayoutInflater(), R.layout.toast_view, null, false);
        mBinding.setVariable(BR.content, text.toString());
        if (toast == null) {
            toast = new Toast(activity);
        }
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(mBinding.getRoot()); //添加视图文件
        toast.setGravity(Gravity.TOP, 0, 100);
        toast.show();
    }

}
