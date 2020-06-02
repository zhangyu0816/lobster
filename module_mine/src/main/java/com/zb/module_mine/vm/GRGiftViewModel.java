package com.zb.module_mine.vm;

import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.api.giveOrReceiveListApi;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.GiftRecord;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.R;
import com.zb.module_mine.adapter.MineAdapter;
import com.zb.module_mine.databinding.MineGiveReceiveGiftBinding;
import com.zb.module_mine.iv.GRGiftVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

public class GRGiftViewModel extends BaseViewModel implements GRGiftVMInterface, OnRefreshListener, OnLoadMoreListener {
    public int friendDynGiftType;
    public MineAdapter adapter;
    private int pageNo = 1;
    private List<GiftRecord> giftRecordList = new ArrayList<>();
    private MineGiveReceiveGiftBinding mBinding;

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (MineGiveReceiveGiftBinding) binding;
        setAdapter();
    }

    @Override
    public void setAdapter() {
        adapter = new MineAdapter<>(activity, R.layout.item_gift_record, giftRecordList, this);
        giveOrReceiveList();
    }

    @Override
    public void giveOrReceiveList() {
        giveOrReceiveListApi api = new giveOrReceiveListApi(new HttpOnNextListener<List<GiftRecord>>() {
            @Override
            public void onNext(List<GiftRecord> o) {
                int start = giftRecordList.size();
                giftRecordList.addAll(o);
                adapter.notifyItemRangeChanged(start, giftRecordList.size());
                mBinding.refresh.finishRefresh();
                mBinding.refresh.finishLoadMore();
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    mBinding.refresh.setEnableLoadMore(false);
                    mBinding.refresh.finishRefresh();
                    mBinding.refresh.finishLoadMore();
                }
            }
        }, activity).setPageNo(pageNo).setFriendDynGiftType(friendDynGiftType);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void toDiscover(int position) {
        GiftRecord giftRecord = giftRecordList.get(position);
        if (giftRecord.getDycType() < 4)
            ActivityUtils.getHomeDiscoverDetail(giftRecord.getFriendDynamicId());
        else
            ActivityUtils.getHomeDiscoverVideo(giftRecord.getFriendDynamicId());
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        pageNo++;
        giveOrReceiveList();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        // 下拉刷新
        mBinding.refresh.setEnableLoadMore(true);
        pageNo = 1;
        giftRecordList.clear();
        adapter.notifyDataSetChanged();
        giveOrReceiveList();
    }
}
