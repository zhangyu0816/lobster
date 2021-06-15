package com.zb.lib_base.app;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;

import com.alibaba.android.arouter.launcher.ARouter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.xiaomi.mimc.MIMCUser;
import com.zb.lib_base.R;
import com.zb.lib_base.model.ContactNum;
import com.zb.lib_base.model.DiscoverInfo;
import com.zb.lib_base.model.FlashInfo;
import com.zb.lib_base.model.LikeMe;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.model.MineNewsCount;
import com.zb.lib_base.model.RecommendInfo;
import com.zb.lib_base.model.RegisterInfo;
import com.zb.lib_base.model.VideoInfo;
import com.zb.lib_base.model.VipInfo;
import com.zb.lib_base.model.WalletInfo;
import com.zb.lib_base.utils.DisplayUtils;
import com.zb.lib_base.utils.UIUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MineApp extends MultiDexApplication {
    /**
     * 上下文
     */
    public static MineApp instance;
    public static RegisterInfo registerInfo = new RegisterInfo();
    public static int W;
    public static int H;
    public static String PHONE_NUMBER_REG = "^[0-9]{11}$";
    public static String EMAIL_REG = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    public static String HEIGHT_REG = "^[1-2][0-9]{2}$";
    public static MineInfo mineInfo = new MineInfo();
    public static FlashInfo sFlashInfo = new FlashInfo();

    public static Typeface type;
    public static Typeface simplifiedType;
    public static Typeface QingSongShouXieTiType;

    public static String cityName = "";
    public static String versionName;
    public static String WX_PAY_APPID = "wxbdd7128e0a0a08f8";

    public static List<VipInfo> vipInfoList = new ArrayList<>();
    public static List<VideoInfo> videoInfoList = new ArrayList<>();
    public static WalletInfo walletInfo = new WalletInfo();
    public static MineNewsCount mineNewsCount = new MineNewsCount();
    public static ContactNum contactNum = new ContactNum();
    public static List<LikeMe> pairList = new ArrayList<>();
    public static List<RecommendInfo> recommendInfoList = new ArrayList<>();
    public static List<FlashInfo> sFlashInfoList = new ArrayList<>();
    public static int noReadBottleNum = 0;
    public static int chatSelectIndex = 0;
    public static String NOTIFICATION_CHANNEL_ID = "com.yimi.rentme_notice";

    public static boolean isChat = false;
    public static boolean isLocation = false;
    public static boolean showBottom = false;
    public static boolean toPublish = false;
    public static boolean toContinue = false;
    public static int cameraType = 0;
    public static String filePath = "";
    public static boolean isMore = false;
    public static long time = 0;
    public static Map<String, Integer> selectMap = new HashMap<>();

    public static int sex = 0;
    public static int maxAge = 70;
    public static int minAge = 18;
    public static int noDataCount = 0;
    public static List<DiscoverInfo> discoverInfoList = new ArrayList<>();

    public static boolean isFirstOpen = false;

    public ExecutorService fixedThreadPool;

    public ExecutorService getFixedThreadPool() {
        return fixedThreadPool == null ? fixedThreadPool = Executors.newFixedThreadPool(3) : fixedThreadPool;
    }

    public static MIMCUser sMIMCUser;
    public static boolean isLogin = false;
    public static String imUserId = "";

    public static int filmMaxSize = 24;

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
        QingSongShouXieTiType = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/QingSongShouXieTi.ttf");
        initRouter(this);
        initRealm();
        DisplayUtils.init(this);
        MultiDex.install(this);

        try {
            PackageInfo packageInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        fixedThreadPool = Executors.newFixedThreadPool(3);
    }

    public static MineApp getApp() {
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

    public LinkedList<RxAppCompatActivity> mActivityList = new LinkedList<>();
    public Map<String, RxAppCompatActivity> activityMap = new HashMap<>();

    public LinkedList<RxAppCompatActivity> getActivityList() {
        return mActivityList;
    }

    public Map<String, RxAppCompatActivity> getActivityMap() {
        return activityMap;
    }

    /**
     * Activity开启时添加Activity到集合
     *
     * @param activity
     */
    public void addActivity(RxAppCompatActivity activity) {
        mActivityList.addFirst(activity);
    }

    /**
     * Activity退出时清除集合中的Activity.
     *
     * @param oneself 被移除的activity
     */
    public void removeActivity(RxAppCompatActivity oneself) {
        try {
            Iterator<RxAppCompatActivity> iterator = mActivityList.iterator();
            while (iterator.hasNext()) {
                RxAppCompatActivity current = iterator.next();
                if (current != null && current == oneself) {
                    iterator.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 退出应用时调用
     */
    public void exit() {
        for (RxAppCompatActivity activity : mActivityList) {
            if (activity != null) {
                activity.finish();
            }
        }
        mActivityList.clear();
        if (fixedThreadPool != null) {
            fixedThreadPool.shutdown();
        }
        fixedThreadPool = null;
    }


}
