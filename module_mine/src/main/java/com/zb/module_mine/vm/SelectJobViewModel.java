package com.zb.module_mine.vm;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.zb.lib_base.db.JobInfoDb;
import com.zb.lib_base.model.JobInfo;
import com.zb.lib_base.utils.SimulateNetAPI;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.R;
import com.zb.module_mine.adapter.MineAdapter;
import com.zb.module_mine.iv.SelectJobVMInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.ViewDataBinding;

public class SelectJobViewModel extends BaseViewModel implements SelectJobVMInterface {
    public MineAdapter titleAdapter;
    public MineAdapter adapter;
    public String job = "";
    private List<String> titleList = new ArrayList<>();
    private List<JobInfo> jobInfoList = new ArrayList<>();
    private int _position = -1;
    private String selectJob = "";

    @Override
    public void back(View view) {
        super.back(view);
        select(view);
    }

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        titleList.add("信息技术");
        titleList.add("金融保险");
        titleList.add("娱乐服务");
        titleList.add("商业管理");
        titleList.add("工程制造");
        titleList.add("交通运输");
        titleList.add("文化传媒");
        titleList.add("公共事业");
        titleList.add("模特");
        titleList.add("学生");

        selectJob = job;

        if (JobInfoDb.getInstance().getJobList("").size() == 0) {
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
                        JobInfoDb.getInstance().saveJobInfo(jobInfo);
                    }
                }
            } catch (Exception ignored) {
            }
        }
        setAdapter();
    }

    @Override
    public void select(View view) {
        if (!TextUtils.equals(selectJob, job)) {
            Intent data = new Intent("lobster_member");
            data.putExtra("type", 1);
            data.putExtra("content", selectJob);
            activity.sendBroadcast(data);
        }
        activity.finish();
    }


    @Override
    public void setAdapter() {
        titleAdapter = new MineAdapter<>(activity, R.layout.item_mine_title, titleList, this);
        titleAdapter.setSelectIndex(0);
        jobInfoList.addAll(JobInfoDb.getInstance().getJobList(titleList.get(0)));

        adapter = new MineAdapter<>(activity, R.layout.item_mine_job, jobInfoList, this);
        _position = -1;
        for (int i = 0; i < jobInfoList.size(); i++) {
            if (TextUtils.equals(job, jobInfoList.get(i).getJob())) {
                _position = i;
                break;
            }
        }
        adapter.setSelectIndex(_position);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void selectPosition(int position) {
        super.selectPosition(position);

        titleAdapter.setSelectIndex(position);
        titleAdapter.notifyDataSetChanged();
        jobInfoList.clear();
        adapter.notifyDataSetChanged();

        jobInfoList.addAll(JobInfoDb.getInstance().getJobList(titleList.get(position)));
        _position = -1;
        for (int i = 0; i < jobInfoList.size(); i++) {
            if (TextUtils.equals(job, jobInfoList.get(i).getJob())) {
                _position = i;
                break;
            }
        }
        adapter.setSelectIndex(_position);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void selectJob(int position) {
        _position = position;
        selectJob = jobInfoList.get(_position).getJob();
        adapter.setSelectIndex(position);
        adapter.notifyDataSetChanged();
    }
}
