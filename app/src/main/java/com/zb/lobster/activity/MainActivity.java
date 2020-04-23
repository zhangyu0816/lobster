package com.zb.lobster.activity;

import com.zb.lib_base.db.TagDb;
import com.zb.lib_base.model.Tag;
import com.zb.lib_base.utils.SimulateNetAPI;
import com.zb.lobster.BR;
import com.zb.lobster.R;
import com.zb.lobster.vm.MainViewModel;

import org.json.JSONArray;
import org.json.JSONObject;

import io.realm.Realm;

public class MainActivity extends AppBaseActivity {
    private TagDb tagDb;

    @Override
    public int getRes() {
        return R.layout.ac_main;
    }

    @Override
    public void initUI() {
        fitComprehensiveScreen();
        tagDb = new TagDb(Realm.getDefaultInstance());
        MainViewModel viewModel = new MainViewModel();
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
    }
}
