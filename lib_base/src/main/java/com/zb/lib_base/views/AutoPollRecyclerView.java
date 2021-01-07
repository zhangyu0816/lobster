package com.zb.lib_base.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewConfiguration;

import com.zb.lib_base.adapter.BindingItemAdapter;
import com.zb.lib_base.utils.glide.ScrollSpeedLinearLayoutManger;

import java.lang.ref.WeakReference;

import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class AutoPollRecyclerView extends RecyclerView {
    private static final long TIME_AUTO_POLL = 10;
    AutoPollTask autoPollTask;
    private boolean running; //标示是否正在自动轮询
    private final int mTouchSlop;

    public AutoPollRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        autoPollTask = new AutoPollTask(this);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    /**
     * 持续滑动（走马灯）
     */
    static class AutoPollTask implements Runnable {
        private final WeakReference<AutoPollRecyclerView> mReference;

        //使用弱引用持有外部类引用->防止内存泄漏
        public AutoPollTask(AutoPollRecyclerView reference) {
            this.mReference = new WeakReference<>(reference);
        }

        @Override
        public void run() {
            AutoPollRecyclerView recyclerView = mReference.get();
            if (recyclerView != null && recyclerView.running) {
                recyclerView.scrollBy(0, 2);
                recyclerView.postDelayed(recyclerView.autoPollTask, TIME_AUTO_POLL);
            }
        }
    }

    //开启:如果正在运行,先停止->再开启
    public void start() {
        if (running)
            stop();
        running = true;
        postDelayed(autoPollTask, TIME_AUTO_POLL);
    }

    public void stop() {
        running = false;
        removeCallbacks(autoPollTask);
    }


    // RecyclerView
    @BindingAdapter("autoAdapter")
    public static void setAdapter(AutoPollRecyclerView view, BindingItemAdapter autoAdapter) {
        autoAdapter.setMax(true);
        view.setAdapter(autoAdapter);
        ScrollSpeedLinearLayoutManger layoutManager1 = new ScrollSpeedLinearLayoutManger(view.getContext());
        layoutManager1.setSmoothScrollbarEnabled(true);
        layoutManager1.setAutoMeasureEnabled(true);
        view.setLayoutManager(layoutManager1);// 布局管理器。
        view.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
    }
}
