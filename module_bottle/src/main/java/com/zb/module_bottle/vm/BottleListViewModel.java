package com.zb.module_bottle.vm;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.myBottleListApi;
import com.zb.lib_base.api.myInfoApi;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.BottleInfo;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_bottle.R;
import com.zb.module_bottle.adapter.BottleAdapter;
import com.zb.module_bottle.databinding.BottleListBinding;
import com.zb.module_bottle.iv.BottleListVMInterface;
import com.zb.module_bottle.windows.BottleVipPW;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

public class BottleListViewModel extends BaseViewModel implements BottleListVMInterface, OnRefreshListener, OnLoadMoreListener {

    public BottleAdapter adapter;
    private List<BottleInfo> bottleInfoList = new ArrayList<>();
    public BaseReceiver openVipReceiver;
    public MineInfo mineInfo;
    private int pageNo = 1;
    private BottleListBinding listBinding;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mineInfo = mineInfoDb.getMineInfo();
        listBinding = (BottleListBinding) binding;

        // 开通会员
        openVipReceiver = new BaseReceiver(activity, "lobster_openVip") {
            @Override
            public void onReceive(Context context, Intent intent) {
                myInfo();
            }
        };
        setAdapter();
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void setAdapter() {
        adapter = new BottleAdapter<>(activity, R.layout.item_bottle, bottleInfoList, this);
        myBottleList();
    }

    @Override
    public void selectIndex(int position) {
        if (mineInfo.getMemberType() == 2) {
            BottleInfo bottleInfo = bottleInfoList.get(position);
            bottleInfo.setNoReadNum(0);
            adapter.notifyItemChanged(position);
            ActivityUtils.getBottleChat(bottleInfo.getDriftBottleId());
            return;
        }
        new BottleVipPW(activity, mBinding.getRoot());
    }

    @Override
    public void myBottleList() {
        myBottleListApi api = new myBottleListApi(new HttpOnNextListener<List<BottleInfo>>() {
            @Override
            public void onNext(List<BottleInfo> o) {
                int start = bottleInfoList.size();
                for (BottleInfo bottleInfo : o) {
                    if (bottleInfo.getOtherHeadImage().isEmpty()) {
                        bottleInfo.setOtherHeadImage(mineInfo.getImage());
                        bottleInfo.setOtherNick(mineInfo.getNick());
                    }
                    bottleInfoList.add(bottleInfo);
                }
                adapter.notifyItemRangeChanged(start, bottleInfoList.size());
                listBinding.refresh.finishRefresh();
                listBinding.refresh.finishLoadMore();
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    listBinding.refresh.setEnableLoadMore(false);
                    listBinding.refresh.finishRefresh();
                    listBinding.refresh.finishLoadMore();
                }
            }
        }, activity).setPageNo(pageNo);
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
        listBinding.refresh.setEnableLoadMore(true);
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
