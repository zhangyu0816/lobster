package com.zb.lib_base.windows;

import android.graphics.Bitmap;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.R;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.databinding.DfBigPhotoBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentManager;

public class BigPhotoDF extends BaseDialogFragment {

    private DfBigPhotoBinding mBinding;
    private String imageUrl = "";
    private BigPhotoCallBack mBigPhotoCallBack;

    public BigPhotoDF(RxAppCompatActivity activity) {
        super(activity, false, false);
    }

    public void show(FragmentManager manager) {
        show(manager, "BigPhotoDF");
    }

    public BigPhotoDF setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public BigPhotoDF setBigPhotoCallBack(BigPhotoCallBack bigPhotoCallBack) {
        mBigPhotoCallBack = bigPhotoCallBack;
        return this;
    }

    @Override
    public void onStart() {
        super.onStart();
        cleanPadding();
    }

    @Override
    public int getLayoutId() {
        return R.layout.df_big_photo;
    }

    @Override
    public void setDataBinding(ViewDataBinding viewDataBinding) {
        mBinding = (DfBigPhotoBinding) viewDataBinding;
    }

    @Override
    public void initUI() {
        mBinding.setDialog(this);
        Glide.with(activity).asBitmap().load(imageUrl).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                float width = (float) resource.getWidth();
                float height = (float) resource.getHeight();
                if (width > height) {
                    AdapterBinding.viewSize(mBinding.ivUrl, MineApp.W, (int) (MineApp.W * height / width));
                } else {
                    AdapterBinding.viewSize(mBinding.ivUrl, (int) (MineApp.H * width / height), MineApp.H);
                }
                mBinding.ivUrl.setImageBitmap(resource);
            }
        });
    }

    public void select(View view) {
        mBigPhotoCallBack.select();
        dismiss();
    }

    public void cancel(View view) {
        dismiss();
    }

    public interface BigPhotoCallBack {
        void select();
    }
}
