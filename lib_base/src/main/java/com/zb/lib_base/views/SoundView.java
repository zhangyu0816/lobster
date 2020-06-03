package com.zb.lib_base.views;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.R;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.utils.SCToastUtil;

import java.util.Timer;
import java.util.TimerTask;

public class SoundView {
    private RxAppCompatActivity context;
    private MediaRecorder mediaRecorder;
    private Timer timer;
    private int resTime = 0;
    private AnimationDrawable animationDrawable;
    private String audioPath;
    private int MAX_DURATION = 60;
    private ImageView audioBtn;
    private MediaPlayer mediaPlayer;
    private CallBack callBack;

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public void setResTime(int resTime) {
        this.resTime = resTime;
    }

    @FunctionalInterface
    public interface CallBack {
        default void sendSoundBack(int resTime, String audioPath) {
        }

        void playEndBack(View view);

        default void soundEnd() {
        }
    }

    public SoundView(RxAppCompatActivity context, ImageView audioBtn, CallBack callBack) {
        this.context = context;
        this.audioBtn = audioBtn;
        this.callBack = callBack;
    }

    public SoundView(RxAppCompatActivity context, CallBack callBack) {
        this.context = context;
        this.callBack = callBack;
    }

    /**
     * 语音开始
     */
    public void start() {
        try {
            audioBtn.setVisibility(View.VISIBLE);
            audioBtn.setImageResource(R.drawable.voice_record_anim);
            animationDrawable = (AnimationDrawable) audioBtn.getDrawable();
            animationDrawable.start();

            if (mediaRecorder == null) {
                mediaRecorder = new MediaRecorder();
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder
                        .setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                mediaRecorder
                        .setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                audioPath = BaseActivity.getAudioFile().getAbsolutePath();
                mediaRecorder.setOutputFile(audioPath);
                mediaRecorder.prepare();
                mediaRecorder.start();
                // 按钮按下时创建一个Timer定时器
                timer = new Timer();
                // 创建一个TimerTask
                // TimerTask是个抽象类,实现了Runnable接口，所以TimerTask就是一个子线程
                TimerTask timerTask = new TimerTask() {
                    // 倒数10秒
                    int i = 0;

                    @Override
                    public void run() {
                        // 定义一个消息传过去
                        Message msg = new Message();
                        msg.what = i++;
                        handler.sendMessage(msg);
                    }
                };
                timer.scheduleAtFixedRate(timerTask, 0, 1000);
            }
        } catch (Exception e) {
        }
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            resTime = msg.what;
            if (msg.what == MAX_DURATION || msg.what > MAX_DURATION) {
                SCToastUtil.showToastBlack(context, "最长时间为60秒");
                stop();
            }
            return false;
        }
    });

    /**
     * 语音结束
     */
    public void stop() {
        if (mediaRecorder == null)
            return;
        timer.cancel();
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        audioBtn.setVisibility(View.GONE);
        audioBtn.setImageResource(R.mipmap.voice_anim_1);
        animationDrawable.stop();
        callBack.soundEnd();
        if (resTime < 1) {
            SCToastUtil.showToast(context, "语音录制不能少于1秒");
            resTime = 0;
        } else
            callBack.sendSoundBack(resTime, audioPath);
    }

    /**
     * 语音播放
     *
     * @param msg
     */
    public void soundPlayer(String msg, final View v) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(msg);
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(arg0 -> {
                stopPlayer();
                callBack.playEndBack(v);
            });
        } catch (Exception e) {
        }
    }

    public void stopPlayer() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
