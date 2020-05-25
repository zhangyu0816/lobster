package com.zb.lib_base.app;

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
import com.zb.lib_base.R;
import com.zb.lib_base.adaptive.FitScreen;
import com.zb.lib_base.imcore.LoginSampleHelper;
import com.zb.lib_base.log.LogUtil;
import com.zb.lib_base.model.BaseEntity;
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
    public static Map<Integer, String> tranTypeMap = new HashMap<>();
    public static Map<String, CutImageView> cutImageViewMap = new HashMap<>();
    public static Typeface type;
    public static Typeface simplifiedType;
    public static List<VipInfo> vipInfoList = new ArrayList<>();
    public static String logo = "http://img01.zuwo.la/img/A/YMXXXX919714-206348_YM0000.jpg";
    public static String cityName = "";
    public static String versionName;
    public static String WX_PAY_APPID = "wxbdd7128e0a0a08f8";
    public static List<BaseEntity> apiList = new ArrayList<>(); // 断线重连

    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
            layout.setPrimaryColorsId(R.color.black_efe, R.color.black_252);//全局设置主题颜色
            return new ClassicsHeader(context);
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> new ClassicsFooter(context).setDrawableSize(20));

        tranTypeMap.put(1,"充值");
        tranTypeMap.put(2,"提现");
        tranTypeMap.put(3,"订单平台服务费");
        tranTypeMap.put(4,"个人信息首页置顶");
        tranTypeMap.put(5,"下级消费分成");
        tranTypeMap.put(6,"技能服务订单付款");
        tranTypeMap.put(7,"开通会员");
        tranTypeMap.put(8,"评论付款");
        tranTypeMap.put(9,"红包付款");
        tranTypeMap.put(10,"私密相册");
        tranTypeMap.put(11,"相册打赏");
        tranTypeMap.put(12,"好友红包");
        tranTypeMap.put(13,"视频围观");
        tranTypeMap.put(14,"付费提问");
        tranTypeMap.put(15,"视频围观分润");
        tranTypeMap.put(16,"诚意订单诚意金");
        tranTypeMap.put(17,"诚意订单定金");
        tranTypeMap.put(18,"诚意订单收款");
        tranTypeMap.put(19,"诚意订单退款");
        tranTypeMap.put(31,"礼物打赏");
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
}
