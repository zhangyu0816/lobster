package com.zb.module_card.vm;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.db.AreaDb;
import com.zb.lib_base.model.CityInfo;
import com.zb.lib_base.model.DistrictInfo;
import com.zb.lib_base.model.PairInfo;
import com.zb.lib_base.model.ProvinceInfo;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.utils.SimulateNetAPI;
import com.zb.lib_base.views.MyRecyclerView;
import com.zb.lib_base.views.RoundImageView;
import com.zb.lib_base.views.card.CardConfig;
import com.zb.lib_base.views.card.OnSwipeListener;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_card.R;
import com.zb.module_card.adapter.CardAdapter;
import com.zb.module_card.iv.CardVMInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.databinding.ViewDataBinding;
import io.realm.Realm;

public class CardViewModel extends BaseViewModel implements CardVMInterface, OnSwipeListener<PairInfo> {
    public AreaDb areaDb;
    public CardAdapter adapter;
    private List<PairInfo> pairInfoList = new ArrayList<>();

    private Handler handler = new Handler(msg -> {
        if (msg.what == 1) {
            adapter.notifyDataSetChanged();
        }
        return false;
    });

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        areaDb = new AreaDb(Realm.getDefaultInstance());

        for (int i = 0; i < 10; i++) {
            PairInfo pairInfo = new PairInfo();
            pairInfo.setNick("组我吧");
            pairInfo.getImageList().add("http://img01.zuwo.la/img/A/YMXXXX919714-206348_YM0000.jpg");
            pairInfo.getImageList().add("http://img01.zuwo.la/img/A/YMXXXX2350392-sgjdwurnll_YM0000.jpg");
            pairInfo.getImageList().add("http://img01.zuwo.la/img/A/YMXXXX919714-206348_YM0000.jpg");
            pairInfo.getImageList().add("http://img01.zuwo.la/img/A/YMXXXX2350392-sgjdwurnll_YM0000.jpg");
            pairInfo.getImageList().add("http://img01.zuwo.la/img/A/YMXXXX919714-206348_YM0000.jpg");
            pairInfo.getImageList().add("http://img01.zuwo.la/img/A/YMXXXX2350392-sgjdwurnll_YM0000.jpg");
            pairInfo.getImageList().add("http://img01.zuwo.la/img/A/YMXXXX919714-206348_YM0000.jpg");
            pairInfo.setPersonalitySign("文科积分为了附件为了看积分为了就访问路径访问了就放了假法律文件弗兰克就法律文件放了假放了假发");
            pairInfoList.add(pairInfo);
        }

        initArea();
        setAdapter();
    }

    @Override
    public void setAdapter() {
        adapter = new CardAdapter<>(activity, R.layout.item_card, pairInfoList, this);
        adapter.setSelectIndex(0);
    }

    @Override
    public void selectCard(View currentView, int position) {

    }

    @Override
    public void returnView(View view) {

    }

    @Override
    public void leftBtn(View currentView, CardAdapter imageAdapter, int position) {
        if (imageAdapter.getSelectImageIndex() > 0) {
            int preIndex = imageAdapter.getSelectImageIndex();
            int selectIndex = preIndex - 1;
            updateAdapterUI(currentView, imageAdapter, preIndex, selectIndex, pairInfoList.get(position).getImageList());
        }
    }

    @Override
    public void rightBtn(View currentView, CardAdapter imageAdapter, int position) {
        if (imageAdapter.getSelectImageIndex() < pairInfoList.get(position).getImageList().size() - 1) {
            int preIndex = imageAdapter.getSelectImageIndex();
            int selectIndex = preIndex + 1;
            updateAdapterUI(currentView, imageAdapter, preIndex, selectIndex, pairInfoList.get(position).getImageList());
        }
    }

    // 更新adapterUI
    private void updateAdapterUI(View view, CardAdapter imageAdapter, int preIndex, int selectIndex, List<String> imageList) {
        imageAdapter.setSelectImageIndex(selectIndex);
        imageAdapter.notifyItemChanged(preIndex);
        imageAdapter.notifyItemChanged(selectIndex);

        RoundImageView imageView = view.findViewById(R.id.iv_big_image);
        MyRecyclerView imageListView = view.findViewById(R.id.image_list);

        Objects.requireNonNull(imageListView.getLayoutManager()).scrollToPosition(selectIndex);
        AdapterBinding.loadImage(imageView, imageList.get(selectIndex),
                0, ObjectUtils.getDefaultRes(), ObjectUtils.getViewSizeByWidth(0.92f),
                ObjectUtils.getLogoHeight(0.92f), false, false, 0,
                false, 0);
    }

    @Override
    public void superLike(View view) {
        super.superLike(view);

    }

    @Override
    public void onSwiping(View view, float ratio, int direction) {
    }

    @Override
    public void onSwiped(View view, PairInfo pairInfo, int direction) {

    }

    @Override
    public void onSwipedClear() {

    }

    // 省市信息
    private void initArea() {
        if (!areaDb.hasProvince()) {
            String data = SimulateNetAPI.getOriginalFundData(activity, "cityData.json");
            new Thread(() -> {
                AreaDb areaDb = new AreaDb(Realm.getDefaultInstance());
                try {
                    JSONArray array = new JSONArray(data);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject provinceJSON = array.optJSONObject(i);
                        // 省
                        ProvinceInfo provinceInfo = new ProvinceInfo();
                        provinceInfo.setProvinceId(Long.parseLong(provinceJSON.optString("value")));
                        provinceInfo.setProvinceName(provinceJSON.optString("label"));
                        areaDb.saveProvince(provinceInfo);

                        // 市
                        JSONArray cityArray = provinceJSON.optJSONArray("children");
                        if (cityArray != null) {
                            for (int j = 0; j < cityArray.length(); j++) {
                                JSONObject cityJSON = cityArray.optJSONObject(j);
                                CityInfo cityInfo = new CityInfo();
                                cityInfo.setProvinceId(provinceInfo.getProvinceId());
                                cityInfo.setCityId(Long.parseLong(cityJSON.optString("value")));
                                cityInfo.setCityName(cityJSON.optString("label"));
                                areaDb.saveCity(cityInfo);
                                // 地区
                                JSONArray districtArray = cityJSON.optJSONArray("children");
                                if (districtArray != null) {
                                    for (int k = 0; k < districtArray.length(); k++) {
                                        JSONObject districtJSON = districtArray.optJSONObject(k);
                                        DistrictInfo districtInfo = new DistrictInfo();
                                        districtInfo.setCityId(cityInfo.getCityId());
                                        districtInfo.setDistrictId(Long.parseLong(districtJSON.optString("value")));
                                        districtInfo.setDistrictName(districtJSON.optString("label"));
                                        areaDb.saveDistrictInfo(districtInfo);
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(1);
            }).start();
        } else {
            handler.sendEmptyMessage(1);
        }
    }


}
