package com.zb.module_bottle.vm;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.clearAllDriftBottleHistoryMsgApi;
import com.zb.lib_base.api.myBottleListApi;
import com.zb.lib_base.api.myInfoApi;
import com.zb.lib_base.api.pickBottleApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.BottleCacheDb;
import com.zb.lib_base.db.HistoryMsgDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.BottleCache;
import com.zb.lib_base.model.BottleInfo;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DateUtil;
import com.zb.lib_base.utils.DisplayUtils;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.utils.SimpleItemTouchHelperCallback;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.TextPW;
import com.zb.module_bottle.R;
import com.zb.module_bottle.adapter.BottleAdapter;
import com.zb.module_bottle.databinding.BottleListBinding;
import com.zb.module_bottle.iv.BottleListVMInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.ItemTouchHelper;

public class BottleListViewModel extends BaseViewModel implements BottleListVMInterface, OnRefreshListener, OnLoadMoreListener {

    public BottleAdapter adapter;
    private List<BottleInfo> bottleInfoList = new ArrayList<>();
    private BaseReceiver openVipReceiver;
    private int pageNo = 1;
    private BottleListBinding mBinding;
    private BaseReceiver singleBottleCacheReceiver;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (BottleListBinding) binding;
        mBinding.setShowBg(false);
        // ????????????
        openVipReceiver = new BaseReceiver(activity, "lobster_openVip") {
            @Override
            public void onReceive(Context context, Intent intent) {
                myInfo();
            }
        };
        singleBottleCacheReceiver = new BaseReceiver(activity, "lobster_singleBottleCache") {
            @Override
            public void onReceive(Context context, Intent intent) {
                long driftBottleId = intent.getLongExtra("driftBottleId", 0);
                int position = -1;
                BottleInfo bottleInfo = null;
                for (int i = 0; i < bottleInfoList.size(); i++) {
                    bottleInfo = bottleInfoList.get(i);
                    if (bottleInfo.getDriftBottleId() == driftBottleId) {
                        position = i;
                        break;
                    }
                }

                if (position > -1) {
                    adapter.notifyItemRemoved(position);
                    bottleInfoList.remove(position);

                    BottleCache bottleCache = BottleCacheDb.getInstance().getBottleCache(driftBottleId);
                    if (bottleCache != null) {
                        bottleInfo.setText(bottleCache.getStanza());
                        bottleInfo.setModifyTime(bottleCache.getCreationDate());
                        bottleInfo.setNoReadNum(bottleCache.getNoReadNum());
                    }
                    bottleInfoList.add(0, bottleInfo);
                    adapter.notifyDataSetChanged();
                }

            }
        };

