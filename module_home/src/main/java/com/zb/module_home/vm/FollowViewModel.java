package com.zb.module_home.vm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.attentionDynApi;
import com.zb.lib_base.api.dynCancelLikeApi;
import com.zb.lib_base.api.dynDoLikeApi;
import com.zb.lib_base.api.otherInfoApi;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.AttentionInfo;
import com.zb.lib_base.model.CollectID;
import com.zb.lib_base.model.DiscoverInfo;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.views.GoodView;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_home.R;
import com.zb.module_home.adapter.HomeAdapter;
import com.zb.module_home.databinding.HomeFollowBinding;
import com.zb.module_home.iv.FollowVMInterface;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

public class FollowViewModel extends BaseViewModel implements FollowVMInterface, OnRefreshListener, OnLoadMoreListener {
    public HomeAdapter adapter;
    private List<DiscoverInfo> discoverInfoList = new ArrayList<>();
    private int pageNo = 1;
    private HomeFollowBinding mBinding;
    private BaseReceiver publishReceiver;
    private BaseReceiver doGoodReceiver;
    private BaseReceiver finishRefreshReceiver;
    private BaseReceiver mainSelectReceiver;
    private BaseReceiver attentionListReceiver;
    private int prePosition = -1;
    private long friendDynId = 0;
    private DiscoverInfo discoverInfo;

    @Override
    public void setAdapter() {
        adapter = new HomeAdapter<>(activity, R.layout.item_home_discover, discoverInfoList, this);
        attentionDyn();
    }

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (HomeFollowBinding) binding;
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
                        if (goodNum != 0) {
                            discoverInfoList.get(i).setGoodNum(goodNum);
                        }
                        adapter.notifyItemChanged(i);
                        break;
                    }
                }
            }
        };

        finishRefreshReceiver = new BaseReceiver(activity, "lobster_finishRefresh") {
            @Override
            public void onReceive(Context context, Intent intent) {
                mBinding.refresh.finishRefresh();
                mBinding.refresh.finishLoadMore();
            }
        };
        mainSelectReceiver = new BaseReceiver(activity, "lobster_mainSelect") {
            @Override
            public void onReceive(Context context, Intent intent) {
                onRefreshForNet(null);
            }
        };
        attentionListReceiver = new BaseReceiver(activity, "lobster_attentionList") {
            @Override
            public void onReceive(Context context, Intent intent) {
                onRefreshForNet(null);
            }
        };
    }

    public void onDestroy() {
        publishReceiver.unregisterReceiver();
        doGoodReceiver.unregisterReceiver();
        finishRefreshReceiver.unregisterReceiver();
        mainSelectReceiver.unregisterReceiver();
        attentionListReceiver.unregisterReceiver();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        // 上拉加载更多
        pageNo++;
        attentionDyn();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        onRefreshForNet(null);
    }

    private int index = 0;
    private int start;

    @Override
    public void attentionDyn() {
        attentionDynApi api = new attentionDynApi(new HttpOnNextListener<List<DiscoverInfo>>() {
            @Override
            public void onNext(List<DiscoverInfo> o) {
                mBinding.noNetLinear.setVisibility(View.GONE);
                start = discoverInfoList.size();
                for (DiscoverInfo item : o) {
                    try {
                        Bitmap bitmap = Glide.with(activity).asBitmap().load(item.getVideoUrl().isEmpty() ? (item.getImages().isEmpty() ? attentionDb.getAttentionInfo(item.getOtherUserId()).getImage() : item.getImages().split(",")[0]) : item.getVideoUrl())
                                .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                        item.setWidth(bitmap.getWidth());
                        item.setHeight(bitmap.getHeight());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    discoverInfoList.add(item);
                }
                setImage();
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof SocketTimeoutException || e instanceof ConnectException || e instanceof UnknownHostException) {
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
        }, activity).setPageNo(pageNo).setTimeSortType(1);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void setImage() {
        DiscoverInfo discoverInfo = discoverInfoList.get(index);
        if (!attentionDb.isAttention(discoverInfo.getOtherUserId())) {
            otherInfoApi api = new otherInfoApi(new HttpOnNextListener<MemberInfo>() {
                @Override
                public void onNext(MemberInfo o) {
                    attentionDb.saveAttention(new AttentionInfo(discoverInfo.getOtherUserId(), o.getNick(), o.getImage(), true, BaseActivity.userId));
                    if (index < discoverInfoList.size() - 1) {
                        index++;
                        setImage();
                    } else {
                        adapter.notifyItemRangeChanged(start, discoverInfoList.size());
                        mBinding.refresh.finishRefresh();
                        mBinding.refresh.finishLoadMore();
                    }
                }
            }, activity).setOtherUserId(discoverInfo.getOtherUserId());
            HttpManager.getInstance().doHttpDeal(api);
        } else {
            if (index < discoverInfoList.size() - 1) {
                index++;
                setImage();
            } else {
                adapter.notifyItemRangeChanged(start, discoverInfoList.size());
                mBinding.refresh.finishRefresh();
                mBinding.refresh.finishLoadMore();
            }
        }
    }

    @Override
    public void onRefreshForNet(View view) {
        // 下拉刷新
        mBinding.noNetLinear.setVisibility(View.GONE);
        mBinding.refresh.setEnableLoadMore(true);
        pageNo = 1;
        index = 0;
        discoverInfoList.clear();
        adapter.notifyDataSetChanged();
        attentionDyn();
    }

    @Override
    public void clickItem(int position) {
        prePosition = position;
        DiscoverInfo discoverInfo = discoverInfoList.get(position);
        if (discoverInfo.getVideoUrl().isEmpty())
            ActivityUtils.getHomeDiscoverDetail(discoverInfo.getFriendDynId());
        else
            ActivityUtils.getHomeDiscoverVideoL2(discoverInfo.getFriendDynId());
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
