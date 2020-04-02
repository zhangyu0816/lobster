package com.zb.lib_base.app;

import android.app.Application;
import android.content.Context;

import com.alibaba.android.arouter.launcher.ARouter;
import com.zb.lib_base.model.RegisterInfo;
import com.zb.lib_base.utils.UIUtils;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MineApp extends Application {
    /**
     * 上下文
     */
    private static MineApp instance;
    public static RegisterInfo registerInfo = new RegisterInfo();
    public static int W;
    public static int H;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        W = getApplicationContext().getResources().getDisplayMetrics().widthPixels;
        H = getApplicationContext().getResources().getDisplayMetrics().heightPixels;

        initRouter(this);
        initRealm();
    }

    public static Context getInstance() {
        return instance;
    }

    // 初始化路由
    private void initRouter(MineApp mApplication) {
        // 这两行必须写在init之前，否则这些配置在init过程中将无效
        if (UIUtils.isApkInDebug(instance)) {
            //打印日志
            ARouter.openLog();
            //开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！
            //线上版本需要关闭,否则有安全风险)
            ARouter.openDebug();
        }
        // 尽可能早，推荐在Application中初始化
        ARouter.init(mApplication);
    }

    // 初始化数据库
    private void initRealm(){
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }
}
