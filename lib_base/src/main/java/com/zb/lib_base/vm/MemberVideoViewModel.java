package com.zb.lib_base.vm;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.R;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.adapter.BaseAdapter;
import com.zb.lib_base.api.dynCancelLikeApi;
import com.zb.lib_base.api.dynDoLikeApi;
import com.zb.lib_base.api.dynPiazzaListApi;
import com.zb.lib_base.api.personOtherDynApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.databinding.CardMemberVideoBinding;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.iv.MemberVideoVMInterface;
import com.zb.lib_base.model.CollectID;
import com.zb.lib_base.model.DiscoverInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.views.GoodView;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

public class MemberVideoViewModel extends BaseViewModel implements MemberVideoVMInterface, OnRefreshListener, OnLoadMoreListener {
    public BaseAdapter adapter;
    public long otherUserId;
    private int pageNo = 1;
    private List<DiscoverInfo> discoverInfoList = new ArrayList<>();
    private CardMemberVideoBinding mBinding;
    private BaseReceiver publishReceiver;
    private int prePosition = -1;
    private BaseReceiver doGoodReceiver;
    private BaseReceiver locationReceiver;
    private long friendDynId = 0;
    private DiscoverInfo discoverInfo;
    private boolean isMore = true;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (CardMemberVideoBinding) binding;
        publishReceiver = new BaseReceiver(activity, "lobster_publish") {
            @Override
            public void onReceive(Context context, Intent intent) {
                onRefreshForNet(null);
            }
        };
        doGoodReceiver = new BaseReceiver(activity, "lobster_doGood") {
            @Override
            public void onReceive(Context context, Intent intent) {
                long friendDynId = intent.getLongExtra("friendDynId", 0);
                int goodNum = intent.getIntExtra("goodNum", 0);
                for (int i = 0; i < discoverInfoList.size(); i++) {
                    if (discoverInfoList.get(i).getFriendDynId() == friendDynId) {
                        discoverInfoList.get(i).setGoodNum(goodNum);
                        adapter.notifyItemChanged(i);
                        break;
                    }
                }
            }
        };

        // 位置漫游
        locationReceiver = new BaseReceiver(activity, "lobster_location") {
            @Override
            public void onReceive(Context context, Intent intent) {
                onRefreshForNet(null);
            }
        };

