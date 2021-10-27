package com.yimi.rentme.vm;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.yimi.rentme.R;
import com.yimi.rentme.adapter.MainAdapter;
import com.yimi.rentme.databinding.AcLoginBinding;
import com.yimi.rentme.iv.LoginVMInterface;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.checkFaceApi;
import com.zb.lib_base.api.checkUserNameApi;
import com.zb.lib_base.api.loginByCaptchaApi;
import com.zb.lib_base.api.loginByPassApi;
import com.zb.lib_base.api.loginByUnionApi;
import com.zb.lib_base.api.loginCaptchaApi;
import com.zb.lib_base.api.modifyMemberInfoApi;
import com.zb.lib_base.api.myInfoApi;
import com.zb.lib_base.api.registerApi;
import com.zb.lib_base.api.registerCaptchaApi;
import com.zb.lib_base.api.verifyCaptchaApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.CheckUser;
import com.zb.lib_base.model.LoginInfo;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.model.RegisterInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DataCleanManager;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.utils.ThreeLogin;
import com.zb.lib_base.utils.uploadImage.PhotoManager;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.BirthdayPW;
import com.zb.lib_base.windows.TextPW;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.databinding.ViewDataBinding;

public class LoginViewModel extends BaseViewModel implements LoginVMInterface, TextWatcher {
    public Map<Integer, String> titleMap = new HashMap<>();
    public Map<Integer, String> contentMap = new HashMap<>();
    public int loginStep = 0;// 0：手机号  1：密码登录  2：验证码
    public MainAdapter mAdapter;
    private List<String> tagList = new ArrayList<>();
    private AcLoginBinding mBinding;
    private long changeTime = 0;
    private TextView[] array = new TextView[4];
    private CountDownTimer timer;
    private int second = 60;
    private boolean canGetCode = true;
    private BaseReceiver cameraReceiver;
    private BaseReceiver bindPhoneReceiver;
    private PhotoManager photoManager;
    private ThreeLogin threeLogin;
    private int passErrorCount = 0;
    private CheckUser mCheckUser;
    private BaseReceiver memberReceiver;
    private boolean needMoreInfo = false;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        MineApp.getApp().getActivityMap().put("LoginActivity", activity);
        titleMap.put(0, "登录/注册 更精彩");
        contentMap.put(0, "输入手机号后，开始探索虾菇！未注册手机，\n将自动进入注册页面。");

        titleMap.put(1, "密码登录");
        contentMap.put(1, "输入密码后请点击“开启虾菇”即刻进入虾菇世界！");

        titleMap.put(3, "选择性别");
        contentMap.put(3, "请告诉我们你是小哥哥还是小姐姐哦～");

        titleMap.put(4, "输入昵称");
        contentMap.put(4, "你想取什么名字，这是你在虾菇上使用的名字哦～");

        titleMap.put(5, "选择你的生日哦");
        contentMap.put(5, "必须满18岁以上才能使用虾菇哦～");

        titleMap.put(6, "放入你的照片");
        contentMap.put(6, "这一张照片会当成你的头像和卡片的首张图片哦～");

        titleMap.put(7, "快完成啦！");
        contentMap.put(7, "最后补充好你的个人资料吧～不填写就显示默认");

        mBinding = (AcLoginBinding) binding;

        mBinding.edPhone.addTextChangedListener(this);
        mBinding.edCode.addTextChangedListener(this);
        mBinding.edPass.addTextChangedListener(this);
        mBinding.edNick.addTextChangedListener(this);
        mBinding.setSexIndex(2);

        SpannableString style = new SpannableString("请阅读《用户注册协议》和《隐私政策》并勾选");

