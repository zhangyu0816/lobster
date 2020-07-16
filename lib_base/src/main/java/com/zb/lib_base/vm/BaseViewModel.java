package com.zb.lib_base.vm;

import android.content.Context;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.R;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.db.AttentionDb;
import com.zb.lib_base.db.GoodDb;
import com.zb.lib_base.db.MineInfoDb;
import com.zb.lib_base.iv.BaseVMInterface;
import com.zb.lib_base.windows.BottleQuestionPW;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.ViewDataBinding;
import androidx.viewpager.widget.ViewPager;
import io.realm.Realm;

public class BaseViewModel implements BaseVMInterface {
    public ViewDataBinding mBinding;
    public RxAppCompatActivity activity;
    public MineInfoDb mineInfoDb;
    public GoodDb goodDb;
    public AttentionDb attentionDb;

    @Override
    public void setBinding(ViewDataBinding binding) {
        mBinding = binding;
        activity = (RxAppCompatActivity) mBinding.getRoot().getContext();
        if (mineInfoDb == null)
            mineInfoDb = new MineInfoDb(Realm.getDefaultInstance());
        if (goodDb == null)
            goodDb = new GoodDb(Realm.getDefaultInstance());
        if (attentionDb == null)
            attentionDb = new AttentionDb(Realm.getDefaultInstance());
    }

    @Override
    public void back(View view) {

    }

    @Override
    public void right(View view) {

    }

    @Override
    public void more(View view) {

    }

    @Override
    public void follow(View view) {

    }

    @Override
    public void superLike(View view) {

    }

    @Override
    public void selectPosition(int position) {

    }

    @Override
    public void question(View view) {
        new BottleQuestionPW(activity, mBinding.getRoot());
    }

    public void initTabLayout(String[] tabNames, TabLayout tabLayout, ViewPager viewPager, int selectColor, int color) {
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(getTabView(tabNames[i]));
            }
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                changeTab(tab, 18, selectColor);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                changeTab(tab, 14, color);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        changeTab(Objects.requireNonNull(tabLayout.getTabAt(0)), 18, selectColor);
    }

    // 改变选中状态
    private void changeTab(TabLayout.Tab tab, int size, int color) {
        View view = tab.getCustomView();
        if (view instanceof TextView) {
            // 改变 tab 选择状态下的字体大小
            ((TextView) view).setTextSize(size);
            ((TextView) view).setTextColor(ContextCompat.getColor(activity, color));
        }
    }

    /**
     * 自定义Tab的View * @param currentPosition * @return
     */
    private View getTabView(String name) {
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_tab, null);
        TextView textView = view.findViewById(R.id.tab_item_textview);
        textView.setText(name);
        return view;
    }

    private InputMethodManager imm;

    /**
     * 隐藏键盘
     */
    public void hintKeyBoard() {
        //拿到InputMethodManager
        imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        //如果window上view获取焦点 && view不为空
        if (imm.isActive() && activity.getCurrentFocus() != null) {
            //拿到view的token 不为空
            if (activity.getCurrentFocus().getWindowToken() != null) {
                //表示软键盘窗口总是隐藏，除非开始时以SHOW_FORCED显示。
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * 打开软键盘
     *
     * @param v
     */

    public void showImplicit(View v) {
        imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 隐藏键盘
     */
    public void closeImplicit(View view) {
        imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0); // 强制隐藏键盘
    }

    public static void setProhibitEmoji(EditText et) {
        InputFilter[] filters = {getInputFilterProhibitEmoji()};
        et.setFilters(filters);
    }

    public static InputFilter getInputFilterProhibitEmoji() {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                StringBuffer buffer = new StringBuffer();
                for (int i = start; i < end; i++) {
                    char codePoint = source.charAt(i);
                    if (!getIsEmoji(codePoint)) {
                        buffer.append(codePoint);
                    } else {
                        i++;
                        continue;
                    }
                }
                if (source instanceof Spanned) {
                    SpannableString sp = new SpannableString(buffer);
                    TextUtils.copySpansFrom((Spanned) source, start, end, null, sp, 0);
                    return sp;
                } else {
                    return buffer;
                }
            }
        };
        return filter;
    }

    public static boolean getIsEmoji(char codePoint) {
        if ((codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)))
            return false;
        return true;
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


    /********************** 公用网络请求 ***************************/

}
