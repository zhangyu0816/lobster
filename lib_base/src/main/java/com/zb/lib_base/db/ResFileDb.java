package com.zb.lib_base.db;


import com.zb.lib_base.model.ResFile;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by DIY on 2019-03-06.
 */

public class ResFileDb extends BaseDao {
    public ResFileDb(Realm realm) {
        super(realm);
    }

    /**
     * 保存资源文件
     *
     * @param resFile
     */
    public void saveResFile(ResFile resFile) {
        beginTransaction();
        realm.insertOrUpdate(resFile);
        commitTransaction();
    }

    /**
     * 获取资源文件
     *
     * @param resLink
     * @return
     */
    public ResFile getResFile(String resLink) {
        beginTransaction();
        ResFile resFile = realm.where(ResFile.class).equalTo("resLink", resLink).findFirst();
        commitTransaction();
        return resFile;
    }

    /**
     * 已读
     *
     * @param resLink
     * @return
     */
    public boolean isRead(String resLink) {
        beginTransaction();
        ResFile resFile = realm.where(ResFile.class).equalTo("resLink", resLink).findFirst();
        commitTransaction();
        return resFile != null;
    }

    /**
     * 删除资源文件
     *
     * @param resLink
     */
    public void deleteResFile(String resLink) {
        beginTransaction();
        ResFile resFile = realm.where(ResFile.class).equalTo("resLink", resLink).findFirst();
        resFile.deleteFromRealm();
        commitTransaction();
    }

    public void deleteAll() {
        beginTransaction();
        RealmResults<ResFile> results = realm.where(ResFile.class).findAll();
        if (results.size() > 0) {
            results.deleteAllFromRealm();
        }
        commitTransaction();
    }
}
