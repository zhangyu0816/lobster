package com.zb.module_mine.vm;

import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.api.dynNewMsgListApi;
import com.zb.lib_base.api.readOverMyDynNewMsgApi;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.MineNews;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.R;
import com.zb.module_mine.adapter.MineAdapter;
import com.zb.module_mine.databinding.MineNewsListBinding;
import com.zb.module_mine.iv.NewsListVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

public class NewsListViewModel extends BaseViewModel implements NewsListVMInterface, OnRefreshListener, OnLoadMoreListener {
    public MineAdapter adapter;
    private List<MineNews> mineNewsList = new ArrayList<>();
    public int reviewType;
    private int pageNo = 1;
    private MineNewsListBinding mBinding;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (MineNewsListBinding) binding;
        setAdapter();
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void setAdapter() {
        adapter = new MineAdapter<>(activity, R.layout.item_mine_news_list, mineNewsList, this);
        dynNewMsgList();
    }

    @Override
    public void dynNewMsgList() {
        dynNewMsgListApi api = new dynNewMsgListApi(new HttpOnNextListener<List<MineNews>>() {
            @Override
            public void onNext(List<MineNews> o) {
                int start = mineNewsList.size();
                mineNewsList.addAll(o);
                adapter.notifyItemRangeChanged(start, mineNewsList.size());
                mBinding.refresh.finishRefresh();
                mBinding.refresh.finishLoadMore();
                mBinding.setNoData(false);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    mBinding.refresh.setEnableLoadMore(false);
                    mBinding.refresh.finishRefresh();
                    mBinding.refresh.finishLoadMore();
                }
            }
        }, activity).setPageNo(pageNo).setReviewType(reviewType);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void clickDyn(MineNews mineNews) {
        if (mineNews.getFriendDynamicDycType() >= 4)
            ActivityUtils.getHomeDiscoverVideo(mineNews.getFriendDynamicId());
        else
            ActivityUtils.getHomeDiscoverDetail(mineNews.getFriendDynamicId());

        readOverMyDynNewMsgApi api = new readOverMyDynNewMsgApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {

            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        pageNo++;
        dynNewMsgList();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        // 下拉刷新
        mBinding.refresh.setEnableLoadMore(true);
        pageNo = 1;
        mineNewsList.clear();
        adapter.notifyDataSetChanged();
        dynNewMsgList();
    }
}
