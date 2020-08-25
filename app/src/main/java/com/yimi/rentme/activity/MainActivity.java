package com.yimi.rentme.activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;
import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yimi.rentme.BR;
import com.yimi.rentme.R;
import com.yimi.rentme.vm.MainViewModel;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.JobInfoDb;
import com.zb.lib_base.db.TagDb;
import com.zb.lib_base.model.JobInfo;
import com.zb.lib_base.model.Tag;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.utils.SimulateNetAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import io.realm.Realm;

@Route(path = RouteUtils.Main_MainActivity)
public class MainActivity extends AppBaseActivity {
    private TagDb tagDb;
    private JobInfoDb jobInfoDb;
    private MainViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.ac_main;
    }

    @Override
    public void initUI() {
        tagDb = new TagDb(Realm.getDefaultInstance());
        jobInfoDb = new JobInfoDb(Realm.getDefaultInstance());
        viewModel = new MainViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);

        if (tagDb.get().size() == 0) {
            String data = SimulateNetAPI.getOriginalFundData(activity, "tag.json");
            try {
                JSONArray array = new JSONArray(data);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.optJSONObject(i);
                    Tag tag = new Tag();
                    tag.setName(object.optString("name"));
                    tag.setTags(object.optString("tags"));
                    tagDb.saveTag(tag);
                }
            } catch (Exception e) {
            }
        }
        if (jobInfoDb.getJobList("").size() == 0) {
            String data = SimulateNetAPI.getOriginalFundData(activity, "job.json");
            try {
                JSONArray array = new JSONArray(data);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.optJSONObject(i);
                    JSONArray jobArray = object.optJSONArray("job");
                    assert jobArray != null;
                    for (int j = 0; j < jobArray.length(); j++) {
                        JSONObject jobObject = jobArray.optJSONObject(j);
                        JobInfo jobInfo = new JobInfo();
                        jobInfo.setJobTitle(object.optString("jobTitle"));
                        jobInfo.setJob(jobObject.optString("name"));
                        jobInfoDb.saveJobInfo(jobInfo);
                    }
                }
            } catch (Exception e) {
            }
        }
        setNotificationChannel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
        viewModel.stopAnimator();
    }

    /**
     * 适配8.0通知栏
     */
    private void setNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = MineApp.NOTIFICATION_CHANNEL_ID;
            String channelName = "虾菇消息通知";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationChannel.enableLights(true); //是否在桌面icon右上角展示小红点
            notificationChannel.setLightColor(Color.RED); //小红点颜色
            notificationChannel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (notificationManager != null)
                notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    // 监听程序退出
    private long exitTime = 0;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                SCToastUtil.showToast(activity, "再按一次退出程序", true);
                exitTime = System.currentTimeMillis();
            } else {
                System.exit(0);
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
