package com.zb.module_mine.vm;

import android.view.View;

import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.databinding.MineNoticeBinding;
import com.zb.module_mine.iv.NoticeVMInterface;

import androidx.databinding.ViewDataBinding;

public class NoticeViewModel extends BaseViewModel implements NoticeVMInterface {
    public boolean switchChatCheck = false;
    public boolean switchBeLikeCheck = false;
    public boolean switchBeSuperLikeCheck = true;
    public boolean switchBeFollowCheck = false;
    public boolean switchBeRewardCheck = true;
    public boolean switchBeGoodCheck = false;
    public boolean switchBeReviewCheck = true;

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        MineNoticeBinding mBinding = (MineNoticeBinding) binding;

        mBinding.switchChat.setOnCheckedChangeListener((buttonView, isChecked) -> switchChatCheck = isChecked);
        mBinding.switchBeLike.setOnCheckedChangeListener((buttonView, isChecked) -> switchBeLikeCheck = isChecked);
        mBinding.switchBeSuperLike.setOnCheckedChangeListener((buttonView, isChecked) -> switchBeSuperLikeCheck = isChecked);
        mBinding.switchBeFollow.setOnCheckedChangeListener((buttonView, isChecked) -> switchBeFollowCheck = isChecked);
        mBinding.switchBeReward.setOnCheckedChangeListener((buttonView, isChecked) -> switchBeRewardCheck = isChecked);
        mBinding.switchBeGood.setOnCheckedChangeListener((buttonView, isChecked) -> switchBeGoodCheck = isChecked);
        mBinding.switchBeReview.setOnCheckedChangeListener((buttonView, isChecked) -> switchBeReviewCheck = isChecked);
    }
}
