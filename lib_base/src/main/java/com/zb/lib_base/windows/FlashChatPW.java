package com.zb.lib_base.windows;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.zb.lib_base.BR;
import com.zb.lib_base.R;
import com.zb.lib_base.databinding.PwsFlashChatBinding;
import com.zb.lib_base.model.RecommendInfo;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.utils.glide.BlurTransformation;
import com.zb.lib_base.utils.glide.GlideRoundTransform;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FlashChatPW extends BasePopupWindow {
    private RecommendInfo recommendInfo;
    private PwsFlashChatBinding binding;
    private RequestOptions cropOptions;
    private MultiTransformation<Bitmap> multiTransformation;

    public FlashChatPW(View parentView, RecommendInfo recommendInfo) {
        super(parentView, false);
        this.recommendInfo = recommendInfo;
        initUI();
    }

    @Override
    public int getRes() {
        return R.layout.pws_flash_chat;
    }

    @SuppressLint("CheckResult")
    @Override
    public void initUI() {
        binding = (PwsFlashChatBinding) mBinding;
        mBinding.setVariable(BR.pw, this);
        mBinding.setVariable(BR.recommendInfo, recommendInfo);

        cropOptions = new RequestOptions();
        multiTransformation = new MultiTransformation<>(new CenterCrop(), new BlurTransformation(), new GlideRoundTransform(12, 0));
        cropOptions.transform(multiTransformation);
        Glide.with(activity).asBitmap().load(recommendInfo.getSingleImage()).apply(cropOptions).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                binding.ivBg.setImageBitmap(resource);
            }
        });
    }

    @Override
    public void cancel(View view) {
        super.cancel(view);
        cropOptions = null;
        multiTransformation = null;
        dismiss();
    }

    public void toChat(View view) {
        SCToastUtil.showToast(activity, "闪聊", true);
    }
}
