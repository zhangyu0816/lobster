package com.zb.lib_base.model;

import io.realm.RealmObject;

public class JobInfo extends RealmObject {
    private String jobTitle = "";
    private String job = "";

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }
}
