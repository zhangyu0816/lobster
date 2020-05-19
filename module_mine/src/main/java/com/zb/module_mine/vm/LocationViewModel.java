package com.zb.module_mine.vm;

import android.Manifest;
import android.os.Build;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.model.LocationInfo;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.R;
import com.zb.module_mine.adapter.MineAdapter;
import com.zb.module_mine.databinding.MineLocationBinding;
import com.zb.module_mine.iv.LocationVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.ViewDataBinding;

public class LocationViewModel extends BaseViewModel implements LocationVMInterface, GeocodeSearch.OnGeocodeSearchListener {
    public MineAdapter adapter;
    private List<LocationInfo> locationInfoList = new ArrayList<>();
    private AMap aMap;
    private int prePosition = -1;
    private MineLocationBinding locationBinding;
    private LatLng myLl;
    private LatLng tagLl;
    private boolean isSearch = false;
    private GeocodeSearch geocodeSearch;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        geocodeSearch = new GeocodeSearch(activity);
        geocodeSearch.setOnGeocodeSearchListener(this);
        locationBinding = (MineLocationBinding) binding;
        setAdapter();
        initMap();
        locationBinding.edKey.setOnEditorActionListener((arg0, arg1, arg2) -> {
            if (arg1 == EditorInfo.IME_ACTION_SEARCH) {
                hintKeyBoard();
                querySearchByTips(arg0.getText().toString());
            }
            return false;
        });
        AdapterBinding.viewSize(locationBinding.addressList, MineApp.W, (int) (MineApp.H * 0.4f));
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void setAdapter() {
        adapter = new MineAdapter<>(activity, R.layout.item_mine_location_address, locationInfoList, this);
    }

    private void initMap() {
        aMap = locationBinding.mapView.getMap();
        myLl = new LatLng(Double.parseDouble(PreferenceUtil.readStringValue(activity, "latitude")),
                Double.parseDouble(PreferenceUtil.readStringValue(activity, "longitude")));
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(myLl));//设置中心点
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16)); // 设置地图可视缩放大小
        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                if (!isSearch) {
                    tagLl = cameraPosition.target;
                    getPermissions();
                }
                isSearch = false;

            }
        });
    }

    @Override
    public void selectPosition(int position) {
        super.selectPosition(position);
        if (prePosition != position) {
            adapter.setSelectIndex(position);
            adapter.notifyItemChanged(position);
            adapter.notifyItemChanged(prePosition);
            prePosition = position;
        }
    }

    /**
     * 权限
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            performCodeWithPermission("虾菇需要访问定位权限", new BaseActivity.PermissionCallback() {
                        @Override
                        public void hasPermission() {
                            setPermissions();
                        }

                        @Override
                        public void noPermission() {

                        }
                    }, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE);
        } else {
            setPermissions();
        }
    }

    private void setPermissions() {
        PoiSearch.Query query = new PoiSearch.Query("", "", "");
        query.setPageSize(10);
        PoiSearch search = new PoiSearch(activity, query);
        search.setBound(new PoiSearch.SearchBound(new LatLonPoint(tagLl.latitude, tagLl.longitude), 10000, true));
        search.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult poiResult, int i) {
                if (poiResult != null) {
                    locationInfoList.clear();
                    adapter.setSelectIndex(-1);
                    adapter.notifyDataSetChanged();
                    for (PoiItem poi : poiResult.getPois()) {
                        LocationInfo info = new LocationInfo();
                        info.setCityName(poi.getCityName());
                        info.setTitle(poi.getTitle());
                        info.setAddress(poi.getSnippet());
                        info.setLatitude(poi.getLatLonPoint().getLatitude());
                        info.setLongitude(poi.getLatLonPoint().getLongitude());
                        locationInfoList.add(info);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    locationInfoList.clear();
                    adapter.setSelectIndex(-1);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onPoiItemSearched(PoiItem poiItem, int i) {

            }
        });
        search.searchPOIAsyn();
    }

    /**
     * 附近列表
     *
     * @param keyWord
     */
    public void querySearchByTips(String keyWord) {
        if (keyWord.isEmpty()) return;

        InputtipsQuery inputQuery = new InputtipsQuery(keyWord, "");
        inputQuery.setCityLimit(false);
        Inputtips inputTips = new Inputtips(activity, inputQuery);
        inputTips.setInputtipsListener((tipList, rCode) -> {
            if (rCode == AMapException.CODE_AMAP_SUCCESS) {
                if (tipList.size() > 0) {
                    prePosition = -1;
                    isSearch = true;
                    locationInfoList.clear();
                    adapter.setSelectIndex(-1);
                    adapter.notifyDataSetChanged();
                    for (int i = 0; i < tipList.size(); i++) {
                        try {
                            aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(tipList.get(i).getPoint().getLatitude(), tipList.get(i).getPoint().getLongitude())));//设置中心点
                            break;
                        } catch (Exception e) {

                        }
                    }
                    for (Tip tipItem : tipList) {
                        //如果该条数据不是一个地点的数据，剔除
                        if (tipItem.getPoiID() != null && tipItem.getPoint() == null) {
                            continue;
                        }
                        LocationInfo info = new LocationInfo();
                        info.setTitle(tipItem.getName());
                        info.setAddress(tipItem.getAddress());
                        info.setLatitude(tipItem.getPoint().getLatitude());
                        info.setLongitude(tipItem.getPoint().getLongitude());
                        locationInfoList.add(info);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
        inputTips.requestInputtipsAsyn();
    }

    @Override
    public void selectAddress(View view) {
        if (prePosition == -1) {
            SCToastUtil.showToastBlack(activity, "请选择地址");
            return;
        }
        LocationInfo info = locationInfoList.get(prePosition);
        LatLonPoint latLonPoint = new LatLonPoint(info.getLatitude(), info.getLongitude());
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 500f, GeocodeSearch.AMAP);
        //异步查询
        geocodeSearch.getFromLocationAsyn(query);
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
        LocationInfo info = locationInfoList.get(prePosition);
        info.setCityName(regeocodeAddress.getCity());
        PreferenceUtil.saveStringValue(activity, "address", info.getAddress());
        MineApp.cityName = info.getCityName();
        activity.finish();
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }
}
