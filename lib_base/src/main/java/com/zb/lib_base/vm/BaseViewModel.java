package com.zb.lib_base.vm;

import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.iv.BaseVMInterface;
import com.zb.lib_base.utils.DisplayUtils;

import java.lang.reflect.Field;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;

public class BaseViewModel implements BaseVMInterface {
    public ViewDataBinding mBinding;
    public AppCompatActivity activity;

    @Override
    public void setBinding(ViewDataBinding binding) {
        mBinding = binding;
        activity = (AppCompatActivity) mBinding.getRoot().getContext();
    }

    @Override
    public void back(View view) {

    }

    @Override
    public void right(View view) {

    }

    /**
     * 通过反射机制 修改TabLayout 的下划线长度
     */
    public void setIndicator(TabLayout tabLayout) {
        tabLayout.post(() -> {
            try {
                //拿到tabLayout的mTabStrip属性
                Field mTabStripField = tabLayout.getClass().getDeclaredField("slidingTabIndicator");
                mTabStripField.setAccessible(true);
                LinearLayout mTabStrip = (LinearLayout) mTabStripField.get(tabLayout);
                int dp10 = DisplayUtils.dip2px(activity, 10);
                for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                    View tabView = mTabStrip.getChildAt(i);
                    //拿到tabView的mTextView属性
                    Field mTextViewField = tabView.getClass().getDeclaredField("textView");
                    mTextViewField.setAccessible(true);
                    TextView mTextView = (TextView) mTextViewField.get(tabView);
                    tabView.setPadding(0, 0, 0, 0);
                    //因为我想要的效果是   字多宽线就多宽，所以测量mTextView的宽度
                    int width = 0;
                    width = mTextView.getWidth();
                    if (width == 0) {
                        mTextView.measure(0, 0);
                        width = mTextView.getMeasuredWidth();
                    }
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                    params.width = width;
                    params.leftMargin = dp10;
                    params.rightMargin = dp10;
                    tabView.setLayoutParams(params);
                    tabView.invalidate();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Android M运行时权限请求封装
     *
     * @param permissionDes 权限描述
     * @param runnable      请求权限回调
     * @param permissions   请求的权限（数组类型），直接从Manifest中读取相应的值，比如Manifest.permission.WRITE_CONTACTS
     */
    public void performCodeWithPermission(@NonNull String permissionDes, BaseActivity.PermissionCallback runnable, @NonNull String... permissions) {
        if (activity != null && activity instanceof BaseActivity) {
            ((BaseActivity) activity).performCodeWithPermission(permissionDes, runnable, permissions);
        }
    }
}