        int height = DisplayUtils.dip2px(82) - ObjectUtils.getViewSizeByWidth(660f / 1125f);
        mBinding.appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> mBinding.setShowBg(verticalOffset <= height));
        setAdapter();
    }

    public void onDestroy() {
        try {
            openVipReceiver.unregisterReceiver();
            singleBottleCacheReceiver.unregisterReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void setAdapter() {
        adapter = new BottleAdapter<>(activity, R.layout.item_bottle, bottleInfoList, this);
        SimpleItemTouchHelperCallback callback = new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mBinding.bottleList);
        callback.setSort(false);
        callback.setSwipeEnabled(true);
        callback.setSwipeFlags(ItemTouchHelper.START | ItemTouchHelper.END);
        callback.setDragFlags(0);
        myBottleList();
    }

    @Override
    public void selectIndex(int position) {
        BottleInfo bottleInfo = bottleInfoList.get(position);
        bottleInfo.setNoReadNum(0);
        adapter.notifyItemChanged(position);
        ActivityUtils.getBottleChat(bottleInfo.getDriftBottleId(), false);

    }

    @Override
    public void myBottleList() {
        myBottleListApi api = new myBottleListApi(new HttpOnNextListener<List<BottleInfo>>() {
            @Override
            public void onNext(List<BottleInfo> o) {
                for (BottleInfo bottleInfo : o) {
                    if (bottleInfo.getDestroyType() == 1 && bottleInfo.getUserId() == BaseActivity.userId) {
                        BottleCacheDb.getInstance().deleteBottleCache(bottleInfo.getDriftBottleId());
                        continue;
                    }
                    if (bottleInfo.getDestroyType() == 2 && bottleInfo.getOtherUserId() == BaseActivity.userId) {
                        BottleCacheDb.getInstance().deleteBottleCache(bottleInfo.getDriftBottleId());
                        continue;
                    }

                    if (bottleInfo.getOtherHeadImage().isEmpty()) {
                        bottleInfo.setOtherHeadImage(MineApp.mineInfo.getImage());
                        bottleInfo.setOtherNick(MineApp.mineInfo.getNick());
                    }
                    BottleCache bottleCache = BottleCacheDb.getInstance().getBottleCache(bottleInfo.getDriftBottleId());
                    if (bottleCache != null) {
                        bottleInfo.setText(bottleCache.getStanza());
                        bottleInfo.setNoReadNum(bottleCache.getNoReadNum());
                        bottleInfo.setModifyTime(bottleCache.getCreationDate());
                    } else {
                        bottleInfo.setModifyTime(bottleInfo.getCreateTime());
                    }
                    bottleInfoList.add(bottleInfo);
                }
                pageNo++;
                myBottleList();
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    mBinding.refresh.setEnableLoadMore(false);
                    mBinding.refresh.finishRefresh();
                    mBinding.refresh.finishLoadMore();

                    if (bottleInfoList.size() == 0) {
                        mBinding.setNoData(true);
                    } else {
                        mBinding.setNoData(false);
                        BottleInfoComparator comparator = new BottleInfoComparator();
                        Collections.sort(bottleInfoList, comparator);
                        adapter.notifyDataSetChanged();
                        LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_bottleNum"));
                    }
                }
            }
        }, activity).setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    public static class BottleInfoComparator implements Comparator<BottleInfo> {
        @Override
        public int compare(BottleInfo o1, BottleInfo o2) {
            if (o1 == null && o2 == null) {
                return 0;
            }
            if (o1 == null) {
                return -1;
            }
            if (o2 == null) {
                return 1;
            }
            if (o1.getModifyTime().isEmpty())
                return -1;
            if (o2.getModifyTime().isEmpty())
                return -1;
            return DateUtil.getDateCount(o2.getModifyTime(), o1.getModifyTime(), DateUtil.yyyy_MM_dd_HH_mm_ss, 1000f);
        }
    }

    @Override
    public void deleteBottle(int position) {
        new TextPW(mBinding.getRoot(), "???????????????", "???????????????????????????????????????", "??????", false, new TextPW.CallBack() {
            @Override
            public void sure() {
                pickBottle(position);
            }

            @Override
            public void cancel() {
                adapter.notifyItemChanged(position);
            }
        });
    }

    // ??????
    private void pickBottle(int position) {
        pickBottleApi api = new pickBottleApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                BottleInfo bottleInfo = bottleInfoList.get(position);
                long otherUserId = bottleInfo.getUserId() == BaseActivity.userId ? bottleInfo.getOtherUserId() : bottleInfo.getUserId();

                BottleCacheDb.getInstance().deleteBottleCache(bottleInfo.getDriftBottleId());
                HistoryMsgDb.getInstance().deleteHistoryMsg(otherUserId, 2, bottleInfo.getDriftBottleId(), 0);
                adapter.notifyItemRemoved(position);
                bottleInfoList.remove(position);
                adapter.notifyDataSetChanged();

                clearAllHistoryMsg(otherUserId, bottleInfo.getDriftBottleId());
                LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_bottleNum"));
                if (bottleInfoList.size() == 0) {
                    onRefresh(mBinding.refresh);
                }

            }
        }, activity).setDriftBottleId(bottleInfoList.get(position).getDriftBottleId()).setDriftBottleType(3);
        api.setShowProgress(true);
        api.setDialogTitle("???????????????");
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        // ??????????????????
        pageNo++;
        myBottleList();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        // ????????????
        mBinding.refresh.setEnableLoadMore(true);
        pageNo = 1;
        bottleInfoList.clear();
        adapter.notifyDataSetChanged();
        myBottleList();
    }

    @Override
    public void myInfo() {
        myInfoApi api = new myInfoApi(new HttpOnNextListener<MineInfo>() {
            @Override
            public void onNext(MineInfo o) {
                MineApp.mineInfo = o;
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void clearAllHistoryMsg(long otherUserId, long driftBottleId) {
        clearAllDriftBottleHistoryMsgApi api = new clearAllDriftBottleHistoryMsgApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {

            }
        }, activity).setOtherUserId(otherUserId).setDriftBottleId(driftBottleId);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
