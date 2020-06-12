package com.zb.module_mine.vm;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.attentionOtherApi;
import com.zb.lib_base.api.cancelAttentionApi;
import com.zb.lib_base.api.likeMeListApi;
import com.zb.lib_base.api.makeEvaluateApi;
import com.zb.lib_base.api.myConcernsApi;
import com.zb.lib_base.api.myFansApi;
import com.zb.lib_base.api.relievePairApi;
import com.zb.lib_base.db.HistoryMsgDb;
import com.zb.lib_base.db.LikeDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.AttentionInfo;
import com.zb.lib_base.model.CollectID;
import com.zb.lib_base.model.LikeMe;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.SuperLikePW;
import com.zb.lib_base.windows.TextPW;
import com.zb.module_mine.R;
import com.zb.module_mine.adapter.MineAdapter;
import com.zb.module_mine.databinding.MineFclBinding;
import com.zb.module_mine.iv.FCLVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import io.realm.Realm;

public class FCLViewModel extends BaseViewModel implements FCLVMInterface, OnRefreshListener, OnLoadMoreListener {
    public int position;
    public MineAdapter adapter;
    private List<MemberInfo> memberInfoList = new ArrayList<>();
    private int pageNo = 1;
    private MineFclBinding mBinding;
    public LikeDb likeDb;
    private int _selectIndex = -1;
    private MineInfo mineInfo;
    private int prePosition = -1;
    private BaseReceiver attentionReceiver;
    private BaseReceiver finishRefreshReceiver;
    private HistoryMsgDb historyMsgDb;

