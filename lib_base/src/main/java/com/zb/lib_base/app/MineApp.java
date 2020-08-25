package com.zb.lib_base.app;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.mobileim.YWAPI;
import com.alibaba.tcms.service.TCMSService;
import com.alibaba.wxlib.util.SysUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.zb.lib_base.R;
import com.zb.lib_base.adaptive.FitScreen;
import com.zb.lib_base.imcore.LoginSampleHelper;
import com.zb.lib_base.log.LogUtil;
import com.zb.lib_base.model.BankInfo;
import com.zb.lib_base.model.ContactNum;
import com.zb.lib_base.model.DiscoverInfo;
import com.zb.lib_base.model.GiftInfo;
import com.zb.lib_base.model.LikeMe;
import com.zb.lib_base.model.MineNewsCount;
import com.zb.lib_base.model.RechargeInfo;
import com.zb.lib_base.model.RecommendInfo;
import com.zb.lib_base.model.RegisterInfo;
import com.zb.lib_base.model.Report;
import com.zb.lib_base.model.VipInfo;
import com.zb.lib_base.model.WalletInfo;
import com.zb.lib_base.utils.DisplayUtils;
import com.zb.lib_base.utils.UIUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
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
    public static String PHONE_NUMBER_REG = "^1[0-9]{10}$";
    public static String EMAIL_REG = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    public static Map<Integer, String> tranTypeMap = new HashMap<>();
    public static Map<String, Integer> selectMap = new HashMap<>();
    public static Typeface type;
    public static Typeface simplifiedType;
    public static Typeface QingSongShouXieTiType;

    public static String cityName = "";
    public static String versionName;
    public static String WX_PAY_APPID = "wxbdd7128e0a0a08f8";
    public static boolean isLogin = false;
    public static Map<Integer, String> tranStatusMap = new HashMap<>();

    public static List<VipInfo> vipInfoList = new ArrayList<>();
    public static List<GiftInfo> giftInfoList = new ArrayList<>();
    public static List<RechargeInfo> rechargeInfoList = new ArrayList<>();
    public static List<BankInfo> bankInfoList = new ArrayList<>();
    public static List<Report> reportList = new ArrayList<>();
    public static WalletInfo walletInfo;
    public static MineNewsCount mineNewsCount;
    public static ContactNum contactNum;
    public static int noReadBottleNum = 0;
    public static int chatSelectIndex = 0;
    public static String NOTIFICATION_CHANNEL_ID ="com.yimi.rentme_notice";

    public static List<LikeMe> pairList = new ArrayList<>();

    private static LinkedList<Activity> mActivityList = new LinkedList<>();

    public static boolean isChat = false;
    public static boolean isLocation = false;
    public static boolean showBottom = false;
    public static boolean toPublish = false;
    public static boolean toContinue = false;
    public static int cameraType = 0;
    public static String filePath = "";
    public static boolean isMore = false;
    public static long time = 0;

    public static int sex = 0;
    public static int maxAge = 70;
    public static int minAge = 18;

    public static List<DiscoverInfo> discoverInfoList = new ArrayList<>();
    public static Map<String, Integer> constellationMap = new HashMap<>();

    public static List<RecommendInfo> recommendInfoList = new ArrayList<>();

    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
            layout.setPrimaryColorsId(R.color.black_efe, R.color.black_252);//全局设置主题颜色
            return new ClassicsHeader(context);
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> new ClassicsFooter(context).setDrawableSize(20));

        tranTypeMap.put(1, "充值");
        tranTypeMap.put(2, "提现");
        tranTypeMap.put(3, "订单平台服务费");
        tranTypeMap.put(4, "个人信息首页置顶");
        tranTypeMap.put(5, "下级消费分成");
        tranTypeMap.put(6, "技能服务订单付款");
        tranTypeMap.put(7, "开通会员");
        tranTypeMap.put(8, "评论付款");
        tranTypeMap.put(9, "红包付款");
        tranTypeMap.put(10, "私密相册");
        tranTypeMap.put(11, "相册打赏");
        tranTypeMap.put(12, "好友红包");
        tranTypeMap.put(13, "视频围观");
        tranTypeMap.put(14, "付费提问");
        tranTypeMap.put(15, "视频围观分润");
        tranTypeMap.put(16, "诚意订单诚意金");
        tranTypeMap.put(17, "诚意订单定金");
        tranTypeMap.put(18, "诚意订单收款");
        tranTypeMap.put(19, "诚意订单退款");
        tranTypeMap.put(31, "礼物打赏消费");
        tranTypeMap.put(32, "礼物打赏收益");
        tranTypeMap.put(41, "优惠充值");

        // -10 交易超时 -20交易失败 -30用户取消 -40系统关闭(用户不能再操作) -50 已退款 -60原路退款
        // 10 ,"待付款" 20 ,"已付款" 30 ,"正在处理" 40,"待卖家发货" 46,"待买家确定收货" 200,"交易成功"
        tranStatusMap.put(-10, "交易超时");
        tranStatusMap.put(-20, "交易失败");
        tranStatusMap.put(-30, "用户取消");
        tranStatusMap.put(-40, "系统关闭");
        tranStatusMap.put(-50, "已退款");
        tranStatusMap.put(-60, "原路退款");
        tranStatusMap.put(10, "待付款");
        tranStatusMap.put(20, "已付款");
        tranStatusMap.put(30, "正在处理");
        tranStatusMap.put(40, "待卖家发货");
        tranStatusMap.put(46, "待买家确定收货");
        tranStatusMap.put(200, "交易成功");

        constellationMap.put("摩羯座", R.drawable.btn_bg_moxie_radius2);
        constellationMap.put("水瓶座", R.drawable.btn_bg_shuiping_radius2);
        constellationMap.put("双鱼座", R.drawable.btn_bg_shuangyu_radius2);
        constellationMap.put("白羊座", R.drawable.btn_bg_baiyang_radius2);
        constellationMap.put("金牛座", R.drawable.btn_bg_jinniu_radius2);
        constellationMap.put("双子座", R.drawable.btn_bg_shuangzi_radius2);
        constellationMap.put("巨蟹座", R.drawable.btn_bg_juxie_radius2);
        constellationMap.put("狮子座", R.drawable.btn_bg_shizi_radius2);
        constellationMap.put("处女座", R.drawable.btn_bg_chunv_radius2);
        constellationMap.put("天秤座", R.drawable.btn_bg_tianping_radius2);
        constellationMap.put("天蝎座", R.drawable.btn_bg_tianxie_radius2);
        constellationMap.put("射手座", R.drawable.btn_bg_sheshou_radius2);
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
        FitScreen.createDesign(getApplicationContext(), H, W);

        UMConfigure.init(this, "55cac14467e58e8bd7000359", "", UMConfigure.DEVICE_TYPE_PHONE, "");
        PlatformConfig.setWeixin("wxb83427622a6740f6", "97f837c0ae8b11af734041828ba4a737");
        PlatformConfig.setQQZone("1104574025", "ayk3uI7axNJlfGDk");
        UMShareAPI.get(this);

        try {
            PackageInfo packageInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        LogUtil.init();

        // 必须首先执行这部分代码, 如果在":TCMSSevice"进程中，无需进行云旺（OpenIM）和app业务的初始化，以节省内存;
        TCMSService.setEnableForeground(false);
        SysUtil.setApplication(this);
        if (SysUtil.isTCMSServiceProcess(this)) {
            return;
        }
        if (SysUtil.isMainProcess()) {
            YWAPI.init(this, LoginSampleHelper.APP_KEY);
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

    //验证银行卡号
    public static boolean checkBankCard(String cardId) {
        char bit = getBankCardCheckCode(cardId.substring(0, cardId.length() - 1));
        if (bit == 'N') {
            return false;
        }
        return cardId.charAt(cardId.length() - 1) == bit;
    }

    //从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
    private static char getBankCardCheckCode(String nonCheckCodeCardId) {
        if (nonCheckCodeCardId == null || nonCheckCodeCardId.trim().length() == 0
                || !nonCheckCodeCardId.matches("\\d+")) {
            //如果传的不是数据返回N
            return 'N';
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
    }

    /**
     * Activity开启时添加Activity到集合
     *
     * @param activity
     */
    public static void addActivity(Activity activity) {
        mActivityList.addFirst(activity);
    }

    /**
     * Activity退出时清除集合中的Activity.
     *
     * @param activity 被移除的activity
     */
    public static void removeActivity(Activity activity) {
        mActivityList.remove(activity);
    }

    /**
     * 清除 除了自己外其他activity
     *
     * @param oneself 不被移除的activity
     */
    public static void removeOtherActivity(Activity oneself) {
        try {
            for (Activity activity : mActivityList) {
                if (activity != null && !activity.getLocalClassName().equals(oneself.getLocalClassName())) {
                    activity.finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 退出应用时调用
     */
    public static void exit() {
        for (Activity activity : mActivityList) {
            if (activity != null) {
                activity.finish();
            }
        }
    }
}
