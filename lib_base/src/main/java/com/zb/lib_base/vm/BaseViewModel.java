package com.zb.lib_base.vm;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.R;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.iv.BaseVMInterface;
import com.zb.lib_base.utils.ObjectUtils;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.ViewDataBinding;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager2.widget.ViewPager2;

public class BaseViewModel implements BaseVMInterface {
    public ViewDataBinding mBinding;
    public RxAppCompatActivity activity;

    @Override
    public void setBinding(ViewDataBinding binding) {
        mBinding = binding;
        activity = (RxAppCompatActivity) mBinding.getRoot().getContext();
    }

    @Override
    public void back(View view) {
        MineApp.getApp().removeActivity(activity);
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
    public void visitMember(long userId) {

    }

    @Override
    public void question(View view) {
    }

    @Override
    public void setFilmType(View view) {

    }

    public void onDestroy() {

    }

    public boolean checkPermissionGranted(RxAppCompatActivity activity, String... permissions) {
        boolean flag = true;
        for (String p : permissions) {
            if (ActivityCompat.checkSelfPermission(activity, p) != PackageManager.PERMISSION_GRANTED) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    public void initTabLayout(String[] tabNames, TabLayout tabLayout, ViewPager2 viewPager, int selectColor, int color, int index, boolean isChatSelectIndex) {
        if (tabLayout == null)
            return;
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (tabNames[position].contains("-")) {
                String[] temp = tabNames[position].split("-");
                if (temp.length == 3)
                    tab.setCustomView(getTabView(temp[0], Boolean.parseBoolean(temp[1]), temp[2]));
                else
                    tab.setCustomView(getTabView(temp[0], Boolean.parseBoolean(temp[1]), ""));
            } else {
                tab.setCustomView(getTabView(tabNames[position], false, ""));
            }
        });
        tabLayoutMediator.attach();

        viewPager.setCurrentItem(index);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (isChatSelectIndex)
                    MineApp.chatSelectIndex = tab.getPosition();
                MineApp.filmSelectIndex = tab.getPosition();
                viewPager.setCurrentItem(tab.getPosition());
                changeTab(tab, 16, selectColor);
                if (tabNames[0].equals("??????")) {
                    Intent data = new Intent("lobster_homeBottle");
                    data.putExtra("index", tab.getPosition());
                    LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(data);
                } else if (tabNames[0].equals("??????")) {
                    Intent data = new Intent("lobster_myFilmPosition");
                    data.putExtra("index", tab.getPosition());
                    LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(data);
                }
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
            changeTab(Objects.requireNonNull(tabLayout.getTabAt(index)), 16, selectColor);
        } catch (Exception ignored) {

        }
    }

    // ??????????????????
    private void changeTab(TabLayout.Tab tab, int size, int color) {
        View view = tab.getCustomView();
        if (view instanceof RelativeLayout) {
            TextView textView = view.findViewById(R.id.tab_item_textview);
            // ?????? tab ??????????????????????????????
            textView.setTextSize(size);
            textView.setTextColor(ContextCompat.getColor(activity, color));
            TextPaint paint = textView.getPaint();
            paint.setFakeBoldText(size == 16);
        }
    }

