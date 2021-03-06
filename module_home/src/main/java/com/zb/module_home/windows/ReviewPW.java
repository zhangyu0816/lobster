package com.zb.module_home.windows;

import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.api.dynDoReviewApi;
import com.zb.lib_base.api.seeReviewsApi;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.Review;
import com.zb.lib_base.utils.ActivityUtils;
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

    private HomeAdapter adapter;
    private List<Review> reviewList = new ArrayList<>();
    private PwsHomeReviewBinding binding;
    private int pageNo = 1;
    private long friendDynId;
    private long reviewId;
    private CallBack callBack;
    private int reviews;
    private long mainId;

    public ReviewPW(View parentView, long friendDynId, int reviews, CallBack callBack) {
        super(parentView, false);
        this.friendDynId = friendDynId;
        this.callBack = callBack;
        this.reviews = reviews;
        initUI();
    }

    public ReviewPW setMainId(long mainId) {
        this.mainId = mainId;
        return this;
    }

    @Override
    public int getRes() {
        return R.layout.pws_home_review;
    }

    @Override
    public void initUI() {
        adapter = new HomeAdapter<>(activity, R.layout.item_review, reviewList, this);
        mBinding.setVariable(BR.pw, ReviewPW.this);
        mBinding.setVariable(BR.name, "");
        mBinding.setVariable(BR.content, "");
        mBinding.setVariable(BR.adapter, adapter);
        mBinding.setVariable(BR.reviews, reviews);
        binding = (PwsHomeReviewBinding) mBinding;
        seeReviews();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        pageNo++;
        seeReviews();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        // ????????????
        binding.refresh.setEnableLoadMore(true);
        pageNo = 1;
        reviewList.clear();
        adapter.notifyDataSetChanged();
        seeReviews();
    }

    private void seeReviews() {
        seeReviewsApi api = new seeReviewsApi(new HttpOnNextListener<List<Review>>() {
            @Override
            public void onNext(List<Review> o) {
                int start = reviewList.size();
                binding.tvNoData.setVisibility(View.GONE);
                binding.refresh.setVisibility(View.VISIBLE);
                for (Review item : o) {
                    item.setMainId(mainId);
                }
                if(start>0)
                    start--;
                reviewList.addAll(o);
                adapter.notifyItemRangeChanged(start, reviewList.size());
                binding.refresh.finishLoadMore();
                binding.refresh.finishRefresh();
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    binding.refresh.setEnableLoadMore(false);
                    binding.refresh.finishLoadMore();
                    binding.refresh.finishRefresh();
                    if (reviewList.size() == 0) {
                        binding.tvNoData.setVisibility(View.VISIBLE);
                        binding.refresh.setVisibility(View.GONE);
                    }
                }
            }
        }, activity).setFriendDynId(friendDynId).setTimeSortType(1).setPageNo(pageNo).setRow(10);
        HttpManager.getInstance().doHttpDeal(api);
    }

    public void close(View view) {
        dismiss();
    }

    public void sendReview(View view) {
        if (binding.getContent().isEmpty()) {
            SCToastUtil.showToast(activity, "?????????????????????", true);
            return;
        }
        dynDoReviewApi api = new dynDoReviewApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                SCToastUtil.showToast(activity, "????????????", true);
                binding.setContent("");
                callBack.success();
                reviews = reviews + 1;
                mBinding.setVariable(BR.reviews, reviews);
                // ????????????
                onRefresh(binding.refresh);
            }
        }, activity).setFriendDynId(friendDynId).setText(binding.getContent()).setReviewId(reviewId);
        api.setDialogTitle("????????????");
        HttpManager.getInstance().doHttpDeal(api);
    }

    public void selectReview(Review review) {
        reviewId = reviewId == review.getReviewId() ? 0 : review.getReviewId();
        mBinding.setVariable(BR.name, reviewId == 0 ? "" : review.getNick());
    }

    public void toMemberDetail(Review review) {
        if (review.getUserId() != BaseActivity.userId)
            ActivityUtils.getCardMemberDetail(review.getUserId(), false);
    }

    public interface CallBack {
        void success();
    }

}
