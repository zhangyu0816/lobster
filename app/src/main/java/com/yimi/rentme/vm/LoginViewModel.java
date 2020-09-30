package com.yimi.rentme.vm;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.Editable;
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
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.api.loginByCaptchaApi;
import com.zb.lib_base.api.loginByPassApi;
import com.zb.lib_base.api.loginByUnionApi;
import com.zb.lib_base.api.loginCaptchaApi;
import com.zb.lib_base.api.myInfoApi;
import com.zb.lib_base.api.registerApi;
import com.zb.lib_base.api.registerCaptchaApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.AreaDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.imcore.ImUtils;
import com.zb.lib_base.model.LoginInfo;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.model.RegisterInfo;
import com.zb.lib_base.utils.AMapLocation;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DataCleanManager;
import com.zb.lib_base.utils.MNImage;
import com.zb.lib_base.utils.Mac;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.utils.SimpleItemTouchHelperCallback;
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
import androidx.recyclerview.widget.ItemTouchHelper;

public class LoginViewModel extends BaseViewModel implements LoginVMInterface, TextWatcher {
    public Map<Integer, String> titleMap = new HashMap<>();
    public int loginStep = 0;// 0：主页  1：昵称  2：生日  3：手机号  4：验证码  5：个人头像  6：多图  7：密码登录
    public MainAdapter adapter;
    public List<String> moreImageList = new ArrayList<>();
    private AcLoginBinding mBinding;
    private long changeTime = 0;
    private TextView[] array = new TextView[4];
    private CountDownTimer timer;
    private int second = 120;
    private boolean canGetCode = true;
    private int _position;
    private BaseReceiver cameraReceiver;
    private BaseReceiver bindPhoneReceiver;
    private PhotoManager photoManager;
    private AMapLocation aMapLocation;
    private ThreeLogin threeLogin;
    private int passErrorCount = 0;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        for (int i = 0; i < 6; i++) {
            moreImageList.add("");
        }
        titleMap.put(0, "嗨！您的性别为...");
        titleMap.put(1, "下一步，您叫什么名字？");
        titleMap.put(3, "您的手机号码是？");
        titleMap.put(4, "请输入验证码");
        titleMap.put(5, "请选择您的照片哦");
        titleMap.put(6, "眼光不错！\n一般来说照片越多越好");
        titleMap.put(7, "请输入密码");

        mBinding = (AcLoginBinding) binding;
        AdapterBinding.viewSize(mBinding.whiteBg, MineApp.W, 10);
        MineApp.registerInfo.setPhone(PreferenceUtil.readStringValue(activity, "userName"));
        mBinding.setPass(PreferenceUtil.readStringValue(activity, "loginPass"));
        mBinding.setRegisterInfo(MineApp.registerInfo);
        mBinding.setCanNext(false);
        mBinding.edNick.addTextChangedListener(this);
        mBinding.edPhone.addTextChangedListener(this);
        mBinding.edCode.addTextChangedListener(this);
        mBinding.edPass.addTextChangedListener(this);

        if (loginStep == 3) {
            toLogin(null);
        } else {
            mBinding.setLoginStep(loginStep);
            mBinding.setRight("");
        }

        threeLogin = new ThreeLogin(activity, this::loginByUnion);
        ImUtils.getInstance().setCallBackForLogin(() -> {
            ImUtils.getInstance().setCallBackForLogin(null);
            ActivityUtils.getMainActivity();
            MineApp.registerInfo = new RegisterInfo();
            timer.cancel();
            passErrorCount = 0;
            activity.finish();
        });

        array[0] = mBinding.tvCode1;
        array[1] = mBinding.tvCode2;
        array[2] = mBinding.tvCode3;
        array[3] = mBinding.tvCode4;
        timer = new CountDownTimer(second * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                canGetCode = false;
                mBinding.setCodeRemark(activity.getResources().getString(R.string.code_second, millisUntilFinished / 1000));
            }

