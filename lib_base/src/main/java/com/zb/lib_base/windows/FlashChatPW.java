package com.zb.lib_base.windows;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.zb.lib_base.BR;
import com.zb.lib_base.R;
import com.zb.lib_base.adapter.BaseAdapter;
import com.zb.lib_base.api.otherRentInfoApi;
import com.zb.lib_base.api.saveTalkApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.databinding.PwsFlashChatBinding;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.FlashInfo;
import com.zb.lib_base.model.FlashUser;
import com.zb.lib_base.model.RentInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.utils.glide.BlurTransformation;
import com.zb.lib_base.utils.glide.GlideRoundTransform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class FlashChatPW extends BasePopupWindow {
    private FlashInfo mFlashInfo;
    private PwsFlashChatBinding binding;
    private RequestOptions cropOptions;
    private MultiTransformation<Bitmap> multiTransformation;
    private BaseAdapter mAdapter;
    private List<String> tagList = new ArrayList<>();

    public FlashChatPW(View parentView) {
        super(parentView, false);
        mFlashInfo = MineApp.sFlashInfo;
        initUI();
    }

    @Override
    public int getRes() {
        return R.layout.pws_flash_chat;
    }

    @SuppressLint("CheckResult")
    @Override
    public void initUI() {
        if (!mFlashInfo.getServiceTags().isEmpty()) {
            String tags = mFlashInfo.getServiceTags().substring(1, mFlashInfo.getServiceTags().length() - 1);
            String[] temp = tags.split("#");
            tagList.addAll(Arrays.asList(temp).subList(0, Math.min(3, temp.length)));
        } else {
            otherRentInfo();
        }
        mAdapter = new BaseAdapter<>(activity, R.layout.item_flash_tag, tagList, this);
        binding = (PwsFlashChatBinding) mBinding;
        mBinding.setVariable(BR.pw, this);
        mBinding.setVariable(BR.flashInfo, mFlashInfo);
        mBinding.setVariable(BR.adapter, mAdapter);
        MineApp.getApp().getFixedThreadPool().execute(() -> {
            SystemClock.sleep(300);
            cropOptions = new RequestOptions();
            multiTransformation = new MultiTransformation<>(new CenterCrop(), new BlurTransformation(), new GlideRoundTransform(12, 0));
            cropOptions.transform(multiTransformation);
            activity.runOnUiThread(() -> Glide.with(activity).asBitmap().load(mFlashInfo.getSingleImage()).apply(cropOptions).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    binding.ivBg.setImageBitmap(resource);
                }
            }));
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void otherRentInfo() {
        otherRentInfoApi api = new otherRentInfoApi(new HttpOnNextListener<RentInfo>() {
            @Override
            public void onNext(RentInfo o) {
                String tags = o.getServiceTags().substring(1, o.getServiceTags().length() - 1);
                String[] temp = tags.split("#");
                tagList.addAll(Arrays.asList(temp).subList(0, Math.min(3, temp.length)));
                mAdapter.notifyDataSetChanged();
            }
        }, activity).setOtherUserId(mFlashInfo.getUserId());
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void cancel(View view) {
        super.cancel(view);
        dismiss();
    }

    public void toChat(View view) {
        saveTalkApi api = new saveTalkApi(new HttpOnNextListener<FlashUser>() {
            @Override
            public void onNext(FlashUser o) {
                LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_updateFlash"));
                ActivityUtils.getFlashChatActivity(o.getOtherUserId(), o.getFlashTalkId(), false);
                dismiss();
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.ERROR) {
                    dismiss();
                    if (MineApp.mineInfo.getMemberType() == 1)
                        new VipAdPW(mBinding.getRoot(), 7, "");
                    else
                        SCToastUtil.showToast(activity, e.getMessage(), true);
                }
            }
        }, activity).setOtherUserId(mFlashInfo.getUserId());
        HttpManager.getInstance().doHttpDeal(api);
    }
}
