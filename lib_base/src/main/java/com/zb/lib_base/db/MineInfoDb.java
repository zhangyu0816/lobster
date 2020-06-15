package com.zb.lib_base.db;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.model.MineInfo;

import io.realm.Realm;

public class MineInfoDb extends BaseDao {

    public MineInfoDb(Realm realm) {
        super(realm);
    }

    public void saveMineInfo(MineInfo mineInfo) {
        beginTransaction();
        realm.insertOrUpdate(mineInfo);
        commitTransaction();
    }

    public MineInfo getMineInfo() {
        beginTransaction();
        MineInfo mineInfo = realm.where(MineInfo.class).equalTo("userId", BaseActivity.userId).findFirst();
        commitTransaction();
        return mineInfo;
    }

    public void updateContent(String content, int type) {
        // type   1：工作  2：昵称
        beginTransaction();
        MineInfo mineInfo = realm.where(MineInfo.class).equalTo("userId", BaseActivity.userId).findFirst();
        if (mineInfo != null) {
            if (type == 1)
                mineInfo.setJob(content);
            else if (type == 2)
                mineInfo.setNick(content);
            else if (type == 3)
                mineInfo.setPersonalitySign(content);
            else if (type == 4)
                mineInfo.setServiceTags(content);
            else if (type == 5)
                mineInfo.setBirthday(content);

        }
        commitTransaction();
    }

    public void updateImages(String images) {
        beginTransaction();
        MineInfo mineInfo = realm.where(MineInfo.class).equalTo("userId", BaseActivity.userId).findFirst();
        if (mineInfo != null) {
            mineInfo.setImage(images.split("#")[0]);
            mineInfo.setMoreImages(images);
        }
        commitTransaction();
    }

}
