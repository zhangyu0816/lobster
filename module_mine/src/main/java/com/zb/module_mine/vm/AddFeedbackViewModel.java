package com.zb.module_mine.vm;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.api.addFeedBackApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.FeedbackInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DataCleanManager;
import com.zb.lib_base.utils.MNImage;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.utils.uploadImage.PhotoManager;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.TextPW;
import com.zb.module_mine.R;
import com.zb.module_mine.adapter.MineAdapter;
import com.zb.module_mine.databinding.MineAddFeedbackBinding;
import com.zb.module_mine.iv.AddFeedbackVMInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.databinding.ViewDataBinding;

public class AddFeedbackViewModel extends BaseViewModel implements AddFeedbackVMInterface {
    public FeedbackInfo feedbackInfo;
    public MineAdapter adapter;
    private MineAddFeedbackBinding mBinding;
    public List<String> images = new ArrayList<>();
    private PhotoManager photoManager;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (MineAddFeedbackBinding) binding;
        mBinding.setTitle("添加反馈");
        setAdapter();
        photoManager = new PhotoManager(activity, () -> addFeedBack(photoManager.jointWebUrl(",")));
    }

    @Override
    public void back(View view) {
        super.back(view);
        MineApp.selectMap.clear();
        DataCleanManager.deleteFile(new File(activity.getCacheDir(), "images"));
        activity.finish();
    }

    @Override
    public void setAdapter() {
        images.add("add_image_icon");
        adapter = new MineAdapter<>(activity, R.layout.item_mine_feedback_image, images, this);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void selectPosition(int position) {
        super.selectPosition(position);
        if (TextUtils.equals(images.get(position), "add_image_icon")) {
            if (checkPermissionGranted(activity, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                setPermissions();
            } else {
                if (PreferenceUtil.readIntValue(activity, "photoPermission") == 0) {
                    PreferenceUtil.saveIntValue(activity, "photoPermission", 1);
                    new TextPW(activity, mBinding.getRoot(), "权限说明",
                            "提交反馈意见时需要使用上传图片功能，我们将会申请相机、存储权限：" +
                                    "\n 1、申请相机权限--上传图片时获取拍摄照片功能，" +
                                    "\n 2、申请存储权限--上传图片时获取保存和读取图片功能，" +
                                    "\n 3、若您点击“同意”按钮，我们方可正式申请上述权限，以便拍摄照片及选取照片，完善反馈信息，" +
                                    "\n 4、若您点击“拒绝”按钮，我们将不再主动弹出该提示，您也无法使用上传图片功能，不影响使用其他的虾菇功能/服务，" +
                                    "\n 5、您也可以通过“手机设置--应用--虾菇--权限”或app内“我的--设置--权限管理--权限”，手动开启或关闭相机、存储权限。",
                            "同意", false, true, this::getPermissions);
                } else {
                    if (!checkPermissionGranted(activity, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        SCToastUtil.showToast(activity, "你未开启存储权限，请前往我的--设置--权限管理--权限进行设置", true);
                    } else if (!checkPermissionGranted(activity, Manifest.permission.CAMERA)) {
                        SCToastUtil.showToast(activity, "你未开启相机权限，请前往我的--设置--权限管理--权限进行设置", true);
                    }
                }
            }
        } else {
            ArrayList<String> imageList = new ArrayList<>();
            for (int i = 0; i < images.size() - 1; i++) {
                imageList.add(images.get(i));
            }
            MNImage.imageBrowser(activity, mBinding.getRoot(), 0, imageList, position, true, position12 -> {
                try {
                    int count = MineApp.selectMap.remove(images.get(position12));
                    for (Map.Entry<String, Integer> entry : MineApp.selectMap.entrySet()) {
                        if (entry.getValue() > count) {
                            MineApp.selectMap.put(entry.getKey(), entry.getValue() - 1);
                        }
                    }
                } catch (Exception ignored) {

                }

                adapter.notifyItemRemoved(position12);
                images.remove(position12);
                adapter.notifyDataSetChanged();
            });
        }
    }

    @Override
    public void submit(View view) {
        if (feedbackInfo.getContent().isEmpty()) {
            SCToastUtil.showToast(activity, "请填写反馈内容", true);
            return;
        }
        if (images.size() == 1) {
            SCToastUtil.showToast(activity, "请上传图片证据", true);
            return;
        }

        List<String> imageList = new ArrayList<>();
        for (String url : images) {
            if (!TextUtils.equals(url, "add_image_icon")) {
                imageList.add(url);
            }
        }
        photoManager.addFiles(imageList, () -> photoManager.reUploadByUnSuccess());
    }

    @Override
    public void addFeedBack(String images) {
        addFeedBackApi api = new addFeedBackApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                SCToastUtil.showToast(activity, "提交成功", true);
                back(null);
            }
        }, activity).setContent(feedbackInfo.getContent()).setImages(images).setTitle("问题反馈");
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
                            PreferenceUtil.saveIntValue(activity, "photoPermission", 1);
                        }
                    }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else {
            setPermissions();
        }
    }

    private void setPermissions() {
        MineApp.toPublish = false;
        ActivityUtils.getCameraMain(activity, true, false, false);
    }
}
