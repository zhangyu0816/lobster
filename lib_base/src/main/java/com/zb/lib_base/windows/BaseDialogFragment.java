package com.zb.lib_base.windows;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.R;

import java.lang.reflect.Field;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public abstract class BaseDialogFragment extends DialogFragment {

    public RxAppCompatActivity activity;

    private boolean mIsKeyCanBack = true;
    private boolean mIsOutCanBack = true;

    private CallBack callBack;

    public BaseDialogFragment(RxAppCompatActivity activity) {
        this.activity = activity;
    }

    public BaseDialogFragment(RxAppCompatActivity activity, boolean isKeyCanBack, boolean isOutCanBack) {
        this.activity = activity;
        mIsKeyCanBack = isKeyCanBack;
        mIsOutCanBack = isOutCanBack;
    }

    public BaseDialogFragment setCallBack(CallBack callBack) {
        this.callBack = callBack;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setAnim();
        setCanCancel();
        ViewDataBinding dataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        setDataBinding(dataBinding);
        initUI();
        return dataBinding.getRoot();
    }

    protected void setAnim() {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.Animation_Dialog;
    }

    protected Window win;

    @Override
    public void onStart() {
        super.onStart();

        win = getDialog().getWindow();
        // 一定要设置Background，如果不设置，window属性设置无效
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        WindowManager.LayoutParams params = win.getAttributes();
        params.gravity = Gravity.BOTTOM;
        // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        win.setAttributes(params);
    }

    public void center() {
        center(0.75);
    }

    public void cleanPadding() {
        win.getDecorView().setPadding(0, 0, 0, 0);
    }

    public void center(double ratio) {
        Dialog dialog = getDialog();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(params);
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * ratio), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    /**
     * 设置 可取消相关
     */
    private void setCanCancel() {
        //弹出框外面是否可取消
        getDialog().setCanceledOnTouchOutside(mIsOutCanBack);
        getDialog().setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (callBack != null)
                    callBack.onFinish();
                return !mIsKeyCanBack;                                      //return true 不往上传递则关闭不了，默认是可以取消，即return false
            } else {
                return false;
            }
        });
    }

    public abstract int getLayoutId();

    public abstract void setDataBinding(ViewDataBinding viewDataBinding);

    public abstract void initUI();

    public void onAction(int index) {
    }

    public void showAllowingStateLoss(FragmentManager manager, String tag) {
        try {
            Field dismissed = DialogFragment.class.getDeclaredField("mDismissed");
            dismissed.setAccessible(true);
            dismissed.set(this, false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        try {
            Field shown = DialogFragment.class.getDeclaredField("mShownByMe");
            shown.setAccessible(true);
            shown.set(this, true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();

    }

    public interface CallBack {
        void onFinish();
    }

}
