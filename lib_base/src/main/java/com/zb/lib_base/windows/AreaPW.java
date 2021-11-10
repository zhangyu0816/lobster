package com.zb.lib_base.windows;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.R;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.databinding.PwsAreaBinding;
import com.zb.lib_base.db.AreaDb;
import com.zb.lib_base.utils.widget.OnWheelScrollListener;
import com.zb.lib_base.utils.widget.WheelView;
import com.zb.lib_base.utils.widget.adapters.AbstractWheelTextAdapter;

import java.util.ArrayList;

public class AreaPW extends BasePopupWindow {
    private PwsAreaBinding binding;
    private ArrayList<String> provinceNameList;
    private ArrayList<String> cityNameList;
    private int MAX_TEXT_SIZE = 20;
    private int MIN_TEXT_SIZE = 16;
    private String provinceName = "";
    private String cityName = "";
    private long provinceId;
    private long cityId;
    private CallBack callBack;

    public AreaPW(View parentView) {
        super(parentView, true);
        provinceNameList = AreaDb.getInstance().getProvinceNameList();
        provinceName = provinceNameList.get(0);
        provinceId = AreaDb.getInstance().getProvinceId(provinceName);

        cityNameList = AreaDb.getInstance().getCityNameList(provinceId);
        cityName = cityNameList.get(0);
        cityId = AreaDb.getInstance().getCityId(cityName);
    }

    public AreaPW setCallBack(CallBack callBack) {
        this.callBack = callBack;
        return this;
    }

    @Override
    public int getRes() {
        return R.layout.pws_area;
    }

    @Override
    public void initUI() {
        binding = (PwsAreaBinding) mBinding;
        binding.setPw(this);
        binding.wheelProvince.setVisibleItems(7);
        binding.wheelCity.setVisibleItems(7);
        initData(activity, binding.wheelProvince, provinceNameList, 0);
        initData(activity, binding.wheelCity, cityNameList, 0);
    }

    public void cancel(View view) {
        dismiss();
    }

    public void sure(View view) {
        callBack.sure(provinceName + "." + cityName, provinceId, cityId);
        dismiss();
    }

    public interface CallBack {
        void sure(String name, long provinceId, long cityId);
    }

    private void initData(RxAppCompatActivity activity, WheelView wheelView, final ArrayList<String> data, int defaultIndex) {

        defaultIndex = defaultIndex == -1 ? 0 : defaultIndex;

        final CalendarTextAdapter adapter = new CalendarTextAdapter(activity, data, defaultIndex, MAX_TEXT_SIZE, MIN_TEXT_SIZE);
        wheelView.setViewAdapter(adapter);
        wheelView.setCurrentItem(defaultIndex);
        adapter.setSelectIndex(defaultIndex);
        wheelView.addChangingListener((wheel, oldValue, newValue) -> adapter.setSelectIndex(wheel.getCurrentItem()));
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
        adapter.setSelectIndex(wheel.getCurrentItem());
        int id = wheel.getId();
        if (id == R.id.wheel_province) {
            provinceName = provinceNameList.get(wheel.getCurrentItem());
            provinceId = AreaDb.getInstance().getProvinceId(provinceName);
            cityNameList = AreaDb.getInstance().getCityNameList(provinceId);
            initData(activity, binding.wheelCity, cityNameList, 0);
            cityName = cityNameList.get(0);
            cityId = AreaDb.getInstance().getCityId(cityName);
        } else if (id == R.id.wheel_city) {
            cityName = cityNameList.get(wheel.getCurrentItem());
            cityId = AreaDb.getInstance().getCityId(cityName);

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
                textView.setTextColor(MineApp.getApp().getResources().getColor(R.color.black));
            } else {
                textView.setTextSize(MIN_TEXT_SIZE);
                textView.setTextColor(MineApp.getApp().getResources().getColor(R.color.black_9));
            }
            return view;
        }

        @Override
        public int getItemsCount() {
            return list.size();
        }

        @Override
        protected CharSequence getItemText(int index) {
            return list.get(index) + "";
        }
    }
}
