package com.zb.module_mine.vm;

import android.content.Intent;
import android.view.View;

import com.zb.lib_base.db.JobInfoDb;
import com.zb.lib_base.model.JobInfo;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.R;
import com.zb.module_mine.adapter.MineAdapter;
import com.zb.module_mine.iv.SelectJobVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.ViewDataBinding;
import io.realm.Realm;

public class SelectJobViewModel extends BaseViewModel implements SelectJobVMInterface {
    public MineAdapter titleAdapter;
    public MineAdapter adapter;
    private List<String> titleList = new ArrayList<>();
    private List<JobInfo> jobInfoList = new ArrayList<>();
    private JobInfoDb jobInfoDb;
    private int _position = -1;

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
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

        jobInfoDb = new JobInfoDb(Realm.getDefaultInstance());

        setAdapter();
    }

    @Override
    public void select(View view) {
        Intent data = new Intent("lobster_member");
        data.putExtra("type", 1);
        data.putExtra("content", jobInfoList.get(_position).getJob());
        activity.sendBroadcast(data);
        activity.finish();
    }


    @Override
    public void setAdapter() {
        titleAdapter = new MineAdapter<>(activity, R.layout.item_mine_title, titleList, this);
        titleAdapter.setSelectIndex(0);
        jobInfoList.addAll(jobInfoDb.getJobList(titleList.get(0)));
        adapter = new MineAdapter<>(activity, R.layout.item_mine_job, jobInfoList, this);
    }

    @Override
    public void selectPosition(int position) {
        super.selectPosition(position);

        titleAdapter.setSelectIndex(position);
        titleAdapter.notifyDataSetChanged();
        jobInfoList.clear();
        jobInfoList.addAll(jobInfoDb.getJobList(titleList.get(position)));
        _position = -1;
        adapter.setSelectIndex(_position);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void selectJob(int position) {
        _position = position;
        adapter.setSelectIndex(position);
        adapter.notifyDataSetChanged();
    }
}
