package com.zb.lib_base.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;

import androidx.annotation.NonNull;

/**
 * @Author: zt
 * @Time: 2019-11-29 18:30
 * @Description: 常用工具类，初始化
 */
public class Utils {
    private static final ExecutorService UTIL_POOL = ThreadUtils.getCachedPool();
    private static final Handler UTIL_HANDLER = new Handler(Looper.getMainLooper());

    @SuppressLint("StaticFieldLeak")
    private static Application sApplication;

    private Utils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 初始化工具类
     *
     * @param context 上下文
     */
    public static void init(@NonNull final Context context) {
        if (context == null) {
            init(getApplicationByReflect());
            return;
        }
        init((Application) context.getApplicationContext());
    }

    public static void init(final Application app) {
        if (sApplication == null) {
            if (app == null) {
                sApplication = getApplicationByReflect();
            } else {
                sApplication = app;
            }
        }
    }

    private static Application getApplicationByReflect() {
        try {
            @SuppressLint("PrivateApi")
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Object thread = activityThread.getMethod("currentActivityThread").invoke(null);
            Object app = activityThread.getMethod("getApplication").invoke(thread);
            if (app == null) {
                throw new NullPointerException("u should init first");
            }
            return (Application) app;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new NullPointerException("u should init first");
    }

    /**
     * 获取ApplicationContext
     *
     * @return ApplicationContext
     */
    public static Application getContext() {
        if (sApplication != null) {
            return sApplication;
        }
        throw new NullPointerException("should be initialized in application");
    }

    static <T> Task<T> doAsync(final Task<T> task) {
        UTIL_POOL.execute(task);
        return task;
    }

    public static void runOnUiThread(final Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            Utils.UTIL_HANDLER.post(runnable);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // interface
    ///////////////////////////////////////////////////////////////////////////

    public abstract static class Task<Result> implements Runnable {

        private static final int NEW = 0;
        private static final int COMPLETING = 1;
        private static final int CANCELLED = 2;
        private static final int EXCEPTIONAL = 3;

        private volatile int state = NEW;

        abstract Result doInBackground();

        private Callback<Result> mCallback;

        public Task(final Callback<Result> callback) {
            mCallback = callback;
        }

        @Override
        public void run() {
            try {
                final Result t = doInBackground();

                if (state != NEW) return;
                state = COMPLETING;
                UTIL_HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onCall(t);
                    }
                });
            } catch (Throwable th) {
                if (state != NEW) return;
                state = EXCEPTIONAL;
            }
        }

        public void cancel() {
            state = CANCELLED;
        }

        public boolean isDone() {
            return state != NEW;
        }

        public boolean isCanceled() {
            return state == CANCELLED;
        }
    }

    public interface Callback<T> {
        void onCall(T data);
    }

    public interface Func1<Ret, Par> {
        Ret call(Par param);
    }
}