    @Override
    public void back(View view) {
        super.back(view);
        activity.sendBroadcast(new Intent("lobster_resumeContactNum"));
        activity.finish();
    }

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        likeDb = new LikeDb(Realm.getDefaultInstance());
        historyMsgDb = new HistoryMsgDb(Realm.getDefaultInstance());
        mBinding = (MineFclBinding) binding;
        mineInfo = mineInfoDb.getMineInfo();
        setAdapter();
        finishRefreshReceiver = new BaseReceiver(activity, "lobster_finishRefresh") {
            @Override
            public void onReceive(Context context, Intent intent) {
                mBinding.refresh.finishRefresh();
                mBinding.refresh.finishLoadMore();
            }
        };
        attentionReceiver = new BaseReceiver(activity, "lobster_attention") {
            @Override
            public void onReceive(Context context, Intent intent) {
                adapter.notifyItemChanged(prePosition);
            }
        };
    }

    public void onDestroy() {
        finishRefreshReceiver.unregisterReceiver();
        attentionReceiver.unregisterReceiver();
    }

    @Override
    public void setAdapter() {
        adapter = new MineAdapter<>(activity, R.layout.item_mine_member, memberInfoList, this);
        getData();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        // 上拉加载更多
        pageNo++;
        getData();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        // 下拉刷新
        mBinding.refresh.setEnableLoadMore(true);
        pageNo = 1;
        memberInfoList.clear();
        adapter.notifyDataSetChanged();
        getData();
    }

    @Override
    public void selectPosition(int position) {
        super.selectPosition(position);
        prePosition = position;
        ActivityUtils.getCardMemberDetail(memberInfoList.get(position).getUserId(), false);
    }

    private void getData() {
        if (position == 0) {
            myConcerns();
        } else if (position == 1) {
            myFans();
        } else {
            likeMeList();
        }
    }

    @Override
    public void clickMember(int selectIndex) {
        _selectIndex = selectIndex;
        long otherUserId = memberInfoList.get(selectIndex).getUserId();
        if (position == 2) {
            // 被喜欢
            if (likeDb.hasLike(otherUserId)) {
                new TextPW(activity, mBinding.getRoot(), "解除匹配关系", "解除匹配关系后，将对方移除匹配列表及聊天列表。","解除", () -> {
                    relievePair(otherUserId);
                });
            } else {
                makeEvaluate(otherUserId, 1);
            }
        } else {
            // 我的关注  我的粉丝
            if (attentionDb.isAttention(otherUserId)) {
                cancelAttention(otherUserId);
            } else {
                attentionOther(otherUserId);
            }
        }
    }

    private void attentionOther(long otherUserId) {
        attentionOtherApi api = new attentionOtherApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                MemberInfo memberInfo = memberInfoList.get(_selectIndex);
                memberInfo.setFansQuantity(memberInfo.getFansQuantity() + 1);
                attentionDb.saveAttention(new AttentionInfo(otherUserId, memberInfo.getNick(), memberInfo.getImage(), true, BaseActivity.userId));
                adapter.notifyItemChanged(_selectIndex);
            }
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void cancelAttention(long otherUserId) {
        cancelAttentionApi api = new cancelAttentionApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                MemberInfo memberInfo = memberInfoList.get(_selectIndex);
                memberInfo.setFansQuantity(memberInfo.getFansQuantity() - 1);
                attentionDb.saveAttention(new AttentionInfo(otherUserId, memberInfo.getNick(), memberInfo.getImage(), false, BaseActivity.userId));
                adapter.notifyItemChanged(_selectIndex);
            }
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void makeEvaluate(long otherUserId, int likeOtherStatus) {
        makeEvaluateApi api = new makeEvaluateApi(new HttpOnNextListener<Integer>() {
            @Override
            public void onNext(Integer o) {
                // 1喜欢成功 2匹配成功 3喜欢次数用尽
                String myHead = mineInfo.getImage();
                String otherHead = memberInfoList.get(_selectIndex).getImage();
                if (o == 1) {
                    if (likeOtherStatus == 0) {
                        // 不喜欢返回结果  data=1
                        likeDb.deleteLike(otherUserId);
                    } else {
                        likeDb.saveLike(new CollectID(otherUserId));
                    }
                    adapter.notifyItemChanged(_selectIndex);
                } else if (o == 2) {
                    new SuperLikePW(activity, mBinding.getRoot(), myHead, otherHead, true, mineInfo.getSex(), memberInfoList.get(_selectIndex).getSex());
                    likeDb.saveLike(new CollectID(otherUserId));
                    adapter.notifyItemChanged(_selectIndex);
                    activity.sendBroadcast(new Intent("lobster_pairList"));
                } else if (o == 3) {
                    SCToastUtil.showToastBlack(activity, "今日喜欢次数已用完");
                }
            }
        }, activity).setOtherUserId(otherUserId).setLikeOtherStatus(likeOtherStatus);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void relievePair(long otherUserId) {
        relievePairApi api = new relievePairApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                likeDb.deleteLike(otherUserId);
                historyMsgDb.deleteHistoryMsg(otherUserId);
                adapter.notifyItemChanged(_selectIndex);
                Intent data = new Intent("lobster_relieve");
                data.putExtra("otherUserId", otherUserId);
                activity.sendBroadcast(data);
            }
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void myConcerns() {
        myConcernsApi api = new myConcernsApi(new HttpOnNextListener<List<MemberInfo>>() {
            @Override
            public void onNext(List<MemberInfo> o) {
                int start = memberInfoList.size();
                memberInfoList.addAll(o);
                adapter.notifyItemRangeChanged(start, memberInfoList.size());
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
        }, activity).setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void myFans() {
        myFansApi api = new myFansApi(new HttpOnNextListener<List<MemberInfo>>() {
            @Override
            public void onNext(List<MemberInfo> o) {
                int start = memberInfoList.size();
                memberInfoList.addAll(o);
                adapter.notifyItemRangeChanged(start, memberInfoList.size());
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
        }, activity).setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void likeMeList() {
        likeMeListApi api = new likeMeListApi(new HttpOnNextListener<List<LikeMe>>() {
            @Override
            public void onNext(List<LikeMe> o) {
                int start = memberInfoList.size();
                for (LikeMe likeMe : o) {
                    MemberInfo memberInfo = new MemberInfo();
                    memberInfo.setUserId(likeMe.getUserId());
                    memberInfo.setImage(likeMe.getHeadImage());
                    memberInfo.setNick(likeMe.getNick());
                    memberInfoList.add(memberInfo);
                }
                adapter.notifyItemRangeChanged(start, memberInfoList.size());
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
        }, activity).setPageNo(pageNo).setLikeOtherStatus(1);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
