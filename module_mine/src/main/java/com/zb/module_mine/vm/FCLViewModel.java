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
import com.zb.lib_base.api.otherConcernsApi;
import com.zb.lib_base.api.otherFansApi;
import com.zb.lib_base.api.relievePairApi;
import com.zb.lib_base.api.visitorBySeeMeListApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.AttentionDb;
import com.zb.lib_base.db.LikeDb;
import com.zb.lib_base.db.LikeTypeDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.AttentionInfo;
import com.zb.lib_base.model.CollectID;
import com.zb.lib_base.model.LikeMe;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.SuperLikePW;
import com.zb.lib_base.windows.TextPW;
import com.zb.lib_base.windows.VipAdPW;
import com.zb.module_mine.R;
import com.zb.module_mine.adapter.MineAdapter;
import com.zb.module_mine.databinding.MineFclBinding;
import com.zb.module_mine.iv.FCLVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

public class FCLViewModel extends BaseViewModel implements FCLVMInterface, OnRefreshListener, OnLoadMoreListener {
    public int position;
    public long otherUserId;
    public MineAdapter adapter;
    private List<MemberInfo> memberInfoList = new ArrayList<>();
    private int pageNo = 1;
    private MineFclBinding mBinding;
    private int _selectIndex = -1;
    private BaseReceiver attentionListReceiver;
    private BaseReceiver updateFCLReceiver;

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (MineFclBinding) binding;
        setAdapter();
        attentionListReceiver = new BaseReceiver(activity, "lobster_attentionList") {
            @Override
            public void onReceive(Context context, Intent intent) {
                adapter.notifyItemChanged(_selectIndex);
            }
        };
        updateFCLReceiver = new BaseReceiver(activity, "lobster_updateFCL") {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (_selectIndex != -1)
                    adapter.notifyItemChanged(_selectIndex);
            }
        };
    }

    public void onDestroy() {
        try {
            attentionListReceiver.unregisterReceiver();
            updateFCLReceiver.unregisterReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        adapter.userIdList.clear();
        getData();
    }

    @Override
    public void selectPosition(int position) {
        super.selectPosition(position);
        _selectIndex = position;
        ActivityUtils.getCardMemberDetail(memberInfoList.get(position).getUserId(), false);
    }

    private void getData() {
        if (position == 0) {
            if (otherUserId == 0)
                myConcerns();
            else
                otherConcerns();
        } else if (position == 1) {
            if (otherUserId == 0)
                myFans();
            else
                otherFans();
        } else if (position == 2) {
            likeMeList();
        } else {
            visitorBySeeMeList();
        }
    }

    @Override
    public void clickMember(int selectIndex) {
        _selectIndex = selectIndex;
        long otherUserId = memberInfoList.get(selectIndex).getUserId();
        if (position == 2) {
            // 被喜欢
            if (LikeDb.getInstance().hasLike(otherUserId)) {
                new TextPW(mBinding.getRoot(), "解除匹配关系", "解除匹配关系后，将对方移除匹配列表及聊天列表。",
                        "解除", () -> relievePair(otherUserId));
            } else {
                makeEvaluate(otherUserId);
            }
        } else {
            // 我的关注  我的粉丝
            if (AttentionDb.getInstance().isAttention(otherUserId)) {
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
                AttentionDb.getInstance().saveAttention(new AttentionInfo(otherUserId, memberInfo.getNick(), memberInfo.getImage(), true, BaseActivity.userId));
                Intent data = new Intent("lobster_attentionList");
                data.putExtra("isAdd", true);
                activity.sendBroadcast(data);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.ERROR) {
                    if (e.getMessage().equals("已经关注过")) {
                        MemberInfo memberInfo = memberInfoList.get(_selectIndex);
                        memberInfo.setFansQuantity(memberInfo.getFansQuantity() + 1);
                        AttentionDb.getInstance().saveAttention(new AttentionInfo(otherUserId, memberInfo.getNick(), memberInfo.getImage(), true, BaseActivity.userId));
                        activity.sendBroadcast(new Intent("lobster_attentionList"));
                    }
                }
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
                AttentionDb.getInstance().saveAttention(new AttentionInfo(otherUserId, memberInfo.getNick(), memberInfo.getImage(), false, BaseActivity.userId));
                Intent data = new Intent("lobster_attentionList");
                data.putExtra("isAdd", false);
                activity.sendBroadcast(data);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.ERROR) {
                    if (e.getMessage().equals("你还没关注我啊")) {
                        MemberInfo memberInfo = memberInfoList.get(_selectIndex);
                        memberInfo.setFansQuantity(memberInfo.getFansQuantity() - 1);
                        AttentionDb.getInstance().saveAttention(new AttentionInfo(otherUserId, memberInfo.getNick(), memberInfo.getImage(), false, BaseActivity.userId));
                        Intent data = new Intent("lobster_attentionList");
                        data.putExtra("isAdd", false);
                        activity.sendBroadcast(data);
                    }
                }
            }
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void makeEvaluate(long otherUserId) {
        makeEvaluateApi api = new makeEvaluateApi(new HttpOnNextListener<Integer>() {
            @Override
            public void onNext(Integer o) {
                // 1喜欢成功 2匹配成功 3喜欢次数用尽
                String myHead = MineApp.mineInfo.getImage();
                String otherHead = memberInfoList.get(_selectIndex).getImage();
                if (o == 1) {
                    LikeDb.getInstance().saveLike(new CollectID(otherUserId));
                    activity.sendBroadcast(new Intent("lobster_isLike"));
                    LikeTypeDb.getInstance().setType(otherUserId, 1);
                    adapter.notifyItemChanged(_selectIndex);
                } else if (o == 2) {
                    new SuperLikePW(mBinding.getRoot(), myHead, otherHead, MineApp.mineInfo.getSex(), memberInfoList.get(_selectIndex).getSex(), memberInfoList.get(_selectIndex).getNick(),
                            () -> ActivityUtils.getChatActivity(memberInfoList.get(_selectIndex).getUserId(), false));
                    LikeDb.getInstance().saveLike(new CollectID(otherUserId));
                    adapter.notifyItemChanged(_selectIndex);
                    activity.sendBroadcast(new Intent("lobster_pairList"));
                    activity.sendBroadcast(new Intent("lobster_isLike"));
                    LikeTypeDb.getInstance().setType(otherUserId, 1);
                } else if (o == 3) {
                    new VipAdPW(mBinding.getRoot(), 6, "");
                    SCToastUtil.showToast(activity, "今日喜欢次数已用完", true);
                } else if (o == 5) {
                    LikeTypeDb.getInstance().setType(otherUserId, 1);
                    SCToastUtil.showToast(activity, "你已喜欢过对方", true);
                }
            }
        }, activity).setOtherUserId(otherUserId).setLikeOtherStatus(1);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void relievePair(long otherUserId) {
        relievePairApi api = new relievePairApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                LikeTypeDb.getInstance().deleteLikeType(otherUserId);
                if (_selectIndex != -1) {
                    LikeDb.getInstance().deleteLike(otherUserId);
                    adapter.notifyItemRemoved(_selectIndex);
                    memberInfoList.remove(_selectIndex);
                    adapter.notifyDataSetChanged();
                }
                if (memberInfoList.size() == 0) {
                    // 下拉刷新
                    mBinding.refresh.setEnableLoadMore(true);
                    pageNo = 1;
                    memberInfoList.clear();
                    adapter.notifyDataSetChanged();
                    adapter.userIdList.clear();
                    getData();
                }
                Intent data = new Intent("lobster_relieve");
                data.putExtra("otherUserId", otherUserId);
                data.putExtra("isRelieve", true);
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
                for (MemberInfo item : o) {
                    AttentionDb.getInstance().saveAttention(new AttentionInfo(item.getUserId(), item.getNick(), item.getImage(), true, BaseActivity.userId));
                    memberInfoList.add(item);
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
                    if (memberInfoList.size() == 0) {
                        mBinding.setNoData(true);
                    }
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
                    if (memberInfoList.size() == 0) {
                        mBinding.setNoData(true);
                    }
                }
            }
        }, activity).setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void otherConcerns() {
        otherConcernsApi api = new otherConcernsApi(new HttpOnNextListener<List<MemberInfo>>() {
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
                    if (memberInfoList.size() == 0) {
                        mBinding.setNoData(true);
                    }
                }
            }
        }, activity).setPageNo(pageNo).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void otherFans() {
        otherFansApi api = new otherFansApi(new HttpOnNextListener<List<MemberInfo>>() {
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
                    if (memberInfoList.size() == 0) {
                        mBinding.setNoData(true);
                    }
                }
            }
        }, activity).setPageNo(pageNo).setOtherUserId(otherUserId);
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
                    if (memberInfoList.size() == 0) {
                        mBinding.setNoData(true);
                    }
                }
            }
        }, activity).setPageNo(pageNo).setLikeOtherStatus(0);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void visitorBySeeMeList() {
        visitorBySeeMeListApi api = new visitorBySeeMeListApi(new HttpOnNextListener<List<MemberInfo>>() {
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
                    if (memberInfoList.size() == 0) {
                        mBinding.setNoData(true);
                    }
                }
            }
        }, activity).setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
