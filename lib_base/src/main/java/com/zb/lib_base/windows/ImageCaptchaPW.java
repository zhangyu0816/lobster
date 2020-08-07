package com.zb.lib_base.windows;

import android.view.View;

import com.bumptech.glide.Glide;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.BR;
import com.zb.lib_base.R;
import com.zb.lib_base.api.findImageCaptchaApi;
import com.zb.lib_base.databinding.PwsImageCapptchaBinding;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.ImageCaptcha;
import com.zb.lib_base.utils.SCToastUtil;

public class ImageCaptchaPW extends BasePopupWindow {

    private ImageCaptcha imageCaptcha;
    private PwsImageCapptchaBinding binding;
    private CallBack callBack;

    public ImageCaptchaPW(RxAppCompatActivity activity, View parentView, CallBack callBack) {
        super(activity, parentView, false);
        this.callBack = callBack;
        initUI();
    }

    @Override
    public int getRes() {
        return R.layout.pws_image_capptcha;
    }

    @Override
    public void initUI() {
        binding = (PwsImageCapptchaBinding) mBinding;
        binding.setCode("");
        mBinding.setVariable(BR.pw,this);
        findImageCaptcha();
    }

    public interface CallBack {
        void success(ImageCaptcha imageCaptcha, String code);

        void fail();
    }

    @Override
    public void sure(View view) {
        super.sure(view);
        if (binding.getCode().length() != 4) {
            SCToastUtil.showToast(activity, "请输入4位有效验证码", true);
            return;
        }
        callBack.success(imageCaptcha, binding.getCode());
        dismiss();
    }

    @Override
    public void cancel(View view) {
        super.cancel(view);
        callBack.fail();
        dismiss();
    }

    @Override
    public void changeImage(View view) {
        super.changeImage(view);
        findImageCaptcha();
    }

    private void findImageCaptcha() {
        findImageCaptchaApi api = new findImageCaptchaApi(new HttpOnNextListener<ImageCaptcha>() {
            @Override
            public void onNext(ImageCaptcha o) {
                binding.setCode("");
                imageCaptcha = o;
                Glide.with(activity).load(imageCaptcha.getImageCaptchaUrl()).into(binding.ivCode);
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
