package com.zb.module_home.windows;

import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.api.dynDoReviewApi;
import com.zb.lib_base.api.seeReviewsApi;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.Review;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.windows.BasePopupWindow;
import com.zb.module_home.BR;
import com.zb.module_home.R;
import com.zb.module_home.adapter.HomeAdapter;
import com.zb.module_home.databinding.PwsHomeReviewBinding;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class ReviewPW extends BasePopupWindow implements OnRefreshListener, OnLoadMoreListener {

    public HomeAdapter adapter;
    private List<Review> reviewList = new ArrayList<>();
    private PwsHomeReviewBinding binding;
    private int pageNo = 1;
    private long friendDynId;
    private long reviewId;

    public ReviewPW(RxAppCompatActivity activity, View parentView, long friendDynId) {
        super(activity, parentView, false);
        this.friendDynId = friendDynId;
        initUI();
    }

    @Override
    public int getRes() {
        return R.layout.pws_home_review;
    }

    @Override
    public void initUI() {
        mBinding.setVariable(BR.pw, this);
        mBinding.setVariable(BR.name, "");
        mBinding.setVariable(BR.content, "");
        binding = (PwsHomeReviewBinding) mBinding;
        adapter = new HomeAdapter<>(activity, R.layout.item_review, reviewList, this);
        pageNo = 1;
        seeReviews();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        pageNo++;
        seeReviews();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        // 下拉刷新
        binding.refresh.setEnableLoadMore(true);
        pageNo = 1;
        reviewList.clear();
        seeReviews();
    }

    private void seeReviews() {
        seeReviewsApi api = new seeReviewsApi(new HttpOnNextListener<List<Review>>() {
            @Override
            public void onNext(List<Review> o) {
                int start = reviewList.size();
                reviewList.addAll(o);
                adapter.notifyItemRangeChanged(start, reviewList.size());
                binding.refresh.finishLoadMore();
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    binding.refresh.setEnableLoadMore(false);
                    binding.refresh.finishLoadMore();
                }
            }
        }, activity).setFriendDynId(friendDynId).setTimeSortType(1).setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    public void close(View view) {
        dismiss();
    }

    public void sendReview(View view) {
        if (binding.getContent().isEmpty()) {
            SCToastUtil.showToastBlack(activity, "请输入评论内容");
            return;
        }
        dynDoReviewApi api = new dynDoReviewApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                SCToastUtil.showToastBlack(activity, "发布成功");
                binding.setContent("");
                // 下拉刷新
                onRefresh(binding.refresh);
            }
        }, activity).setFriendDynId(friendDynId).setText(binding.getContent()).setReviewId(reviewId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    public void selectReview(Review review) {
        reviewId = reviewId == review.getReviewId() ? 0 : review.getReviewId();
        mBinding.setVariable(BR.name, reviewId == 0 ? "" : review.getNick());
    }


}
