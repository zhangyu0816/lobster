package com.zb.module_mine.vm;

import android.Manifest;
import android.os.Build;
import android.view.View;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.AreaDb;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.SimpleItemTouchHelperCallback;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.SelectorPW;
import com.zb.module_mine.R;
import com.zb.module_mine.adapter.MineAdapter;
import com.zb.module_mine.databinding.MineEditMemberBinding;
import com.zb.module_mine.iv.EditMemberVMInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.ItemTouchHelper;
import io.realm.Realm;

public class EditMemberViewModel extends BaseViewModel implements EditMemberVMInterface {
    public MineAdapter adapter;
    public AreaDb areaDb;
    public List<String> imageList = new ArrayList<>();
    public int _position = 0;
    private SimpleItemTouchHelperCallback callback;
    private MineEditMemberBinding mineEditMemberBinding;
    private List<String> selectorList = new ArrayList<>();
    public MineInfo mineInfo;

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        selectorList.add("女");
        selectorList.add("男");
        mineEditMemberBinding = (MineEditMemberBinding) binding;
        areaDb = new AreaDb(Realm.getDefaultInstance());
        mineInfo = mineInfoDb.getMineInfo();
        imageList.addAll(Arrays.asList(mineInfo.getMoreImages().split("#")));
        setAdapter();
    }

    @Override
    public void setAdapter() {
        adapter = new MineAdapter<>(activity, R.layout.item_mine_image, imageList, this);
        callback = new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mineEditMemberBinding.imagesList);
        callback.setSort(true);
        callback.setDragFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT);
    }


    @Override
    public void save(View view) {

    }

    @Override
    public void selectImage(int position) {
        _position = position;
        getPermissions();
    }

    @Override
    public void toEditNick(View view) {
        ActivityUtils.getMineEditContent(2, 1, "编辑昵称", mineInfo.getNick(), "输入你的昵称，昵称不能为空");
    }

    @Override
    public void toSelectSex(View view) {
        new SelectorPW(activity, mBinding.getRoot(), selectorList, position -> mineInfo.setSex(position));
    }

    @Override
    public void toSelectJob(View view) {
        ActivityUtils.getMineSelectJob();
    }

    @Override
    public void toEditSign(View view) {
        ActivityUtils.getMineEditContent(3, 10, "编辑个性签名", mineInfo.getPersonalitySign(), "编辑个性签名...");
    }

    @Override
    public void toSelectTag(View view) {
        ActivityUtils.getMineSelectTag(mineInfo.getServiceTags());
    }

    /**
     * 权限
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            performCodeWithPermission("虾菇需要访问读外部存储权限", new BaseActivity.PermissionCallback() {
                @Override
                public void hasPermission() {
                    setPermissions();
                }

                @Override
                public void noPermission() {
                }
            }, Manifest.permission.READ_EXTERNAL_STORAGE);
        } else {
            setPermissions();
        }
    }

    private void setPermissions() {
        ActivityUtils.getCameraMain(activity, false);
    }
}
