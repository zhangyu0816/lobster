package com.zb.module_register.windows;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.telecom.Call;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.DateUtil;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.utils.widget.OnWheelScrollListener;
import com.zb.lib_base.utils.widget.WheelView;
import com.zb.lib_base.utils.widget.adapters.AbstractWheelTextAdapter;
import com.zb.lib_base.windows.BasePopupWindow;
import com.zb.module_register.R;
import com.zb.module_register.databinding.PwsBirthdayBinding;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

public class BirthdayPW extends BasePopupWindow {
    private PwsBirthdayBinding binding;
    private View mParentView;
    private CallBack mCallBack;

    // 配置滑轮
    private int MAX_TEXT_SIZE = 20;
    private int MIN_TEXT_SIZE = 16;

    private int mYear = 0;
    private int mMonth = 0;
    private int mDay = 0;

    private ArrayList<String> yearList = new ArrayList<>();
    private ArrayList<String> monthList = new ArrayList<>();
    private ArrayList<String> dayList = new ArrayList<>();

    private String selectDate = "";
    private String nowDate = "";

    private int uiRes() {
        return R.layout.pws_birthday;
    }

    private void setBinding(ViewDataBinding viewDataBinding) {
        binding = (PwsBirthdayBinding) viewDataBinding;
    }

    public BirthdayPW(AppCompatActivity activity, View parentView, CallBack callBack) {
        super(activity);
        mParentView = parentView;
        mCallBack = callBack;
        nowDate = DateUtil.getNow(DateUtil.yyyy_MM_dd);
        selectDate = MineApp.registerInfo.getBirthday();
        if (selectDate.isEmpty()) {
            selectDate = nowDate;
            String[] strs = selectDate.split("-");
            mYear = Integer.parseInt(strs[0]);
            mMonth = Integer.parseInt(strs[1]);
            mDay = Integer.parseInt(strs[2]);
        } else {
            String[] strs = selectDate.split("/");
            mDay = Integer.parseInt(strs[0]);
            mMonth = Integer.parseInt(strs[1]);
            mYear = Integer.parseInt(strs[2]);
        }

        Log.i("date", mYear + "" + mMonth + "" + mDay);
        // 配置滑轮
        for (int i = 1960; i <= 2100; i++) {
            yearList.add(i + "年");
        }
        for (int i = 1; i <= 12; i++) {
            monthList.add(i + "月");
        }

        setBinding(DataBindingUtil.inflate(LayoutInflater.from(activity), uiRes(), null, false));
        initUI();
    }

