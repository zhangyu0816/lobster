package com.zb.module_mine.vm;

import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.api.attentionOtherApi;
import com.zb.lib_base.api.cancelAttentionApi;
import com.zb.lib_base.api.contactNumApi;
import com.zb.lib_base.api.likeMeListApi;
import com.zb.lib_base.api.makeEvaluateApi;
import com.zb.lib_base.api.myConcernsApi;
import com.zb.lib_base.api.myFansApi;
import com.zb.lib_base.db.AttentionDb;
import com.zb.lib_base.db.LikeDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.CollectID;
import com.zb.lib_base.model.ContactNum;
import com.zb.lib_base.model.LikeMe;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.CountUsedPW;
import com.zb.lib_base.windows.SuperLikePW;
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
    public AttentionDb attentionDb;
    public LikeDb likeDb;
    private int _selectIndex = -1;
    private MineInfo mineInfo;

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        attentionDb = new AttentionDb(Realm.getDefaultInstance());
        likeDb = new LikeDb(Realm.getDefaultInstance());
        mBinding = (MineFclBinding) binding;
        mineInfo = mineInfoDb.getMineInfo();
        setAdapter();
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
        getData();
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
            makeEvaluate(otherUserId, likeDb.hasLike(otherUserId) ? 0 : 1);
        } else {
            // 我的关注  我的粉丝
            if (attentionDb.hasAttention(otherUserId)) {
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
                attentionDb.saveAttention(new CollectID(otherUserId));
                adapter.notifyItemChanged(_selectIndex);
            }
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void cancelAttention(long otherUserId) {
        cancelAttentionApi api = new cancelAttentionApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                attentionDb.deleteAttention(otherUserId);
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
                String myHead = mineInfo.getMoreImages().split("#")[0];
                String otherHead = memberInfoList.get(_selectIndex).getImage();
                if (o == 1) {
                    if (likeOtherStatus == 0) {
                        // 不喜欢返回结果  data=1
                        likeDb.deleteLike(otherUserId);
                    } else {
                        likeDb.saveLike(new CollectID(otherUserId));
                        new SuperLikePW(activity, mBinding.getRoot(), myHead, otherHead, false, mineInfo.getSex(), memberInfoList.get(_selectIndex).getSex());
                    }
                    adapter.notifyItemChanged(_selectIndex);
                } else if (o == 2) {
                    new SuperLikePW(activity, mBinding.getRoot(), myHead, otherHead, true, mineInfo.getSex(), memberInfoList.get(_selectIndex).getSex());
                    likeDb.saveLike(new CollectID(otherUserId));
                    adapter.notifyItemChanged(_selectIndex);
                } else if (o == 3) {
                    SCToastUtil.showToastBlack(activity, "今日喜欢次数已用完");
                }
            }
        }, activity).setOtherUserId(otherUserId).setLikeOtherStatus(likeOtherStatus);
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
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    mBinding.refresh.setEnableLoadMore(false);
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
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    mBinding.refresh.setEnableLoadMore(false);
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
                    memberInfo.setUserId(likeMe.getOtherUserId());
                    memberInfo.setImage(likeMe.getHeadImage());
                    memberInfo.setNick(likeMe.getNick());
                    memberInfoList.add(memberInfo);
                }
                adapter.notifyItemRangeChanged(start, memberInfoList.size());
                mBinding.refresh.finishRefresh();
                mBinding.refresh.finishLoadMore();
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    mBinding.refresh.setEnableLoadMore(false);
                }
            }
        }, activity).setPageNo(pageNo).setLikeOtherStatus(1);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void getContactNum(MemberInfo memberInfo, int position) {
        contactNumApi api = new contactNumApi(new HttpOnNextListener<ContactNum>() {
            @Override
            public void onNext(ContactNum o) {
                memberInfo.setBeLikeQuantity(o.getBeLikeCount());
                memberInfo.setFansQuantity(o.getFansCount());
                adapter.notifyItemChanged(position);
            }
        }, activity).setOtherUserId(memberInfo.getUserId());
        HttpManager.getInstance().doHttpDeal(api);
    }
}
