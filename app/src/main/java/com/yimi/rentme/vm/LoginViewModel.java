package com.yimi.rentme.vm;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.yimi.rentme.R;
import com.yimi.rentme.adapter.MainAdapter;
import com.yimi.rentme.databinding.AcLoginBinding;
import com.yimi.rentme.iv.LoginVMInterface;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
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
import com.zb.lib_base.db.AreaDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.imcore.ImUtils;
import com.zb.lib_base.model.CheckUser;
import com.zb.lib_base.model.LoginInfo;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.model.RegisterInfo;
import com.zb.lib_base.utils.AMapLocation;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DataCleanManager;
import com.zb.lib_base.utils.Mac;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.utils.ThreeLogin;
import com.zb.lib_base.utils.uploadImage.PhotoManager;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.BirthdayPW;
import com.zb.lib_base.windows.RulePW;
import com.zb.lib_base.windows.TextPW;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private AMapLocation aMapLocation;
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
        mBinding.setSexIndex(2);

        threeLogin = new ThreeLogin(activity, this::loginByUnion);
        ImUtils.getInstance().setCallBackForLogin(() -> {
            ImUtils.getInstance().setCallBackForLogin(null);
            ActivityUtils.getMainActivity();
            MineApp.registerInfo = new RegisterInfo();
            timer.cancel();
            passErrorCount = 0;
            MineApp.getApp().removeActivity(MineApp.getApp().getActivityMap().get("LoginActivity"));
            activity.finish();
        });

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
                ActivityUtils.getBindingPhoneActivity(activity, true);
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

        aMapLocation = new AMapLocation(activity);
        MineApp.cityName = PreferenceUtil.readStringValue(activity, "cityName");
        getPermissions(0);

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

    private long exitTime = 0;

    @Override
    public void back(View view) {
        super.back(view);
        hintKeyBoard();
        switch (mBinding.getLoginStep()) {
            case 0:
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    SCToastUtil.showToast(activity, "再按一次退出程序", true);
                    exitTime = System.currentTimeMillis();
                } else {
                    timer.cancel();
                    ImUtils.getInstance().loginOutIM();
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
        getPermissions(1);
    }

    @Override
    public void toQQ(View view) {
        threeLogin.selectUnionType(2);
    }

    @Override
    public void toWX(View view) {
        threeLogin.selectUnionType(1);
    }

    @Override
    public void next(View view) {
        hintKeyBoard();
        if (mBinding.getCanNext())
            switch (mBinding.getLoginStep()) {
                case 0: // 验证手机号
                    PreferenceUtil.saveStringValue(activity, "userName", MineApp.registerInfo.getPhone());
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
                    step(7);
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

    @Override
    public void changeUrl(View view) {
        if ((System.currentTimeMillis() - changeTime) > 2000) {
            changeTime = System.currentTimeMillis();
        } else {
            changeTime = 0;
            HttpManager.BASE_URL = TextUtils.equals(HttpManager.BASE_URL, "http://192.168.1.88:8090/") ? "https://xgapi.zuwo.la/" : "http://192.168.1.88:8090/";
            HttpManager.INSTANCE = null;
            SCToastUtil.showToast(activity, HttpManager.BASE_URL, true);
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
                .setProvinceId(AreaDb.getInstance().getProvinceId(PreferenceUtil.readStringValue(activity, "provinceName")))
                .setCityId(AreaDb.getInstance().getCityId(MineApp.cityName))
                .setDistrictId(AreaDb.getInstance().getDistrictId(PreferenceUtil.readStringValue(activity, "districtName")));
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
                    ActivityUtils.getBindingPhoneActivity(activity, false);
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
                .setProvinceId(AreaDb.getInstance().getProvinceId(PreferenceUtil.readStringValue(activity, "provinceName")))
                .setCityId(AreaDb.getInstance().getCityId(MineApp.cityName))
                .setDistrictId(AreaDb.getInstance().getDistrictId(PreferenceUtil.readStringValue(activity, "districtName")));
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
                .setProvinceId(AreaDb.getInstance().getProvinceId(PreferenceUtil.readStringValue(activity, "provinceName")))
                .setCityId(AreaDb.getInstance().getCityId(MineApp.cityName))
                .setDistrictId(AreaDb.getInstance().getDistrictId(PreferenceUtil.readStringValue(activity, "districtName")));
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
                    showRule();// 显示协议
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
                if (TextUtils.equals("http://192.168.1.88:8090/", HttpManager.BASE_URL)) {
                    ActivityUtils.getMainActivity();
                    MineApp.registerInfo = new RegisterInfo();
                    timer.cancel();
                    passErrorCount = 0;
                    MineApp.getApp().removeActivity(MineApp.getApp().getActivityMap().get("LoginActivity"));
                    activity.finish();
                } else {
                    ImUtils.getInstance().setChat(false, activity);
                }
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

    private void showRule() {
        if (PreferenceUtil.readIntValue(activity, "ruleType1") == 0) {
            MineApp.getApp().getFixedThreadPool().execute(() -> {
                SystemClock.sleep(200);
                activity.runOnUiThread(() -> new RulePW(activity, mBinding.getRoot(), 1, new RulePW.CallBack() {
                    @Override
                    public void sureBack() {
                        PreferenceUtil.saveIntValue(activity, "ruleType1", 1);
                        registerCaptcha();
                    }

                    @Override
                    public void cancelBack() {

                    }
                }));
            });
        } else {
            registerCaptcha();
        }
    }

    private void step(int step) {
        mBinding.setLoginStep(step);
        mBinding.setRight("");
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
                        showImplicit(mBinding.edPhone);
                        mBinding.setCanNext(MineApp.registerInfo.getPhone().length() == 11);
                    });
                });
                break;
            case 1: // 密码登录
                mBinding.setCanNext(MineApp.registerInfo.getPass().length() >= 6);
                mBinding.setRight("验证码登录");
                mBinding.setBtnName("开启虾菇");
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
                mBinding.setCanNext(mBinding.getImageUrl() != null);
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
            performCodeWithPermission("虾菇需要访问定位权限及手机权限", new BaseActivity.PermissionCallback() {
                        @Override
                        public void hasPermission() {
                            setPermissions(type);
                        }

                        @Override
                        public void noPermission() {
                        }
                    }, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE);
        } else {
            setPermissions(type);
        }
    }

    private void setPermissions(int type) {
        if (type == 0) {
            initPhone();
            if (MineApp.cityName.isEmpty()) {
                aMapLocation.start(activity, null);
            }
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
        String imei = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            imei = tm.getImei();
        } else {
            imei = tm.getDeviceId();
        }

        if (imei == null) {
            imei = ANDROID_ID;
        }
        PreferenceUtil.saveStringValue(activity, "deviceCode", imei);
        String iccid = tm.getSimSerialNumber();
        // CDMA手机返回MEID
        String meid = imei;
        // GSM手机返回IMEI
        String mac = Mac.getMac(activity);

        JSONObject object = new JSONObject();
        try {
            object.put("iccid", iccid);
            object.put("meid", meid);
            object.put("imei", imei);
            object.put("mac", mac);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PreferenceUtil.saveStringValue(activity, "deviceHardwareInfo", object.toString());
    }
}
