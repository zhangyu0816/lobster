package com.zb.module_mine.vm;

import android.view.View;

import com.zb.lib_base.model.FeedbackInfo;
import com.zb.lib_base.utils.MNImage;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.R;
import com.zb.module_mine.adapter.MineAdapter;
import com.zb.module_mine.databinding.MineFeedbackDetailBinding;
import com.zb.module_mine.iv.FeedbackDetailVMInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.databinding.ViewDataBinding;

public class FeedbackDetailViewModel extends BaseViewModel implements FeedbackDetailVMInterface {
    public FeedbackInfo feedbackInfo;
    public MineAdapter adapter;
    private MineFeedbackDetailBinding mBinding;
    public List<String> images = new ArrayList<>();

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (MineFeedbackDetailBinding) binding;
        mBinding.setTitle("反馈详情");
        setAdapter();
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void setAdapter() {
        images.addAll(Arrays.asList(feedbackInfo.getImages().split(",")));
        adapter = new MineAdapter<>(activity, R.layout.item_mine_feedback_image, images, this);
    }

    @Override
    public void selectPosition(int position) {
        super.selectPosition(position);
        ArrayList<String> imageList = new ArrayList<>();
        for (int i = 0; i < (feedbackInfo.getId() == 0 ? (images.size() - 1) : images.size()); i++) {
            imageList.add(images.get(i));
        }
        MNImage.imageBrowser(activity, mBinding.getRoot(), 0, imageList, position, false, null);
    }
}
