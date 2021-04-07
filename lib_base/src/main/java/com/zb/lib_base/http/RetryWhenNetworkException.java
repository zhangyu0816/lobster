package com.zb.lib_base.http;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * retry条件
 * Created by WZG on 2016/10/17.
 */
public class RetryWhenNetworkException implements Func1<Observable<? extends Throwable>, Observable<?>> {
    //    retry次数
    private int count = 3;
    //    延迟
    private long delay = 3000;
    //    叠加延迟
    private long increaseDelay = 9000;

    public RetryWhenNetworkException() {

    }

    @Override
    public Observable<?> call(Observable<? extends Throwable> observable) {
        return observable
                .zipWith(Observable.range(1, count + 1), (Func2<Throwable, Integer, Wrapper>) Wrapper::new).flatMap((Func1<Wrapper, Observable<?>>) wrapper -> {
                    if ((wrapper.throwable instanceof ConnectException || wrapper.throwable instanceof SocketTimeoutException
                            || wrapper.throwable instanceof TimeoutException)
                            && wrapper.index < count + 1) { //如果超出重试次数也抛出错误，否则默认是会进入onCompleted
                        return Observable.timer(delay + (wrapper.index - 1) * increaseDelay, TimeUnit.MILLISECONDS);
                    }
                    return Observable.error(wrapper.throwable);
                });
    }

    private static class Wrapper {
        private int index;
        private Throwable throwable;

        public Wrapper(Throwable throwable, int index) {
            this.index = index;
            this.throwable = throwable;
        }
    }

}
