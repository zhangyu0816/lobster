package com.zb.module_card.vm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.dynCancelLikeApi;
import com.zb.lib_base.api.dynDoLikeApi;
import com.zb.lib_base.api.dynPiazzaListApi;
import com.zb.lib_base.api.otherInfoApi;
import com.zb.lib_base.api.personOtherDynApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.CollectID;
import com.zb.lib_base.model.DiscoverInfo;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DownLoad;
import com.zb.lib_base.views.GoodView;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_card.R;
import com.zb.module_card.adapter.CardAdapter;
import com.zb.module_card.databinding.CardMemberDiscoverBinding;
import com.zb.module_card.iv.MemberDiscoverVMInterface;

import java.io.File;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

public class MemberDiscoverViewModel extends BaseViewModel implements MemberDiscoverVMInterface, OnRefreshListener, OnLoadMoreListener {

    public CardAdapter adapter;
    private int pageNo = 1;
    private List<DiscoverInfo> discoverInfoList = new ArrayList<>();
    private CardMemberDiscoverBinding mBinding;
    private BaseReceiver publishReceiver;
    public long otherUserId;
    private MineInfo mineInfo;
    private MemberInfo memberInfo;
    private int prePosition = -1;
    private BaseReceiver doGoodReceiver;
    private BaseReceiver finishRefreshReceiver;
    private BaseReceiver mainSelectReceiver;
    private BaseReceiver locationReceiver;
    private long friendDynId = 0;
    private DiscoverInfo discoverInfo;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (CardMemberDiscoverBinding) binding;
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
        publishReceiver.unregisterReceiver();
        doGoodReceiver.unregisterReceiver();
        finishRefreshReceiver.unregisterReceiver();
        mainSelectReceiver.unregisterReceiver();
        locationReceiver.unregisterReceiver();
    }

    @Override
    public void setAdapter() {
        adapter = new CardAdapter<>(activity, R.layout.item_card_discover, discoverInfoList, this);
        if (otherUserId == 0)
            getData();
        else {
            if (otherUserId == 1) {
                mineInfo = mineInfoDb.getMineInfo();
                getData();
            } else {
                otherInfo();
            }
        }
    }


    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        // 上拉加载更多
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
        onRefreshForNet(null);
    }

    @Override
    public void dynPiazzaList() {
        dynPiazzaListApi api = new dynPiazzaListApi(new HttpOnNextListener<List<DiscoverInfo>>() {
            @Override
            public void onNext(List<DiscoverInfo> o) {
                mBinding.noNetLinear.setVisibility(View.GONE);
                mBinding.refresh.setVisibility(View.VISIBLE);
                for (DiscoverInfo item : o) {
                    DownLoad.downImageFile(item.getImages().isEmpty() ? item.getImage() : item.getImages().split(",")[0], filePath -> {
                        int start = discoverInfoList.size();
                        Bitmap bitmap = BitmapFactory.decodeFile(new File(filePath).toString());
                        try {
                            item.setWidth(bitmap.getWidth());
                            item.setHeight(bitmap.getHeight());
                        } catch (Exception e) {

                        }
                        discoverInfoList.add(item);
                        adapter.notifyItemRangeChanged(start, discoverInfoList.size());
                    });
                }
                mBinding.refresh.finishRefresh();
                mBinding.refresh.finishLoadMore();
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof UnknownHostException || e instanceof SocketTimeoutException || e instanceof ConnectException) {
                    mBinding.noNetLinear.setVisibility(View.VISIBLE);
                    mBinding.refresh.setVisibility(View.GONE);
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
                .setDynType(1)
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
                for (DiscoverInfo item : o) {
                    if (otherUserId == 1) {
                        item.setNick(mineInfo.getNick());
                        item.setImage(mineInfo.getImage());
                    } else {
                        item.setNick(memberInfo.getNick());
                        item.setImage(memberInfo.getImage());
                    }
                    DownLoad.downImageFile(item.getImages().isEmpty() ? item.getImage() : item.getImages().split(",")[0], filePath -> {
                        int start = discoverInfoList.size();
                        Bitmap bitmap = BitmapFactory.decodeFile(new File(filePath).toString());
                        try {
                            item.setWidth(bitmap.getWidth());
                            item.setHeight(bitmap.getHeight());
                        } catch (Exception e) {
                            Log.e("filePath", "filePath == " + filePath);
                        }

                        discoverInfoList.add(item);
                        adapter.notifyItemRangeChanged(start, discoverInfoList.size());
                    });
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
                .setDynType(2)
                .setOtherUserId(otherUserId == 1 ? BaseActivity.userId : otherUserId)
                .setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void otherInfo() {
        otherInfoApi api = new otherInfoApi(new HttpOnNextListener<MemberInfo>() {
            @Override
            public void onNext(MemberInfo o) {
                memberInfo = o;
                getData();
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof UnknownHostException || e instanceof SocketTimeoutException || e instanceof ConnectException) {
                    mBinding.noNetLinear.setVisibility(View.VISIBLE);
                    mBinding.refresh.setEnableLoadMore(false);
                    mBinding.refresh.finishRefresh();
                    mBinding.refresh.finishLoadMore();
                }
            }
        }, activity).setOtherUserId(otherUserId);
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
