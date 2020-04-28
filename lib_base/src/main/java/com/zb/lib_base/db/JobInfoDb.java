package com.zb.lib_base.db;

import com.zb.lib_base.model.JobInfo;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class JobInfoDb extends BaseDao {

    public JobInfoDb(Realm realm) {
        super(realm);
    }

    public void saveJobInfo(JobInfo jobInfo) {
        beginTransaction();
        realm.insertOrUpdate(jobInfo);
        commitTransaction();
    }

    public List<JobInfo> getJobList(String jobTitle) {
        beginTransaction();
        List<JobInfo> jobInfoList = new ArrayList<>();
        RealmResults<JobInfo> results;
        if (jobTitle.isEmpty()) {
            results = realm.where(JobInfo.class).findAll();
        } else {
            results = realm.where(JobInfo.class).equalTo("jobTitle", jobTitle).findAll();
        }
        if (results.size() > 0) {
            jobInfoList.addAll(results);
        }
        commitTransaction();
        return jobInfoList;
    }
}
