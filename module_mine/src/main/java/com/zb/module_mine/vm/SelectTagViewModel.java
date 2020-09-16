package com.zb.module_mine.vm;

import android.content.Intent;
import android.view.View;

import com.zb.lib_base.db.TagDb;
import com.zb.lib_base.model.Tag;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.utils.SimulateNetAPI;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.adapter.MineAdapter;
import com.zb.module_mine.iv.SelectTagVMInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.databinding.ViewDataBinding;

public class SelectTagViewModel extends BaseViewModel implements SelectTagVMInterface {
    public String serviceTags;
    public MineAdapter selectAdapter;
    public MineAdapter tabAdapter;
    public MineAdapter tagAdapter;

    private List<Tag> tags = new ArrayList<>();
    public List<String> selectList = new ArrayList<>();
    private List<String> tabList = new ArrayList<>();
    private List<String> tagList = new ArrayList<>();

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);

        if (TagDb.getInstance().get().size() == 0) {
            String data = SimulateNetAPI.getOriginalFundData(activity, "tag.json");
            try {
                JSONArray array = new JSONArray(data);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.optJSONObject(i);
                    Tag tag = new Tag();
                    tag.setName(object.optString("name"));
                    tag.setTags(object.optString("tags"));
                    TagDb.getInstance().saveTag(tag);
                }
            } catch (Exception e) {
            }
        }
        tags = TagDb.getInstance().get();
        for (Tag tag : tags) {
            tabList.add(tag.getName());
        }

        if (!serviceTags.isEmpty()) {
            selectList.addAll(Arrays.asList(serviceTags.substring(1, serviceTags.length() - 1).split("#")));
        }
        tagList.addAll(Arrays.asList(tags.get(0).getTags().split(",")));

        setAdapter();
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void setAdapter() {
        selectAdapter = new MineAdapter<>(activity, R.layout.item_tag_select, selectList, this);
        tabAdapter = new MineAdapter<>(activity, R.layout.item_tab, tabList, this);
        tabAdapter.setSelectIndex(0);
        tagAdapter = new MineAdapter<>(activity, R.layout.item_tag, tagList, this);
    }

    @Override
    public void submit(View view) {
        if (selectList.size() == 0) {
            SCToastUtil.showToast(activity, "请选择标签", true);
            return;
        }
        String tags = "#";
        for (String item : selectList) {
            tags += item + "#";
        }
        Intent data = new Intent("lobster_member");
        data.putExtra("type", 4);
        data.putExtra("content", tags);
        activity.sendBroadcast(data);
        activity.finish();
    }

    @Override
    public void deleteTag(int position) {
        selectList.remove(position);
        selectAdapter.notifyDataSetChanged();
        tagAdapter.notifyDataSetChanged();
        mBinding.setVariable(BR.showTag, selectList.size() > 0);
    }

    @Override
    public void selectTab(int position) {
        tabAdapter.setSelectIndex(position);
        tabAdapter.notifyDataSetChanged();
        tagList.clear();
        tagList.addAll(Arrays.asList(tags.get(position).getTags().split(",")));
        tagAdapter.notifyDataSetChanged();
    }

    @Override
    public void selectTag(int position) {
        if (selectList.contains(tagList.get(position))) {
            selectList.remove(tagList.get(position));
            selectAdapter.notifyDataSetChanged();
        } else {
            if (selectList.size() >= 6) {
                SCToastUtil.showToast(activity, "最多发布6个标签哦", true);
                return;
            }
            selectList.add(tagList.get(position));
            selectAdapter.notifyDataSetChanged();
        }
        tagAdapter.notifyItemChanged(position);
        mBinding.setVariable(BR.showTag, selectList.size() > 0);
    }


}