    /**
     * 设置天数
     */
    private void setDays(int year, int month, int day, ArrayList<String> dayList, WheelView wheelView) {
        int days = DateUtil.getDaysByYearMonth(year, month);
        dayList.clear();
        for (int i = 1; i <= days; i++) {
            dayList.add(i + "日");
        }
        if (day == 0) {
            day = dayList.size();
        }
        int defaultIndex = (dayList.size() - day > 0) ? (day - 1) : (dayList.size() - 1);
        initData(activity, wheelView, dayList, defaultIndex);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initUI() {
        View view = binding.getRoot();
        setWidth(LinearLayout.LayoutParams.FILL_PARENT);
        setHeight(LinearLayout.LayoutParams.FILL_PARENT);
        setBackgroundDrawable(new BitmapDrawable());
        setFocusable(true);
        setOutsideTouchable(true);
        setContentView(view);
        showAtLocation(mParentView, Gravity.CENTER, 0, 0);
        update();

        view.setOnTouchListener((v, event) -> {
            if (isShowing()) {
                dismiss();
            }
            return false;
        });

        binding.setPw(this);

        binding.wheelYear.setVisibleItems(7);
        binding.wheelMonth.setVisibleItems(7);
        binding.wheelDay.setVisibleItems(7);
        initData(activity, binding.wheelYear, yearList, yearList.indexOf(mYear + "年"));
        initData(activity, binding.wheelMonth, monthList, mMonth - 1);
        setDays(mYear, mMonth, mDay, dayList, binding.wheelDay);
    }

    public interface CallBack {
        void selectBirthday(String birthday);
    }

    @Override
    public void sure(View view) {
        super.sure(view);
        mYear = Integer.parseInt(yearList.get(binding.wheelYear.getCurrentItem()).replace("年", ""));
        String yearStr = mYear + "";
        int nowYear = Integer.parseInt(nowDate.split("-")[0]);
        if (nowYear - mYear < 18) {
            SCToastUtil.showToast(activity, "您还未满18岁");
            return;
        }
        mMonth = Integer.parseInt(monthList.get(binding.wheelMonth.getCurrentItem()).replace("月", ""));
        String monthStr = mMonth < 10 ? "0" + mMonth : mMonth + "";
        mDay = Integer.parseInt(dayList.get(binding.wheelDay.getCurrentItem()).replace("日", ""));
        String dayStr = mDay < 10 ? "0" + mDay : mDay + "";


        mCallBack.selectBirthday(dayStr + "/" + monthStr + "/" + yearStr);
        dismiss();
    }

    /**
     * 设置日期
     *
     * @param activity
     * @param wheelView
     * @param data
     * @param defaultIndex
     */
    private void initData(AppCompatActivity activity, WheelView wheelView, final ArrayList<String> data, int defaultIndex) {

        defaultIndex = defaultIndex == -1 ? 0 : defaultIndex;

        final CalendarTextAdapter adapter = new CalendarTextAdapter(activity, data, defaultIndex, MAX_TEXT_SIZE, MIN_TEXT_SIZE);
        wheelView.setViewAdapter(adapter);
        wheelView.setCurrentItem(defaultIndex);
        adapter.setSelectIndex(defaultIndex);
        wheelView.addChangingListener((wheel, oldValue, newValue) -> {
            adapter.setSelectIndex(wheel.getCurrentItem());
        });
        wheelView.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                updateDateUI(adapter, wheel);
            }
        });
    }

    private void updateDateUI(CalendarTextAdapter adapter, WheelView wheel) {
        String currentText = (String) adapter.getItemText(wheel.getCurrentItem());
        adapter.setSelectIndex(wheel.getCurrentItem());
        switch (wheel.getId()) {
            case R.id.wheel_year:
                mYear = Integer.parseInt(currentText.replace("年", ""));
                setDays(mYear, mMonth, mDay, dayList, binding.wheelDay);
                break;
            case R.id.wheel_month:
                mMonth = Integer.parseInt(currentText.replace("月", ""));
                setDays(mYear, mMonth, mDay, dayList, binding.wheelDay);
                break;
            case R.id.wheel_day:
                mDay = Integer.parseInt(currentText.replace("日", ""));
                break;
        }
    }

    /**
     * 滚轮的adapter
     */
    private class CalendarTextAdapter extends AbstractWheelTextAdapter {
        ArrayList<String> list;
        private int selectIndex = -1;

        public void setSelectIndex(int selectIndex) {
            this.selectIndex = selectIndex;
            notifyDataChangedEvent();
        }

        protected CalendarTextAdapter(Context context, ArrayList<String> list, int currentItem, int maxsize, int minsize) {
            super(context, R.layout.item_birthday, R.id.tempValue, currentItem, maxsize, minsize);
            this.list = list;
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);

            TextView textView = view.findViewById(R.id.tempValue);
            if (selectIndex == index) {
                textView.setTextSize(MAX_TEXT_SIZE);
                textView.setTextColor(MineApp.getInstance().getResources().getColor(R.color.black));
            } else {
                textView.setTextSize(MIN_TEXT_SIZE);
                textView.setTextColor(MineApp.getInstance().getResources().getColor(R.color.black_9));
            }
            return view;
        }

        @Override
        public int getItemsCount() {
            return list.size();
        }

        @Override
        protected CharSequence getItemText(int index) {
            String str = list.get(index) + "";
            return str;
        }
    }
}