        mBinding.ivNoData.setBackgroundResource(otherUserId == 1 ? R.mipmap.my_no_discover_data : R.mipmap.other_no_discover_data);
    }

    public void onDestroy() {
        try {
            publishReceiver.unregisterReceiver();
            doGoodReceiver.unregisterReceiver();
            locationReceiver.unregisterReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setAdapter() {
        adapter = new BaseAdapter<>(activity, R.layout.item_card_video, discoverInfoList, this);
        getData();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        // 上拉加载更多
        isMore = true;
        pageNo++;
        getData();
    }

    private void getData() {
        if (otherUserId == 0) {
            dynPiazzaList();
        } else {
            personOtherDyn();
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        isMore = false;
        pageNo++;
        getData();
    }

    @Override
    public void dynPiazzaList() {
        dynPiazzaListApi api = new dynPiazzaListApi(new HttpOnNextListener<List<DiscoverInfo>>() {
            @Override
            public void onNext(List<DiscoverInfo> o) {
                mBinding.noNetLinear.setVisibility(View.GONE);
                if (isMore) {
                    int start = discoverInfoList.size();
                    discoverInfoList.addAll(o);
                    adapter.notifyItemRangeChanged(start, discoverInfoList.size());
                } else {
                    discoverInfoList.addAll(0, o);
                    adapter.notifyItemRangeChanged(0, discoverInfoList.size());
                }

                mBinding.refresh.finishRefresh();
                mBinding.refresh.finishLoadMore();
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof UnknownHostException || e instanceof SocketTimeoutException || e instanceof ConnectException) {
                    mBinding.noNetLinear.setVisibility(View.VISIBLE);
                    mBinding.refresh.setEnableLoadMore(false);
                    mBinding.refresh.finishRefresh();
                    mBinding.refresh.finishLoadMore();
                } else if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    mBinding.refresh.setEnableLoadMore(false);
                    mBinding.refresh.finishRefresh();
                    mBinding.refresh.finishLoadMore();
                }
            }
        }, activity)
                .setCityId(areaDb.getCityId(MineApp.cityName))
                .setDynType(2)
                .setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void personOtherDyn() {
        personOtherDynApi api = new personOtherDynApi(new HttpOnNextListener<List<DiscoverInfo>>() {
            @Override
            public void onNext(List<DiscoverInfo> o) {
                mBinding.noNetLinear.setVisibility(View.GONE);
                mBinding.ivNoData.setVisibility(View.GONE);
                if (isMore) {
                    int start = discoverInfoList.size();
                    discoverInfoList.addAll(o);
                    adapter.notifyItemRangeChanged(start, discoverInfoList.size());
                } else {
                    discoverInfoList.addAll(0, o);
                    adapter.notifyItemRangeChanged(0, discoverInfoList.size());
                }
                mBinding.refresh.finishRefresh();
                mBinding.refresh.finishLoadMore();
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof UnknownHostException || e instanceof SocketTimeoutException || e instanceof ConnectException) {
                    mBinding.noNetLinear.setVisibility(View.VISIBLE);
                    mBinding.refresh.setEnableLoadMore(false);
                    mBinding.refresh.finishRefresh();
                    mBinding.refresh.finishLoadMore();
                } else if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    mBinding.refresh.setEnableLoadMore(false);
                    mBinding.refresh.finishRefresh();
                    mBinding.refresh.finishLoadMore();
                    if (discoverInfoList.size() == 0) {
                        mBinding.ivNoData.setVisibility(View.VISIBLE);
                    }
                }
            }
        }, activity)
                .setDynType(3)
                .setOtherUserId(otherUserId == 1 ? BaseActivity.userId : otherUserId)
                .setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void onRefreshForNet(View view) {
        // 下拉刷新
        mBinding.noNetLinear.setVisibility(View.GONE);
        mBinding.refresh.setEnableLoadMore(true);
        pageNo = 1;
        discoverInfoList.clear();
        adapter.notifyDataSetChanged();
        getData();
    }

    @Override
    public void clickItem(int position) {
        prePosition = position;
        DiscoverInfo discoverInfo = discoverInfoList.get(position);
        if (discoverInfo.getVideoUrl().isEmpty())
            ActivityUtils.getHomeDiscoverDetail(discoverInfo.getFriendDynId());
        else {
            if (otherUserId == 0) {
                MineApp.discoverInfoList.clear();
                MineApp.discoverInfoList.addAll(discoverInfoList);
                ActivityUtils.getHomeVideoList(position, pageNo);
            } else {
                ActivityUtils.getHomeDiscoverVideoL2(discoverInfo.getFriendDynId());
            }
        }
    }

    @Override
    public void doLike(View view, int position) {
        prePosition = position;
        discoverInfo = discoverInfoList.get(position);
        friendDynId = discoverInfo.getFriendDynId();

        GoodView goodView = (GoodView) view;

        if (goodDb.hasGood(discoverInfo.getFriendDynId())) {
            goodView.playUnlike();
            dynCancelLike();
        } else {
            goodView.playLike();
            dynDoLike();
        }
    }

    @Override
    public void dynDoLike() {
        dynDoLikeApi api = new dynDoLikeApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                goodDb.saveGood(new CollectID(friendDynId));
                discoverInfo.setGoodNum(discoverInfo.getGoodNum() + 1);
                adapter.notifyItemChanged(prePosition);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == 0) {
                    if (TextUtils.equals(e.getMessage(), "已经赞过了")) {
                        goodDb.saveGood(new CollectID(friendDynId));
                        adapter.notifyItemChanged(prePosition);
                    }
                }
            }
        }, activity).setFriendDynId(friendDynId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void dynCancelLike() {
        dynCancelLikeApi api = new dynCancelLikeApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                goodDb.deleteGood(friendDynId);
                discoverInfo.setGoodNum(discoverInfo.getGoodNum() - 1);
                adapter.notifyItemChanged(prePosition);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == 0) {
                    if (TextUtils.equals(e.getMessage(), "已经取消过")) {
                        goodDb.deleteGood(friendDynId);
                        adapter.notifyItemChanged(prePosition);
                    }
                }
            }
        }, activity).setFriendDynId(friendDynId);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
