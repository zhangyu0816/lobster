package com.zb.lib_base.vm;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Handler;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.R;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.AttentionDb;
import com.zb.lib_base.db.GoodDb;
import com.zb.lib_base.db.MineInfoDb;
import com.zb.lib_base.iv.BaseVMInterface;
import com.zb.lib_base.windows.BottleQuestionPW;

import java.io.IOException;

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

    public void initTabLayout(String[] tabNames, TabLayout tabLayout, ViewPager viewPager, int selectColor, int color, int index) {
        if (tabLayout == null)
            return;
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(index);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                if (tabNames[i].contains("-")) {
                    String[] temp = tabNames[i].split("-");
                    tab.setCustomView(getTabView(temp[0], Boolean.parseBoolean(temp[1])));
                } else {
                    tab.setCustomView(getTabView(tabNames[i], false));
                }

            }
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                MineApp.chatSelectIndex = tab.getPosition();
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
        try {
            changeTab(tabLayout.getTabAt(index), 18, selectColor);
        } catch (Exception e) {

        }
    }

    // 改变选中状态
    private void changeTab(TabLayout.Tab tab, int size, int color) {
        View view = tab.getCustomView();
        if (view instanceof RelativeLayout) {
            TextView textView = view.findViewById(R.id.tab_item_textview);
            // 改变 tab 选择状态下的字体大小
            textView.setTextSize(size);
            textView.setTextColor(ContextCompat.getColor(activity, color));
        }
    }

    /**
     * 自定义Tab的View * @param currentPosition * @return
     */
    private View getTabView(String name, boolean showRed) {
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_tab, null);
        TextView textView = view.findViewById(R.id.tab_item_textview);
        TextView tvRed = view.findViewById(R.id.tv_red);
        textView.setText(name);
        tvRed.setVisibility(showRed ? View.VISIBLE : View.GONE);
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

    public boolean isSoftShowing() {
        //获取当屏幕内容的高度
        int screenHeight = activity.getWindow().getDecorView().getHeight();
        //获取View可见区域的bottom
        Rect rect = new Rect();
        //DecorView即为activity的顶级view
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        //考虑到虚拟导航栏的情况（虚拟导航栏情况下：screenHeight = rect.bottom + 虚拟导航栏高度）
        //选取screenHeight*2/3进行判断
        return screenHeight * 2 / 3 > rect.bottom;
    }

    public void playAnimator(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0.5f, 1).setDuration(2000);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0.5f, 1).setDuration(2000);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 1, 0).setDuration(2000);

        scaleX.setRepeatCount(2000);
        scaleX.setRepeatMode(ValueAnimator.RESTART);

        scaleY.setRepeatCount(2000);
        scaleY.setRepeatMode(ValueAnimator.RESTART);

        alpha.setRepeatCount(2000);
        alpha.setRepeatMode(ValueAnimator.RESTART);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.playTogether(scaleX, scaleY, alpha);//同时执行
        animatorSet.start();
    }

    private long time = 200;
    private float scale = 1.4f;

    public interface CallBack {
        void success();
    }

    public void like(View view, CallBack callBack) {

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1, scale).setDuration(time);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1, scale).setDuration(time);
        ObjectAnimator scaleBackX = ObjectAnimator.ofFloat(view, "scaleX", scale, 1).setDuration(100);
        ObjectAnimator scaleBackY = ObjectAnimator.ofFloat(view, "scaleY", scale, 1).setDuration(100);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.play(scaleX).with(scaleY);
        animatorSet.play(scaleBackX).with(scaleBackY).after(scaleX);
        animatorSet.start();

        new Handler().postDelayed(callBack::success, time + 100);
    }

    public void unlike(View view, CallBack callBack) {

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1, 0).setDuration(time);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1, 0).setDuration(time);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.play(scaleX).with(scaleY);
        animatorSet.start();

        new Handler().postDelayed(callBack::success, time);
    }

    public void openBottle() {
        // 播放声音
        MediaPlayer mPlayer = MediaPlayer.create(activity, R.raw.open_bottle);
        try {
            if (mPlayer != null) {
                mPlayer.stop();
            }
            mPlayer.prepare();
            mPlayer.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