        style.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                ActivityUtils.getMineWeb("注册协议", HttpManager.BASE_URL + "mobile/xiagu_reg_protocol.html");
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setUnderlineText(false);
            }
        }, 3, 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                ActivityUtils.getMineWeb("隐私政策", HttpManager.BASE_URL + "mobile/xiagu_privacy_protocol.html");
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setUnderlineText(false);
            }
        }, 12, 18, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(new ForegroundColorSpan(Color.parseColor("#0d88c1")), 3, 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(new ForegroundColorSpan(Color.parseColor("#0d88c1")), 12, 18, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mBinding.tvClick.setText(style);
        mBinding.tvClick.setMovementMethod(LinkMovementMethod.getInstance());
        mBinding.setClickSelect(TextUtils.equals("1", PreferenceUtil.readStringValue(activity, "clickSelect")));
        mBinding.setImageUrl("");
        threeLogin = new ThreeLogin(activity, this::loginByUnion);

        array[0] = mBinding.tvCode1;
        array[1] = mBinding.tvCode2;
        array[2] = mBinding.tvCode3;
        array[3] = mBinding.tvCode4;
        timer = new CountDownTimer(second * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                canGetCode = false;
                mBinding.setCodeRemark(Html.fromHtml(activity.getResources().getString(R.string.code_second, millisUntilFinished / 1000)));
            }

            public void onFinish() {
                mBinding.setCodeRemark(Html.fromHtml(activity.getResources().getString(R.string.code_second_finish)));
                timer.cancel();
                canGetCode = true;
            }
        };

        cameraReceiver = new BaseReceiver(activity, "lobster_camera") {
            @Override
            public void onReceive(Context context, Intent intent) {
                setSingleLogo(intent.getStringExtra("filePath"));
            }
        };

        bindPhoneReceiver = new BaseReceiver(activity, "lobster_bindPhone") {
            @Override
            public void onReceive(Context context, Intent intent) {
                MineApp.registerInfo.setImage(MineApp.registerInfo.getUnionImage());
                ActivityUtils.getBindingPhoneActivity(activity, true, false);
            }
        };

        memberReceiver = new BaseReceiver(activity, "lobster_member") {
            @Override
            public void onReceive(Context context, Intent intent) {
                int type = intent.getIntExtra("type", 0);
                String content = intent.getStringExtra("content");
                if (type == 1) {
                    MineApp.registerInfo.setJob(content);
                    mBinding.setRegisterInfo(MineApp.registerInfo);
                } else if (type == 3) {
                    MineApp.registerInfo.setPersonalitySign(content);
                    mBinding.setRegisterInfo(MineApp.registerInfo);
                } else if (type == 4) {
                    MineApp.registerInfo.setServiceTags(content);
                    tagList.clear();
                    mAdapter.notifyDataSetChanged();
                    if (!content.isEmpty()) {
                        tagList.addAll(Arrays.asList(content.substring(1, content.length() - 1).split("#")));
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        };

        MineApp.cityName = PreferenceUtil.readStringValue(activity, "cityName");
        if (checkPermissionGranted(activity, Manifest.permission.READ_PHONE_STATE))
            setPermissions(0);
        else if (PreferenceUtil.readIntValue(activity, "phonePermission") == 0) {
            PreferenceUtil.saveIntValue(activity, "phonePermission", 1);
            MineApp.getApp().getFixedThreadPool().execute(() -> {
                SystemClock.sleep(300);
                activity.runOnUiThread(() -> new TextPW(activity, mBinding.getRoot(), "权限说明",
                        "当您注册并使用虾菇时，我们希望知道您设备的一些信息（包括设备序列号、设备MAC地址、唯一设备识别码（IMEI/android ID/OPENUDID等）），因此我们将会申请电话权限：" +
                                "\n 1、申请电话权限--获取设备信息（包括设备序列号、设备MAC地址、唯一设备识别码（IMEI/android ID/OPENUDID等））。" +
                                "\n 2、我们的保护：" +
                                "\n\t ①、请注意，单独的设备信息是无法识别特定自然人身份的信息。" +
                                "\n\t ②、如果我们将这类信息与其他信息结合用于识别特定自然人身份，或者将其与个人信息结合使用，则在结合使用期间，这类信息将被视为个人信息，除取得您授权或法律法规另有规定外，我们会将该类个人信息做匿名化、去标识化处理。" +
                                "\n 3、我们的用途：" +
                                "\n\t ①、保障用户账号安全、平台安全、运营安全，便于辨别不同的设备，方便错误原因精准定位。" +
                                "\n\t ②、您同意开启通知功能，即代表您同意将设备信息共享给每日互动股份有限公司，以便提供个推推送服务。" +
                                "\n\t ③、您使用支付功能，即代表您同意将设备信息共享给支付宝和微信，以便提供app支付功能。" +
                                "\n\t ④、为了保障及时了解软件使用情况及修复bug，我们将与友盟+共享设备信息，以便提供统计功能。" +
                                "\n\t ⑤、您同意开启定位功能，即代表您同意将设备信息共享给高德地图，以便提供地图功能和精准定位功能。" +
                                "\n 4、若您点击“同意”按钮，我们方可正式申请上述权限，以便获取设备信息，" +
                                "\n 5、若您点击“拒绝”按钮，我们将不再主动弹出该提示，也不会获取设备信息，不影响使用其他的虾姑功能/服务，" +
                                "\n 6、您也可以通过“手机设置--应用--虾菇--权限”或app内“我的--设置--权限管理--权限”，手动开启或关闭手机权限。",
                        "同意", false, true, () -> getPermissions(0)));
            });
        }


        photoManager = new PhotoManager(activity, () -> {
            String imageUrl = photoManager.jointWebUrl("#");
            mBinding.setCanNext(true);
            MineApp.registerInfo.setMoreImages(imageUrl);
            MineApp.registerInfo.setImage(imageUrl);
            mBinding.setImageUrl(imageUrl);
            photoManager.deleteAllFile();
            needMoreInfo = true;
        });
        step(0);
        setAdapter();
    }

    @Override
    public void setAdapter() {
        tagList.add("旅行");
        tagList.add("摄影");
        tagList.add("乐观主义");
        tagList.add("老实孩子");
        tagList.add("简单");
        tagList.add("音乐会");
        MineApp.registerInfo.setServiceTags("#旅行#摄影#乐观主义#老实孩子#简单#音乐会#");
        mAdapter = new MainAdapter<>(activity, R.layout.item_select_tag, tagList, this);
    }

    public void onDestroy() {
        try {
            cameraReceiver.unregisterReceiver();
            bindPhoneReceiver.unregisterReceiver();
            memberReceiver.unregisterReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新单图
     */
    public void setSingleLogo(String filePath) {
        photoManager.addFileUpload(0, new File(filePath));
    }

    public void setInfo(String bindPhone, String captcha) {
        MineApp.registerInfo.setBindPhone(bindPhone);
        MineApp.registerInfo.setCaptcha(captcha);
        mBinding.setIsThree(true);
        step(3);
    }


    @Override
    public void right(View view) {
        super.right(view);
        hintKeyBoard();
        if (canGetCode)
            loginCaptcha();
        else {
            step(2);
        }
    }

    @Override
    public void cleanPhone(View view) {
        MineApp.registerInfo.setPhone("");
        mBinding.setRegisterInfo(MineApp.registerInfo);
    }

    @Override
    public void selectSex(int sex) {
        mBinding.setCanNext(true);
        MineApp.registerInfo.setSex(sex);
        mBinding.setSexIndex(sex);
    }

    @Override
    public void selectBirthday(View view) {
        new BirthdayPW(mBinding.getRoot(), MineApp.registerInfo.getBirthday(), birthday -> {
            MineApp.registerInfo.setBirthday(birthday);
            mBinding.setRegisterInfo(MineApp.registerInfo);
            mBinding.setCanNext(true);
        });
    }

    @Override
    public void resetCode(View view) {
        if (canGetCode) {
            if (mCheckUser.getIsRegister() == 0) {
                registerCaptcha();
            } else {
                loginCaptcha();
            }
        }
    }

    @Override
    public void upload(View view) {
        if (checkPermissionGranted(activity, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            setPermissions(1);
        } else {
            if (PreferenceUtil.readIntValue(activity, "photoPermission") == 0) {
                PreferenceUtil.saveIntValue(activity, "photoPermission", 1);
                new TextPW(activity, mBinding.getRoot(), "权限说明",
                        "提交本人真实头像时需要使用上传图片功能，我们将会申请相机、存储权限：" +
                                "\n 1、申请相机权限--上传图片时获取拍摄照片功能，" +
                                "\n 2、申请存储权限--上传图片时获取保存和读取图片功能，" +
                                "\n 4、若您点击“同意”按钮，我们方可正式申请上述权限，以便拍摄照片及选取照片，完善个人信息，" +
                                "\n 5、若您点击“拒绝”按钮，我们将不再主动弹出该提示，您也无法使用上传图片功能，不影响使用其他的虾姑功能/服务，" +
                                "\n 6、您也可以通过“手机设置--应用--虾菇--权限”或app内“我的--设置--权限管理--权限”，手动开启或关闭相机、存储权限。",
                        "同意", false, true, () -> getPermissions(1));
            } else {
                if (!checkPermissionGranted(activity, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    SCToastUtil.showToast(activity, "你未开启存储权限，请前往我的--设置--权限管理--权限进行设置", true);
                } else if (!checkPermissionGranted(activity, Manifest.permission.CAMERA)) {
                    SCToastUtil.showToast(activity, "你未开启相机权限，请前往我的--设置--权限管理--权限进行设置", true);
                }
            }
        }
    }

    @Override
    public void toQQ(View view) {
        if (!mBinding.getClickSelect()) {
            SCToastUtil.showToast(activity, "请仔细阅读底部协议，并勾选", false);
            return;
        }
        threeLogin.selectUnionType(2);
    }

    @Override
    public void toWX(View view) {
        if (!mBinding.getClickSelect()) {
            SCToastUtil.showToast(activity, "请仔细阅读底部协议，并勾选", false);
            return;
        }
        threeLogin.selectUnionType(1);
    }

    @Override
    public void next(View view) {
        hintKeyBoard();
        if (mBinding.getCanNext())
            switch (mBinding.getLoginStep()) {
                case 0: // 验证手机号
                    PreferenceUtil.saveStringValue(activity, "userName", MineApp.registerInfo.getPhone());
                    if (!mBinding.getClickSelect()) {
                        SCToastUtil.showToast(activity, "请仔细阅读底部协议，并勾选", false);
                        return;
                    }
                    checkUserName();
                    break;
                case 1: // 账号登录
                    loginByPass();
                    break;
                case 2: // 注册验证码
                    verifyCaptcha();
                    break;
                case 3: // 选择性别
                    step(4);
                    break;
                case 4: // 名字
                    step(5);
                    selectBirthday(view);
                    break;
                case 5:
                    step(6);
                    break;
                case 6:
                    if (mBinding.getImageUrl().isEmpty()) {
                        step(7);
                    } else {
                        checkFace(mBinding.getImageUrl());
                    }
                    break;
                case 7:
                    if (MineApp.registerInfo.getOpenId().isEmpty())
                        register();
                    else {
                        MineApp.registerInfo.setUnionImage(photoManager.jointWebUrl("#"));
                        loginByUnion();
                    }
                    break;
            }
    }

    private void checkFace(String faceImage) {
        checkFaceApi api = new checkFaceApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                step(7);
            }
        }, activity).setFaceImage(faceImage);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void changeUrl(View view) {
        if ((System.currentTimeMillis() - changeTime) > 2000) {
            changeTime = System.currentTimeMillis();
        } else {
            changeTime = 0;
            HttpManager.BASE_URL = TextUtils.equals(HttpManager.BASE_URL, "http://192.168.1.88:8090/") ? "https://xgapi.zuwo.la/" : "http://192.168.1.88:8090/";
            HttpManager.INSTANCE = null;
            SCToastUtil.showToast(activity, HttpManager.BASE_URL, false);
        }
    }

    @Override
    public void selectTag(View view) {
        String selectList = "";
        for (String item : tagList) {
            selectList += item + "#";
        }
        if (!selectList.isEmpty())
            selectList = "#" + selectList;

        ActivityUtils.getMineSelectTag(selectList);
    }

    @Override
    public void close(View view) {
        hintKeyBoard();
    }

    @Override
    public void selectJog(View view) {
        ActivityUtils.getMineSelectJob(MineApp.registerInfo.getJob());
    }

    @Override
    public void editSign(View view) {
        ActivityUtils.getMineEditContent(3, 10, "编辑个性签名", MineApp.registerInfo.getPersonalitySign(), "编辑个性签名...");
    }

    @Override
    public void clickSelect(View view) {
        mBinding.setClickSelect(!mBinding.getClickSelect());
        PreferenceUtil.saveStringValue(activity, "clickSelect", mBinding.getClickSelect() ? "1" : "0");
    }

    @Override
    public void registerCaptcha() {
        // 注册验证码
        registerCaptchaApi api = new registerCaptchaApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                step(2);
                mBinding.setCodeRemark(Html.fromHtml(activity.getResources().getString(R.string.code_second, second)));
                timer.start();
            }
        }, activity).setUserName(MineApp.registerInfo.getPhone());
        api.setPosition(1);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void loginCaptcha() {
        loginCaptchaApi api = new loginCaptchaApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                step(2);
                mBinding.setCodeRemark(Html.fromHtml(activity.getResources().getString(R.string.code_second, second)));
                timer.start();
            }
        }, activity).setUserName(MineApp.registerInfo.getPhone());
        api.setPosition(1);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void register() {
        registerApi api = new registerApi(new HttpOnNextListener<LoginInfo>() {
            @Override
            public void onNext(LoginInfo o) {
                PreferenceUtil.saveLongValue(activity, "userId", o.getId());
                PreferenceUtil.saveStringValue(activity, "sessionId", o.getSessionId());
                PreferenceUtil.saveStringValue(activity, "userName", o.getUserName());
                PreferenceUtil.saveStringValue(activity, "loginPass", "");
                BaseActivity.update();
                DataCleanManager.deleteFile(new File(activity.getCacheDir(), "images"));
                PreferenceUtil.saveIntValue(activity, "myIsThreeLogin", 0);
                SCToastUtil.showToast(activity, "注册成功", true);
                if (needMoreInfo) {
                    modifyMemberInfo();
                } else
                    myInfo();
            }
        }, activity)
                .setUserName(MineApp.registerInfo.getPhone())
                .setCaptcha(mBinding.edCode.getText().toString())
                .setNick(MineApp.registerInfo.getName())
                .setBirthday(MineApp.registerInfo.getBirthday())
                .setMoreImages(MineApp.registerInfo.getMoreImages())
                .setSex(MineApp.registerInfo.getSex())
                .setProvinceId(0)
                .setCityId(0)
                .setDistrictId(0);
        api.setPosition(1);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void loginByUnion() {
        loginByUnionApi api = new loginByUnionApi(new HttpOnNextListener<LoginInfo>() {
            @Override
            public void onNext(LoginInfo o) {
                PreferenceUtil.saveLongValue(activity, "userId", o.getId());
                PreferenceUtil.saveStringValue(activity, "sessionId", o.getSessionId());
                PreferenceUtil.saveIntValue(activity, "myIsThreeLogin", 1);
                DataCleanManager.deleteFile(new File(activity.getCacheDir(), "images"));
                BaseActivity.update();
                PreferenceUtil.saveStringValue(activity, "userName", "");
                PreferenceUtil.saveStringValue(activity, "loginPass", "");
                if (o.getPhoneNum().isEmpty()) {
                    ActivityUtils.getBindingPhoneActivity(activity, false, false);
                } else {
                    SCToastUtil.showToast(activity, "登录成功", true);
                    if (needMoreInfo) {
                        modifyMemberInfo();
                    } else
                        myInfo();
                }
            }
        }, activity)
                .setOpenId(MineApp.registerInfo.getOpenId())
                .setUnionId(MineApp.registerInfo.getUnionId())
                .setUnionImage(MineApp.registerInfo.getUnionImage())
                .setUnionNick(MineApp.registerInfo.getName())
                .setUnionSex(MineApp.registerInfo.getSex())
                .setUnionType(MineApp.registerInfo.getUnionType())
                .setUserName(MineApp.registerInfo.getBindPhone())
                .setCaptcha(MineApp.registerInfo.getCaptcha())
                .setNick(MineApp.registerInfo.getName())
                .setBirthday(MineApp.registerInfo.getBirthday())
                .setMoreImages(MineApp.registerInfo.getMoreImages())
                .setSex(MineApp.registerInfo.getSex())
                .setProvinceId(0)
                .setCityId(0)
                .setDistrictId(0);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void modifyMemberInfo() {
        needMoreInfo = false;
        modifyMemberInfoApi api = new modifyMemberInfoApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                myInfo();
            }
        }, activity)
                .setBirthday(MineApp.registerInfo.getBirthday())
                .setImage(MineApp.registerInfo.getImage())
                .setMoreImages(MineApp.registerInfo.getMoreImages())
                .setNick(MineApp.registerInfo.getName())
                .setJob(MineApp.registerInfo.getJob().isEmpty() ? "设计师" : MineApp.registerInfo.getJob())
                .setPersonalitySign(MineApp.registerInfo.getPersonalitySign().isEmpty() ? "有趣之人终相遇" : MineApp.registerInfo.getPersonalitySign())
                .setSex(MineApp.registerInfo.getSex())
                .setServiceTags(MineApp.registerInfo.getServiceTags())
                .setProvinceId(0)
                .setCityId(0)
                .setDistrictId(0);
        api.setShowProgress(false);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void loginByCaptcha() {
        loginByCaptchaApi api = new loginByCaptchaApi(new HttpOnNextListener<LoginInfo>() {
            @Override
            public void onNext(LoginInfo o) {
                PreferenceUtil.saveLongValue(activity, "userId", o.getId());
                PreferenceUtil.saveStringValue(activity, "sessionId", o.getSessionId());
                PreferenceUtil.saveStringValue(activity, "userName", o.getUserName());
                PreferenceUtil.saveStringValue(activity, "loginPass", "");
                BaseActivity.update();
                PreferenceUtil.saveIntValue(activity, "myIsThreeLogin", 0);
                timer.cancel();
                SCToastUtil.showToast(activity, "登录成功", true);
                myInfo();
            }
        }, activity)
                .setUserName(MineApp.registerInfo.getPhone())
                .setCaptcha(mBinding.edCode.getText().toString());
        api.setPosition(1);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void loginByPass() {
        loginByPassApi api = new loginByPassApi(new HttpOnNextListener<LoginInfo>() {
            @Override
            public void onNext(LoginInfo o) {
                PreferenceUtil.saveLongValue(activity, "userId", o.getId());
                PreferenceUtil.saveStringValue(activity, "sessionId", o.getSessionId());
                PreferenceUtil.saveStringValue(activity, "userName", o.getUserName());
                PreferenceUtil.saveStringValue(activity, "loginPass", MineApp.registerInfo.getPass());
                BaseActivity.update();
                PreferenceUtil.saveIntValue(activity, "myIsThreeLogin", 0);
                SCToastUtil.showToast(activity, "登录成功", true);
                myInfo();
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.ERROR) {
                    if (TextUtils.equals(e.getMessage(), "用户名或密码错误")) {
                        passErrorCount++;
                        if (passErrorCount >= 3) {
                            new TextPW(mBinding.getRoot(), "忘记密码", "如果忘记密码，请使用验证码登录，登录成功后前往我的--设置--修改密码，重设密码",
                                    "验证码登录", () -> {
                                if (canGetCode)
                                    loginCaptcha();
                                else {
                                    step(2);
                                }
                            });
                        }
                    }

                }
            }
        }, activity)
                .setUserName(MineApp.registerInfo.getPhone())
                .setPassWord(MineApp.registerInfo.getPass());
        api.setPosition(1);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void checkUserName() {
        checkUserNameApi api = new checkUserNameApi(new HttpOnNextListener<CheckUser>() {
            @Override
            public void onNext(CheckUser o) {
                // 1.已注册 0.未注册
                mCheckUser = o;
                if (mCheckUser.getIsRegister() == 0) {
                    registerCaptcha();
                } else {
                    step(1);
                }
            }
        }, activity).setUserName(MineApp.registerInfo.getPhone());
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void myInfo() {
        myInfoApi api = new myInfoApi(new HttpOnNextListener<MineInfo>() {
            @Override
            public void onNext(MineInfo o) {
                MineApp.mineInfo = o;
                ActivityUtils.getMainActivity();
                MineApp.registerInfo = new RegisterInfo();
                timer.cancel();
                passErrorCount = 0;
                MineApp.getApp().removeActivity(MineApp.getApp().getActivityMap().get("LoginActivity"));
                activity.finish();
            }
        }, activity);
        api.setPosition(1);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void verifyCaptcha() {
        verifyCaptchaApi api = new verifyCaptchaApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                step(3);
            }
        }, activity).setUserName(MineApp.registerInfo.getPhone()).setCaptcha(mBinding.edCode.getText().toString());
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String content = editable.toString();
        if (mBinding.getLoginStep() == 0) {
            mBinding.setCanNext(content.length() == 11);
        } else if (mBinding.getLoginStep() == 1) {
            mBinding.setCanNext(content.length() >= 6);
        } else if (mBinding.getLoginStep() == 2) {
            for (int i = 0; i < 4; i++) {
                if (i < content.length()) {
                    array[i].setText(String.valueOf(content.charAt(i)));
                } else {
                    array[i].setText("");
                }
            }
            mBinding.setCanNext(content.length() == 4);
            if (content.length() == 4) {
                hintKeyBoard();
                if (mCheckUser.getIsRegister() == 0) {
                    verifyCaptcha();
                } else {
                    loginByCaptcha();
                }
            }
        } else if (mBinding.getLoginStep() == 4) {
            mBinding.setCanNext(!content.isEmpty());
        }
    }

    private long exitTime = 0;

    @Override
    public void back(View view) {
        super.back(view);
        hintKeyBoard();
        switch (mBinding.getLoginStep()) {
            case 0:
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    SCToastUtil.showToast(activity, "再按一次退出程序", false);
                    exitTime = System.currentTimeMillis();
                } else {
                    timer.cancel();
                    MineApp.getApp().exit();
                    System.exit(0);
                }
                break;
            case 1: // 账号登录返回
                step(0);
                break;
            case 2: // 验证码登录返回
                if (mCheckUser.getIsRegister() == 0)
                    step(0);
                else {
                    step(1);
                }
                break;
            case 3: // 性别页返回
                if (mBinding.getIsThree()) {
                    step(0);
                } else {
                    step(2);
                }
                break;
            case 4: // 名字页返回
                step(3);
                break;
            case 5: //
                step(4);
                break;
            case 6:
                step(5);
                break;
            case 7:
                step(6);
                break;
        }
    }

    private void step(int step) {
        mBinding.setLoginStep(step);
        mBinding.setRight("");
        mBinding.setVariable(R.id.tv_right, View.GONE);
        switch (step) {
            case 0: // 手机号
                MineApp.registerInfo = new RegisterInfo();
                MineApp.registerInfo.setPhone(PreferenceUtil.readStringValue(activity, "userName"));
                MineApp.registerInfo.setPass(PreferenceUtil.readStringValue(activity, "loginPass"));
                mBinding.setRegisterInfo(MineApp.registerInfo);
                mBinding.setIsThree(false);
                mBinding.setBtnName("下一步");
                MineApp.getApp().getFixedThreadPool().execute(() -> {
                    SystemClock.sleep(200);
                    activity.runOnUiThread(() -> {
                        mBinding.edPhone.setSelection(mBinding.edPhone.getText().length());
                        mBinding.setCanNext(MineApp.registerInfo.getPhone().length() == 11);
                    });
                });
                break;
            case 1: // 密码登录
                mBinding.setCanNext(MineApp.registerInfo.getPass().length() >= 6);
                mBinding.setRight("验证码登录");
                mBinding.setBtnName("开启虾菇");
                mBinding.setVariable(R.id.tv_right, View.VISIBLE);
                MineApp.getApp().getFixedThreadPool().execute(() -> {
                    SystemClock.sleep(200);
                    activity.runOnUiThread(() -> {
                        mBinding.edPass.setSelection(mBinding.edPass.getText().length());
                        showImplicit(mBinding.edPass);
                    });
                });
                break;
            case 2: // 验证码
                mBinding.setCanNext(false);
                mBinding.edCode.setText("");
                titleMap.put(2, mCheckUser.getIsRegister() == 0 ? "注册虾菇" : "验证码登录");
                contentMap.put(2, "");
                mBinding.setBtnName(mCheckUser.getIsRegister() == 0 ? "下一步" : "开启虾菇");
                showImplicit(mBinding.edCode);
                break;
            case 3: // 性别
                mBinding.setCanNext(mBinding.getSexIndex() != 2);
                mBinding.setBtnName("下一步");
                break;
            case 4: // 名字
                mBinding.setCanNext(!MineApp.registerInfo.getName().isEmpty());
                mBinding.setBtnName("下一步");
                MineApp.getApp().getFixedThreadPool().execute(() -> {
                    SystemClock.sleep(200);
                    activity.runOnUiThread(() -> {
                        mBinding.edNick.setSelection(mBinding.edNick.getText().length());
                        showImplicit(mBinding.edNick);
                    });
                });
                break;
            case 5: // 生日
                mBinding.setCanNext(!MineApp.registerInfo.getBirthday().isEmpty());
                mBinding.setBtnName("下一步");
                break;
            case 6:
                mBinding.setCanNext(true);
                mBinding.setBtnName("下一步");
                break;
            case 7:
                mBinding.setCanNext(true);
                mBinding.setBtnName("开启虾菇吧");
                break;
        }
    }

    /**
     * 权限
     */
    private void getPermissions(int type) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (type == 0)
                performCodeWithPermission("虾菇需要访问电话权限", new BaseActivity.PermissionCallback() {
                    @Override
                    public void hasPermission() {
                        setPermissions(type);
                    }

                    @Override
                    public void noPermission() {
                        PreferenceUtil.saveIntValue(activity, "phonePermission", 1);
                    }
                }, Manifest.permission.READ_PHONE_STATE);
            else
                performCodeWithPermission("虾菇需要访问相机权限及存储空间权限", new BaseActivity.PermissionCallback() {
                    @Override
                    public void hasPermission() {
                        setPermissions(type);
                    }

                    @Override
                    public void noPermission() {
                        PreferenceUtil.saveIntValue(activity, "photoPermission", 1);
                    }
                }, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
        } else {
            setPermissions(type);
        }
    }

    private void setPermissions(int type) {
        if (type == 0) {
            initPhone();
        } else {
            MineApp.toPublish = false;
            ActivityUtils.getCameraMain(activity, false, true, false);
        }
    }

    @SuppressLint("HardwareIds")
    private void initPhone() {
        TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        String ANDROID_ID = Settings.System.getString(activity.getContentResolver(), Settings.System.ANDROID_ID);
        PreferenceUtil.saveStringValue(activity, "deviceCode", ANDROID_ID);
        Log.e("deviceCode", ANDROID_ID);
    }
}
