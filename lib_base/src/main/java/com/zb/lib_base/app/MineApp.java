package com.zb.lib_base.app;

import android.content.Context;
import android.graphics.Typeface;

import com.alibaba.android.arouter.launcher.ARouter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.zb.lib_base.R;
import com.zb.lib_base.adaptive.FitScreen;
import com.zb.lib_base.model.RegisterInfo;
import com.zb.lib_base.model.VipInfo;
import com.zb.lib_base.utils.DisplayUtils;
import com.zb.lib_base.utils.UIUtils;
import com.zb.lib_base.views.CutImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MineApp extends MultiDexApplication {
    /**
     * 上下文
     */
    private static MineApp instance;
    public static RegisterInfo registerInfo = new RegisterInfo();
    public static int W;
    public static int H;
    public static String PHONE_NUMBER_REG = "^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$";
    public static Map<String, Integer> selectMap = new HashMap<>();
    public static Map<String, CutImageView> cutImageViewMap = new HashMap<>();
    public static Typeface type;
    public static Typeface simplifiedType;
    public static List<VipInfo> vipInfoList = new ArrayList<>();
    public static String logo = "http://img01.zuwo.la/img/A/YMXXXX919714-206348_YM0000.jpg";
    public static String logo1 = "http://img01.zuwo.la/img/A/YMXXXX2350392-sgjdwurnll_YM0000.jpg";
    public static String cityName = "";

    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
            layout.setPrimaryColorsId(R.color.black_efe, R.color.black_252);//全局设置主题颜色
            return new ClassicsHeader(context);
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> new ClassicsFooter(context).setDrawableSize(20));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        W = getApplicationContext().getResources().getDisplayMetrics().widthPixels;
        H = getApplicationContext().getResources().getDisplayMetrics().heightPixels;
        type = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/semibold.ttf");
        simplifiedType = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/simplified.ttf");
        initRouter(this);
        initRealm();
        DisplayUtils.init(this);
        MultiDex.install(this);
        FitScreen.createDesign(getApplicationContext(), H, W);

        for (int i = 0; i < 10; i++) {
            VipInfo vipInfo = new VipInfo();
            vipInfo.setPrice((i + 1) * 10);
            vipInfo.setOriginalPrice((i + 1) * 12);
            vipInfo.setDayCount((i + 1) * 30);
            vipInfoList.add(vipInfo);
        }
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
    private void initRealm() {
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }
}