            public void onFinish() {
                mBinding.setCodeRemark("验证码没收到？重新试试！");
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
                moreImageList.set(0, MineApp.registerInfo.getUnionImage());
                mBinding.setIsThree(true);
            }
        };

        aMapLocation = new AMapLocation(activity);
        MineApp.cityName = PreferenceUtil.readStringValue(activity, "cityName");
        getPermissions(0);

        setAdapter();
    }

    public void onDestroy() {
        try {
            cameraReceiver.unregisterReceiver();
            bindPhoneReceiver.unregisterReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (outAlpha != null)
            outAlpha = null;
    }

    /**
     * 更新单图
     */
    public void setSingleLogo(String filePath) {
        mBinding.setCanNext(true);
        if (moreImageList.get(_position).isEmpty()) {
            for (int i = 0; i < moreImageList.size(); i++) {
                if (moreImageList.get(i).isEmpty()) {
                    _position = i;
                    moreImageList.set(_position, filePath);
                    adapter.notifyItemChanged(_position);
                    if (_position == 0)
                        mBinding.setImageUrl(moreImageList.get(0));
                    return;
                }
            }
        } else {
            moreImageList.set(_position, filePath);
            adapter.notifyItemChanged(_position);
            if (_position == 0)
                mBinding.setImageUrl(moreImageList.get(0));
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
                    SCToastUtil.showToast(activity, "再按一次退出程序", true);
                    exitTime = System.currentTimeMillis();
                } else {
                    timer.cancel();
                    ImUtils.getInstance().loginOutIM();
                    MineApp.exit();
                    System.exit(0);
                }
                break;
            case 1: // 昵称页返回
                mBinding.setIsThree(false);
                step(0);
                break;
            case 2: // 生日页返回
                step(1);
                break;
            case 3: // 手机号页返回
                if (mBinding.getRight().isEmpty()) {
                    step(2);
                } else {
                    mBinding.setRight("");
                    mBinding.setIsThree(false);
                    step(0);
                }
                break;
            case 4: // 验证码页返回
            case 5: // 个人头像返回  直接返回手机页
                step(3);
                break;
            case 6:
                step(5);
                break;
            case 7:
                mBinding.setRight("密码登录");
                step(3);
                break;
        }
    }

    @Override
    public void right(View view) {
        super.right(view);
        hintKeyBoard();
        if (TextUtils.equals(mBinding.getRight(), "密码登录")) {
            if (!MineApp.registerInfo.getPhone().matches(MineApp.PHONE_NUMBER_REG)) {
                SCToastUtil.showToast(activity, "请输入正确的手机号", true);
                return;
            }
            mBinding.setRight("验证码登录");
            step(7);
        } else {
            if (canGetCode)
                loginCaptcha();
            else {
                mBinding.setRight("密码登录");
                step(4);
            }
        }
    }

    @Override
    public void setAdapter() {
        adapter = new MainAdapter<>(activity, R.layout.item_logo, moreImageList, this);
        SimpleItemTouchHelperCallback callback = new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mBinding.imagesList);
        callback.setSort(true);
        callback.setDragFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT);

        photoManager = new PhotoManager(activity, () -> {
            MineApp.registerInfo.setMoreImages(photoManager.jointWebUrl("#"));
            photoManager.deleteAllFile();
            if (MineApp.registerInfo.getOpenId().isEmpty())
                register();
            else {
                MineApp.registerInfo.setUnionImage(MineApp.registerInfo.getMoreImages().split("#")[0]);
                loginByUnion();
            }
        });
    }

    @Override
    public void selectSex(int sex) {
        MineApp.registerInfo.setSex(sex);
        showRule();// 跳转昵称页
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
            if (mBinding.getRight().isEmpty()) {
                registerCaptcha();
            } else {
                loginCaptcha();
            }
        } else {
            SCToastUtil.showToast(activity, "短信验证码已发送，请稍后再重试", true);
        }
    }

    @Override
    public void upload(View view) {
        _position = 0;
        getPermissions(1);
    }

    @Override
    public void selectImage(int position) {
        if (moreImageList.get(position).isEmpty()) {
            _position = position;
            getPermissions(1);
        } else {
            ArrayList<String> imageList = new ArrayList<>();
            for (String s : moreImageList) {
                if (!s.isEmpty()) {
                    imageList.add(s);
                }
            }
            MNImage.imageBrowser(activity, mBinding.getRoot(), imageList, position, true, position12 -> {
                adapter.notifyItemRemoved(position12);
                moreImageList.remove(position12);
                moreImageList.add("");
                adapter.notifyDataSetChanged();
            });
        }
    }

    @Override
    public void toLogin(View view) {
        mBinding.setRight("密码登录");
        step(3);
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
                case 1: // 跳转生日页
                    step(2);
                    break;
                case 2: // 跳转手机页
                    step(3);
                    break;
                case 3: // 跳转验证码页
                    if (!MineApp.registerInfo.getPhone().matches(MineApp.PHONE_NUMBER_REG)) {
                        SCToastUtil.showToast(activity, "请输入正确的手机号", true);
                    } else {
                        if (mBinding.getRight().isEmpty()) {
                            registerCaptcha();
                        } else {
                            loginCaptcha();
                        }
                    }
                    break;
                case 4: // 跳转个人头像页 or 验证码登录
                    if (mBinding.getRight().isEmpty()) {
                        step(5);
                    } else {
                        loginByCaptcha();
                    }
                    break;
                case 5:
                    step(6);
                    break;
                case 6:
                    StringBuilder images = new StringBuilder();
                    for (String image : moreImageList) {
                        if (!image.isEmpty()) {
                            images.append("#").append(image);
                        }
                    }
                    if (images.length() == 0) {
                        SCToastUtil.showToast(activity, "请上传至少1张照片", false);
                        return;
                    }
                    images = new StringBuilder(images.substring(1));
                    photoManager.addFiles(Arrays.asList(images.toString().split("#")), () -> photoManager.reUploadByUnSuccess());
                    break;
                case 7:
                    loginByPass();
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
    public void registerCaptcha() {
        // 注册验证码
        registerCaptchaApi api = new registerCaptchaApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                SCToastUtil.showToast(activity, "验证码已发送，请注意查收", false);
                if (mBinding.getLoginStep() == 3) {
                    step(4);
                    mBinding.setCodeRemark(activity.getResources().getString(R.string.code_second, second));
                    timer.start();
                }
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == 12) {
                    new TextPW(mBinding.getRoot(), "温馨提示", "该手机号已注册过，是否前往登录？", "去登录", () -> {
                        mBinding.setCodeRemark("");
                        mBinding.setRight("密码登录");
                        mBinding.setBtnName("获取登录验证码");
                        loginCaptcha();
                    });
                }
            }
        }, activity)
                .setUserName(MineApp.registerInfo.getPhone());
        api.setPosition(1);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void loginCaptcha() {
        loginCaptchaApi api = new loginCaptchaApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                SCToastUtil.showToast(activity, "验证码已发送，请注意查收", false);
                mBinding.setRight("密码登录");
                if (mBinding.getLoginStep() == 3 || mBinding.getLoginStep() == 7) {
                    step(4);
                    mBinding.setCodeRemark(activity.getResources().getString(R.string.code_second, second));
                    timer.start();
                }
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
                    ActivityUtils.getBindingPhoneActivity(activity);
                } else {
                    SCToastUtil.showToast(activity, "登录成功", true);
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
                .setUserName(MineApp.registerInfo.getPhone())
                .setCaptcha(mBinding.edCode.getText().toString())
                .setNick(MineApp.registerInfo.getName())
                .setBirthday(MineApp.registerInfo.getBirthday())
                .setMoreImages(MineApp.registerInfo.getMoreImages())
                .setSex(MineApp.registerInfo.getSex())
                .setProvinceId(AreaDb.getInstance().getProvinceId(PreferenceUtil.readStringValue(activity, "provinceName")))
                .setCityId(AreaDb.getInstance().getCityId(MineApp.cityName))
                .setDistrictId(AreaDb.getInstance().getDistrictId(PreferenceUtil.readStringValue(activity, "districtName")));
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

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.ERROR) {
                    if (TextUtils.equals(e.getMessage(), "账号尚未注册")) {
                        new TextPW(mBinding.getRoot(), "完善个人信息", "请完成个人信息填写流程", "去完善", () -> {
                            mBinding.setRight("");
                            mBinding.setIsThree(false);
                            step(0);
                        });
                    }
                }
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
                PreferenceUtil.saveStringValue(activity, "loginPass", mBinding.getPass());
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
                                    mBinding.setRight("密码登录");
                                    step(4);
                                }
                            });
                        }
                    }

                }
            }
        }, activity)
                .setUserName(MineApp.registerInfo.getPhone())
                .setPassWord(mBinding.getPass());
        api.setPosition(1);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void myInfo() {
        myInfoApi api = new myInfoApi(new HttpOnNextListener<MineInfo>() {
            @Override
            public void onNext(MineInfo o) {
                MineApp.mineInfo = o;
                ImUtils.getInstance().setChat(false, activity);
            }
        }, activity);
        api.setPosition(1);
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
        if (mBinding.getLoginStep() == 1)
            mBinding.setCanNext(!content.isEmpty());
        else if (mBinding.getLoginStep() == 3) {
            mBinding.setCanNext(content.length() == 11);
        } else if (mBinding.getLoginStep() == 4) {
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
                if (mBinding.getRight().isEmpty()) {
                    step(5);
                } else {
                    loginByCaptcha();
                }
            }
        } else if (mBinding.getLoginStep() == 7) {
            mBinding.setCanNext(content.length() >= 6);
        }
    }

    private void showRule() {
        if (PreferenceUtil.readIntValue(activity, "ruleType1") == 0) {
            new Handler().postDelayed(() -> new RulePW(activity, mBinding.getRoot(), 1, new RulePW.CallBack() {
                @Override
                public void sureBack() {
                    PreferenceUtil.saveIntValue(activity, "ruleType1", 1);
                    step(1);
                }

                @Override
                public void cancelBack() {

                }
            }), 200);
        } else {
            step(1);
        }
    }

    private void step(int step) {
        mBinding.setLoginStep(step);
        switch (step) {
            case 0:
                outAlpha(mBinding.step0Layout);
                MineApp.registerInfo.setOpenId("");
                MineApp.registerInfo.setUnionId("");
                mBinding.setIsThree(false);
                break;
            case 1:
                outAlpha(mBinding.step1Layout);
                AdapterBinding.viewSize(mBinding.whiteView, MineApp.W / 6, 10);
                mBinding.setBtnName("继续");
                mBinding.setCanNext(!MineApp.registerInfo.getName().isEmpty());
                break;
            case 2:
                outAlpha(mBinding.step2Layout);
                titleMap.put(2, "嗨 " + MineApp.registerInfo.getName() + "！\n您的生日是？");
                AdapterBinding.viewSize(mBinding.whiteView, MineApp.W / 3, 10);
                mBinding.setBtnName("继续");
                mBinding.setCanNext(!MineApp.registerInfo.getBirthday().isEmpty());
                break;
            case 3:
                if (mBinding.getRight().isEmpty()) {
                    mBinding.setBtnName("获取注册验证码");
                } else {
                    mBinding.setBtnName("获取登录验证码");
                }
                outAlpha(mBinding.step3Layout);
                AdapterBinding.viewSize(mBinding.whiteView, MineApp.W / 2, 10);
                mBinding.setCanNext(MineApp.registerInfo.getPhone().length() == 11);
                if (!MineApp.registerInfo.getPhone().isEmpty()) {
                    mBinding.edPhone.setText(MineApp.registerInfo.getPhone());
                    mBinding.edPhone.setSelection(MineApp.registerInfo.getPhone().length());
                }
                break;
            case 4:
                outAlpha(mBinding.step4Layout);
                AdapterBinding.viewSize(mBinding.whiteView, mBinding.getRight().isEmpty() ? MineApp.W * 2 / 3 : MineApp.W, 10);
                mBinding.setCanNext(false);
                mBinding.setBtnName("确认");
                mBinding.edCode.setText("");
                break;
            case 5:
                outAlpha(mBinding.step5Layout);
                AdapterBinding.viewSize(mBinding.whiteView, MineApp.W * 5 / 6, 10);
                mBinding.setCanNext(!moreImageList.get(0).isEmpty());
                mBinding.setBtnName("确认上传");
                mBinding.setImageUrl(moreImageList.get(0));
                break;
            case 6:
                outAlpha(mBinding.step6Layout);
                AdapterBinding.viewSize(mBinding.whiteView, MineApp.W, 10);
                mBinding.setBtnName("开始使用吧");
                mBinding.setCanNext(true);
                break;
            case 7:
                outAlpha(mBinding.step7Layout);
                AdapterBinding.viewSize(mBinding.whiteView, MineApp.W, 10);
                mBinding.setBtnName("登录");
                mBinding.setCanNext(MineApp.registerInfo.getPass().length() > 5);
                if (!mBinding.getPass().isEmpty()) {
                    mBinding.edPass.setText(mBinding.getPass());
                    mBinding.edPass.setSelection(mBinding.getPass().length());
                }
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
        PreferenceUtil.saveStringValue(activity, "deviceCode", tm.getDeviceId());
        String iccid = tm.getSimSerialNumber();
        // CDMA手机返回MEID
        String meid = tm.getDeviceId();
        // GSM手机返回IMEI
        String imei = tm.getDeviceId();
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

    private ObjectAnimator outAlpha;

    private void outAlpha(View view) {
        outAlpha = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).setDuration(500);
        outAlpha.start();
        new Handler().postDelayed(() -> {
            if (outAlpha != null)
                outAlpha.cancel();
            outAlpha = null;
        }, 500);
    }
}
