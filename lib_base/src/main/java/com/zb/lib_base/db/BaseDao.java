package com.zb.lib_base.db;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmResults;

/**
 * Created by admin on 2016/11/7.
 */

public class BaseDao {
    protected Realm realm;

    public BaseDao(Realm realm) {
        this.realm = realm;
    }

    public void beginTransaction() {
        if (!realm.isInTransaction()) realm.beginTransaction();
    }

    public void commitTransaction() {
        if (realm.isInTransaction()) realm.commitTransaction();
    }

    public void close() {
        realm.close();
    }

    /**
     * 分页查询（Realm不支持分页查询）
     *
     * @param data   所有的list数据
     * @param offset 上次的下标
     * @param limit  每页大小
     * @param <E>    返回数据
     * @return
     */
    public <E extends RealmModel> List<E> getLimitList(RealmResults<E> data, int offset, int limit) {
        List<E> obtainList = new ArrayList();
        Realm realm = Realm.getDefaultInstance();
        if (data.size() == 0) {
            return obtainList;
        }
        for (int i = offset; i < offset + limit; i++) {
            if (i >= data.size()) {
                break;
            }
            E temp = realm.copyFromRealm(data.get(i));
            obtainList.add(temp);
        }
        realm.close();
        return obtainList;
    }
}