    /**
     * ?????????Tab???View * @param currentPosition * @return
     */
    private View getTabView(String name, boolean showRed, String num) {
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_tab, null);
        TextView textView = view.findViewById(R.id.tab_item_textview);
        TextView tvRed = view.findViewById(R.id.tv_red);
        TextView tvRedNum = view.findViewById(R.id.tv_red_num);
        textView.setText(name);
        if (num.isEmpty())
            tvRed.setVisibility(showRed ? View.VISIBLE : View.GONE);
        else {
            tvRedNum.setText(num);
            tvRedNum.setVisibility(showRed ? View.VISIBLE : View.GONE);
        }
        return view;
    }

    private InputMethodManager imm;

    /**
     * ????????????
     */
    public void hintKeyBoard() {
        //??????InputMethodManager
        imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        //??????window???view???????????? && view?????????
        if (imm.isActive() && activity.getCurrentFocus() != null) {
            //??????view???token ?????????
            if (activity.getCurrentFocus().getWindowToken() != null) {
                //??????????????????????????????????????????????????????SHOW_FORCED?????????
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    public boolean isSoftShowing() {
        //??????????????????????????????
        int screenHeight = activity.getWindow().getDecorView().getHeight();
        //??????View???????????????bottom
        Rect rect = new Rect();
        //DecorView??????activity?????????view
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        //???????????????????????????????????????????????????????????????screenHeight = rect.bottom + ????????????????????????
        //??????screenHeight*2/3????????????
        return screenHeight * 2 / 3 > rect.bottom;
    }

    private PropertyValuesHolder pvhSY, pvhSX, pvhA, pvhR;
    private ObjectAnimator pvh;

    public void playAnimator(View view) {
        pvhSY = PropertyValuesHolder.ofFloat("scaleY", 0.5f, 1);
        pvhSX = PropertyValuesHolder.ofFloat("scaleX", 0.5f, 1);
        pvhA = PropertyValuesHolder.ofFloat("alpha", 1, 0);
        pvh = ObjectAnimator.ofPropertyValuesHolder(view, pvhSY, pvhSX, pvhA).setDuration(2000);
        pvh.setRepeatCount(Animation.INFINITE);
        pvh.start();
    }

    public void goAnimator(View view, float min, float max, long time) {
        pvhSY = PropertyValuesHolder.ofFloat("scaleY", min, max, min);
        pvhSX = PropertyValuesHolder.ofFloat("scaleX", min, max, min);
        pvh = ObjectAnimator.ofPropertyValuesHolder(view, pvhSY, pvhSX).setDuration(time);
        pvh.setRepeatCount(Animation.INFINITE);
        pvh.start();
    }

    public void stopGo() {
        if (pvh != null)
            pvh.cancel();
        pvh = null;
    }

    public void likeOrNot(View view) {
        pvhSY = PropertyValuesHolder.ofFloat("scaleY", 0, 1, 0.8f, 1);
        pvhSX = PropertyValuesHolder.ofFloat("scaleX", 0, 1, 0.8f, 1);
        pvh = ObjectAnimator.ofPropertyValuesHolder(view, pvhSY, pvhSX).setDuration(500);
        pvh.start();
        MineApp.getApp().getFixedThreadPool().execute(() -> {
            SystemClock.sleep(500);
            activity.runOnUiThread(() -> {
                if (pvh != null)
                    pvh.cancel();
                pvh = null;
            });
        });
    }

    public void isAttention(RelativeLayout layout, ImageView iv) {
        pvhR = PropertyValuesHolder.ofFloat("rotation", 0, 90);
        pvhA = PropertyValuesHolder.ofFloat("alpha", 1, 0);
        pvh = ObjectAnimator.ofPropertyValuesHolder(iv, pvhR, pvhA).setDuration(200);
        pvh.start();
        MineApp.getApp().getFixedThreadPool().execute(() -> {
            SystemClock.sleep(200);
            activity.runOnUiThread(() -> {
                iv.setBackgroundResource(R.drawable.attention_get_icon);
                pvhR = PropertyValuesHolder.ofFloat("rotation", 90, 0);
                pvh = ObjectAnimator.ofPropertyValuesHolder(iv, pvhR).setDuration(50);
                pvh.start();
            });
            SystemClock.sleep(50);
            activity.runOnUiThread(() -> {
                pvhA = PropertyValuesHolder.ofFloat("alpha", 0, 1);
                pvh = ObjectAnimator.ofPropertyValuesHolder(iv, pvhA).setDuration(100);
                pvh.start();
            });
            SystemClock.sleep(750);
            activity.runOnUiThread(() -> layout.setVisibility(View.INVISIBLE));
        });
    }

    private long exitTime = 0;
    private boolean isScroll = false;
    private Handler mHandler = new Handler();

    public void setScroll(boolean scroll) {
        isScroll = scroll;
    }

    /**
     * ?????????????????????
     */
    @SuppressLint("ClickableViewAccessibility")
    public void initGood(View clickView, View imageView, Runnable ra, Runnable successRa) {
        imageView.setRotation(45f);
        pvhSY = PropertyValuesHolder.ofFloat("scaleY", 2f, 1.8f, 2f, 2f, 3f);
        pvhSX = PropertyValuesHolder.ofFloat("scaleX", 2f, 1.8f, 2f, 2f, 3f);
        pvhA = PropertyValuesHolder.ofFloat("alpha", 1, 1, 1, 0.5f, 0);
        pvh = ObjectAnimator.ofPropertyValuesHolder(imageView, pvhSY, pvhSX, pvhA).setDuration(500);
        clickView.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if (!isScroll) {
                    if ((System.currentTimeMillis() - exitTime) > 500) {
                        exitTime = System.currentTimeMillis();
                        mHandler.postDelayed(ra, 500);
                    } else {
                        exitTime = 0;
                        mHandler.removeCallbacks(ra);
                        imageView.setX(motionEvent.getX() - ObjectUtils.getViewSizeByWidthFromMax(102));
                        imageView.setY(motionEvent.getY() - ObjectUtils.getViewSizeByWidthFromMax(102));
                        imageView.setAlpha(1f);
                        if (pvh != null)
                            pvh.start();
                        mHandler.postDelayed(successRa, 500);
                    }
                }
            }
            return true;
        });
    }

    /**
     * ????????????
     */
    public void closeImplicit(View view) {
        imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0); // ??????????????????
    }

    public void showImplicit(View view) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);
    }

    /**
     * Android M???????????????????????????
     *
     * @param permissionDes ????????????
     * @param runnable      ??????????????????
     * @param permissions   ?????????????????????????????????????????????Manifest??????????????????????????????Manifest.permission.WRITE_CONTACTS
     */
    public void performCodeWithPermission(@NonNull String permissionDes, BaseActivity.PermissionCallback runnable, @NonNull String... permissions) {
        if (activity != null && activity instanceof BaseActivity) {
            ((BaseActivity) activity).performCodeWithPermission(permissionDes, runnable, permissions);
        }
    }
}
