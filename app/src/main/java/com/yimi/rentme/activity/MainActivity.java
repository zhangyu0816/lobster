package com.yimi.rentme.activity;

import com.yimi.rentme.BR;
import com.yimi.rentme.R;
import com.yimi.rentme.vm.MainViewModel;
import com.zb.lib_base.db.JobInfoDb;
import com.zb.lib_base.db.TagDb;
import com.zb.lib_base.model.JobInfo;
import com.zb.lib_base.model.Tag;
import com.zb.lib_base.utils.SimulateNetAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import io.realm.Realm;

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
        fitComprehensiveScreen();
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.stopAnimator();
    }
}
