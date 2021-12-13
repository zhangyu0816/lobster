package com.zb.module_mine.vm;

import android.Manifest;
import android.os.Build;
import android.view.View;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.api.realNameVerifyApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.Authentication;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.TextPW;
import com.zb.module_mine.iv.GiftRecordVMInterface;

public class GiftRecordViewModel extends BaseViewModel implements GiftRecordVMInterface {

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void right(View view) {
        super.right(view);
        ActivityUtils.getMineWeb("提现帮助", HttpManager.BASE_URL + "mobile/xiagu_get_help.html");
    }

    @Override
    public void withdraw(View view) {
        if (PreferenceUtil.readIntValue(activity, "withdraw") == 0) {
            new TextPW(activity, mBinding.getRoot(), "提现说明", "您在使用提现服务时，为确认您所提供的身份信息、提现账号（银行卡信息或支付宝信息）为您本人信息且属实，需要您提供以下信息供我们收集：真实姓名、身份证号及身份证核验授权。" +
                    "真实姓名、身份证号属于个人敏感信息，如您选择不提供上述信息，您可能无法使用提现服务，但不影响其他服务。" +
                    "\n先在卡包内绑定提现账户，单次提现金额不少于10元。提交提现申请后，我们将会在3个工作日内打款至提现账户。", "同意",
                    false, true, () -> {
                PreferenceUtil.saveIntValue(activity, "withdraw", 1);
                if (MineApp.walletInfo.getCanWithdrawCreditWallet() > 0) {
                    realNameVerify(1);
                } else {
                    SCToastUtil.showToast(activity, "暂无可提现收益", true);
                }
            });
        } else {
            if (MineApp.walletInfo.getCanWithdrawCreditWallet() > 0) {
                realNameVerify(1);
            } else {
                SCToastUtil.showToast(activity, "暂无可提现收益", true);
            }
        }
    }

    @Override
    public void toProfitRecord(View view) {
        ActivityUtils.getMineTranRecord(32);
    }

    @Override
    public void toBindingZFB(View view) {
        if (PreferenceUtil.readIntValue(activity, "bindingZFB") == 0) {
            new TextPW(activity, mBinding.getRoot(), "绑定银行卡说明", "您在使用提现服务时，为确认您所提供的身份信息、提现账号（银行卡信息或支付宝信息）为您本人信息且属实，需要您提供以下信息供我们收集：真实姓名、身份证号及身份证核验授权。" +
                    "真实姓名、身份证号属于个人敏感信息，如您选择不提供上述信息，您可能无法使用提现服务，但不影响其他服务。" +
                    "\n先在卡包内绑定提现账户，单次提现金额不少于10元。提交提现申请后，我们将会在3个工作日内打款至提现账户。",
                    "同意", false, true, () -> {
                PreferenceUtil.saveIntValue(activity, "bindingZFB", 1);
                realNameVerify(2);
            });
        } else {
            realNameVerify(2);
        }
    }

    @Override
    public void incomeDeposit(View view) {
    }

    @Override
    public void realNameVerify(int position) {
        realNameVerifyApi api = new realNameVerifyApi(new HttpOnNextListener<Authentication>() {
            @Override
            public void onNext(Authentication o) {
                if (o.getIsChecked() == 1) {
                    if (position == 1)
                        ActivityUtils.getMineWithdraw();
                    else
                        ActivityUtils.getMineBindingBank();
                } else {
                    if (o.getIsChecked() == 0 || o.getIsChecked() == 100) {
                        new TextPW(mBinding.getRoot(), "实名认证", "实名认证还在审核中，请稍后再试！");
                    } else {
                        new TextPW(mBinding.getRoot(), "实名认证", "你的实名认证审核失败，请前往查看失败原因并重新提交！", "重新认证", () -> ActivityUtils.getMineAuthentication(o));
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    if (checkPermissionGranted(activity, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        setPermissions();
                    } else {
                        if (PreferenceUtil.readIntValue(activity, "realNamePermission") == 0)
                            new TextPW(activity, mBinding.getRoot(), "实名认证",
                                    "您在使用提现服务时，为确认您所提供的身份信息、提现账号（银行卡信息或支付宝信息）为您本人信息且属实，需要您提供以下信息供我们收集：真实姓名、身份证号及身份证核验授权，我们将会申请相机、存储权限：" +
                                            "\n 1、申请相机权限--上传图片时获取拍摄照片功能，" +
                                            "\n 2、申请存储权限--上传图片时获取保存和读取图片功能，" +
                                            "\n 4、若您点击“同意”按钮，我们方可正式申请上述权限，以便拍摄照片及选取照片，上传身份证正反面照片及本人头像照片，" +
                                            "\n 5、若您点击“拒绝”按钮，我们将不再主动弹出该提示，您也无法完成身份证认证，不影响使用其他的虾菇功能/服务，" +
                                            "\n 6、您也可以通过“手机设置--应用--虾菇--权限”或app内“我的--设置--权限管理--权限”，手动开启或关闭相机、存储权限，" +
                                            "\n 7、先在卡包内绑定提现账户，单次提现金额不少于10元。提交提现申请后，我们将会在3个工作日内打款至提现账户。",
                                    "同意", false, true, new TextPW.CallBack() {
                                @Override
                                public void sure() {
                                    PreferenceUtil.saveIntValue(activity, "realNamePermission", 1);
                                    getPermissions();
                                }

                                @Override
                                public void cancel() {
                                    PreferenceUtil.saveIntValue(activity, "realNamePermission", 1);
                                }
                            });
                        else {
                            if (!checkPermissionGranted(activity, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                SCToastUtil.showToast(activity, "你未开启存储权限，请前往我的--设置--权限管理--权限进行设置", true);
                            } else if (!checkPermissionGranted(activity, Manifest.permission.CAMERA)) {
                                SCToastUtil.showToast(activity, "你未开启相机权限，请前往我的--设置--权限管理--权限进行设置", true);
                            }
                        }
                    }
                }
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    /**
     * 权限
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            performCodeWithPermission("虾菇需要访问存储权限及相机权限", new BaseActivity.PermissionCallback() {
                        @Override
                        public void hasPermission() {
                            setPermissions();
                        }

                        @Override
                        public void noPermission() {
                            PreferenceUtil.saveIntValue(activity, "realNamePermission", 1);
                        }
                    }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else {
            setPermissions();
        }
    }

    private void setPermissions() {
        ActivityUtils.getMineAuthentication(new Authentication());
    }
}
