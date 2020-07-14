package com.zb.module_bottle.vm;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.myBottleListApi;
import com.zb.lib_base.api.myInfoApi;
import com.zb.lib_base.api.pickBottleApi;
import com.zb.lib_base.db.BottleCacheDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.BottleCache;
import com.zb.lib_base.model.BottleInfo;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.utils.ActivityUtils;
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
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.ItemTouchHelper;
import io.realm.Realm;

public class BottleListViewModel extends BaseViewModel implements BottleListVMInterface, OnRefreshListener, OnLoadMoreListener {

    public BottleAdapter adapter;
    public MineInfo mineInfo;
    private List<BottleInfo> bottleInfoList = new ArrayList<>();
    private BaseReceiver openVipReceiver;
    private BaseReceiver finishRefreshReceiver;
    private int pageNo = 1;
    private BottleListBinding mBinding;
    private SimpleItemTouchHelperCallback callback;
    private BottleCacheDb bottleCacheDb;
    private BaseReceiver updateBottleReceiver;
    private BaseReceiver singleBottleCacheReceiver;
    private int prePosition = -1;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mineInfo = mineInfoDb.getMineInfo();
        bottleCacheDb = new BottleCacheDb(Realm.getDefaultInstance());
        mBinding = (BottleListBinding) binding;
        mBinding.setShowBg(false);
        // 开通会员
        openVipReceiver = new BaseReceiver(activity, "lobster_openVip") {
            @Override
            public void onReceive(Context context, Intent intent) {
                myInfo();
            }
        };
        finishRefreshReceiver = new BaseReceiver(activity, "lobster_finishRefresh") {
            @Override
            public void onReceive(Context context, Intent intent) {
                mBinding.refresh.finishRefresh();
                mBinding.refresh.finishLoadMore();
            }
        };
        updateBottleReceiver = new BaseReceiver(activity, "lobster_updateBottle") {
            @Override
            public void onReceive(Context context, Intent intent) {
                BottleInfo bottleInfo = bottleInfoList.get(prePosition);
                BottleCache bottleCache = bottleCacheDb.getBottleCache(bottleInfo.getDriftBottleId());
                if (bottleCache != null) {
                    bottleInfo.setText(bottleCache.getStanza());
                    bottleInfo.setModifyTime(bottleCache.getCreationDate());
                }
                adapter.notifyItemChanged(prePosition);
            }
        };
        singleBottleCacheReceiver = new BaseReceiver(activity, "lobster_singleBottleCache") {
            @Override
            public void onReceive(Context context, Intent intent) {
                long driftBottleId = intent.getLongExtra("driftBottleId", 0);
                for (int i = 0; i < bottleInfoList.size(); i++) {
                    BottleInfo bottleInfo = bottleInfoList.get(i);
                    if (bottleInfo.getDriftBottleId() == driftBottleId) {
                        BottleCache bottleCache = bottleCacheDb.getBottleCache(driftBottleId);
                        if (bottleCache != null) {
                            bottleInfo.setText(bottleCache.getStanza());
                            bottleInfo.setModifyTime(bottleCache.getCreationDate());
                        }
                        adapter.notifyItemChanged(i);
                        break;
                    }
                }

            }
        };

        int height = DisplayUtils.dip2px(82) - ObjectUtils.getViewSizeByWidth(660f / 1125f);
        mBinding.appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> mBinding.setShowBg(verticalOffset <= height));
        setAdapter();
    }

    public void onDestroy() {
        openVipReceiver.unregisterReceiver();
        finishRefreshReceiver.unregisterReceiver();
        updateBottleReceiver.unregisterReceiver();
        singleBottleCacheReceiver.unregisterReceiver();
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void setAdapter() {
        adapter = new BottleAdapter<>(activity, R.layout.item_bottle, bottleInfoList, this);
        callback = new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mBinding.bottleList);
        callback.setSort(false);
        callback.setSwipeEnabled(true);
        callback.setSwipeFlags(ItemTouchHelper.START | ItemTouchHelper.END);
        callback.setDragFlags(0);
        activity.sendBroadcast(new Intent("lobster_bottleNum"));
        myBottleList();
    }

    @Override
    public void selectIndex(int position) {
        prePosition = position;
        BottleInfo bottleInfo = bottleInfoList.get(position);
        bottleInfo.setNoReadNum(0);
        adapter.notifyItemChanged(position);
        ActivityUtils.getBottleChat(bottleInfo.getDriftBottleId());

    }

    @Override
    public void myBottleList() {
        myBottleListApi api = new myBottleListApi(new HttpOnNextListener<List<BottleInfo>>() {
            @Override
            public void onNext(List<BottleInfo> o) {
                mBinding.setNoData(false);
                int start = bottleInfoList.size();
                for (BottleInfo bottleInfo : o) {
                    if (bottleInfo.getDestroyType() == 1 && bottleInfo.getUserId() == BaseActivity.userId) {
                        continue;
                    }
                    if (bottleInfo.getDestroyType() == 2 && bottleInfo.getOtherUserId() == BaseActivity.userId) {
                        continue;
                    }

                    if (bottleInfo.getOtherHeadImage().isEmpty()) {
                        bottleInfo.setOtherHeadImage(mineInfo.getImage());
                        bottleInfo.setOtherNick(mineInfo.getNick());
                    }
                    BottleCache bottleCache = bottleCacheDb.getBottleCache(bottleInfo.getDriftBottleId());
                    if (bottleCache != null) {
                        bottleInfo.setText(bottleCache.getStanza());
                        bottleInfo.setNoReadNum(bottleCache.getNoReadNum());
                        bottleInfo.setModifyTime(bottleCache.getCreationDate());
                    }
                    bottleInfoList.add(bottleInfo);
                }
                adapter.notifyItemRangeChanged(start, bottleInfoList.size());
                mBinding.refresh.finishRefresh();
                mBinding.refresh.finishLoadMore();
                if (bottleInfoList.size() == 0 && pageNo == 1) {
                    mBinding.setNoData(true);
                } else if (bottleInfoList.size() < 10) {
                    pageNo++;
                    myBottleList();
                }
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    mBinding.refresh.setEnableLoadMore(false);
                    mBinding.refresh.finishRefresh();
                    mBinding.refresh.finishLoadMore();
                    if (bottleInfoList.size() == 0 && pageNo == 1) {
                        mBinding.setNoData(true);
                    }
                }
            }
        }, activity).setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void deleteBottle(int position) {
        new TextPW(activity, mBinding.getRoot(), "销毁漂流瓶", "销毁后，你将与对方失去联系", "销毁", false, new TextPW.CallBack() {
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

    // 销毁
    private void pickBottle(int position) {
        pickBottleApi api = new pickBottleApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                bottleCacheDb.deleteBottleCache(bottleInfoList.get(position).getDriftBottleId());
                adapter.notifyItemRemoved(position);
                bottleInfoList.remove(position);
                activity.sendBroadcast(new Intent("lobster_bottleNum"));
                if (bottleInfoList.size() == 0) {
                    onRefresh(mBinding.refresh);
                }

            }
        }, activity).setDriftBottleId(bottleInfoList.get(position).getDriftBottleId()).setDriftBottleType(3);
        api.setShowProgress(true);
        api.setDialogTitle("销毁漂流瓶");
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        // 上拉加载更多
        pageNo++;
        myBottleList();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        // 下拉刷新
        activity.sendBroadcast(new Intent("lobster_bottleNum"));
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
                mineInfo = o;
                mineInfoDb.saveMineInfo(o);
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
