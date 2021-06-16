package com.yimi.rentme.vm;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;

import com.igexin.sdk.PushManager;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.yimi.rentme.R;
import com.yimi.rentme.databinding.AcLoginVideoBinding;
import com.zb.lib_base.api.myInfoApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import androidx.databinding.ViewDataBinding;

public class LoginVideoViewModel extends BaseViewModel {
    private AcLoginVideoBinding mBinding;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (AcLoginVideoBinding) binding;
        initVideo();
    }

    public void toLogin(View view) {
        // 个推注册
        PushManager.getInstance().initialize(MineApp.instance);

        UMConfigure.preInit(MineApp.instance, "55cac14467e58e8bd7000359", null);
        // 页面统计
        UMConfigure.init(
                MineApp.instance,
                "55cac14467e58e8bd7000359",
                "xiagu",
                UMConfigure.DEVICE_TYPE_PHONE,
                ""
        );

        PlatformConfig.setWeixin("wxb83427622a6740f6", "97f837c0ae8b11af734041828ba4a737");
        PlatformConfig.setQQZone("101928546", "a8d76c68d7590b71f5254aa87c4b24c8");
        UMShareAPI.get(MineApp.instance);

        myInfo();
    }

    @Override
    public void myInfo() {
        myInfoApi api = new myInfoApi(new HttpOnNextListener<MineInfo>() {
            @Override
            public void onNext(MineInfo o) {
                mBinding.videoView.stopPlayback();//停止播放视频,并且释放
                mBinding.videoView.suspend();//在任何状态下释放媒体播放器
                MineApp.mineInfo = o;
                ActivityUtils.getMainActivity();
                activity.finish();
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof SocketTimeoutException || e instanceof ConnectException || e instanceof UnknownHostException) {
                    ActivityUtils.getLoginActivity(0);
                    mBinding.videoView.stopPlayback();//停止播放视频,并且释放
                    mBinding.videoView.suspend();//在任何状态下释放媒体播放器
                    activity.finish();
                }
            }
        }, activity);
        api.setDialogTitle("loadingNotLogin");
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void initVideo() {
        mBinding.videoView.setOnPreparedListener(mp -> {

        });
        //异常回调
        mBinding.videoView.setOnErrorListener((mp, what, extra) -> {
            return true;//如果方法处理了错误，则为true；否则为false。返回false或根本没有OnErrorListener，将导致调用OnCompletionListener。
        });
        mBinding.videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);//让电影循环播放
        });
        //信息回调
        mBinding.videoView.setOnInfoListener((mp, what, extra) -> {
            if (what == MediaPlayer.MEDIA_INFO_UNKNOWN || what == MediaPlayer.MEDIA_INFO_NOT_SEEKABLE) {
                SCToastUtil.showToast(activity, "视频播放失败", true);
                mBinding.videoView.stopPlayback();//停止播放视频,并且释放
                mBinding.videoView.suspend();//在任何状态下释放媒体播放器
                return true;
            } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                // 缓冲结束,此接口每次回调完START就回调END,若不加上判断就会出现缓冲图标一闪一闪的卡顿现象
                mp.isPlaying();
                return true;
            } else if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                mBinding.videoView.setBackgroundColor(Color.TRANSPARENT);
            }
            return false; //如果方法处理了信息，则为true；如果没有，则为false。返回false或根本没有OnInfoListener，将导致丢弃该信息。
        });
        mBinding.videoView.setVideoURI(Uri.parse("android.resource://" + activity.getPackageName() + "/" + R.raw.open));
        mBinding.videoView.start();
    }
}
