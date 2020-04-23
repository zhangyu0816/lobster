package com.zb.lib_base.db;


import com.zb.lib_base.model.Tag;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class TagDb extends BaseDao {

    public TagDb(Realm realm) {
        super(realm);
    }

    public List<Tag> get() {
        List<Tag> tagList = new ArrayList<>();
        beginTransaction();
        RealmResults<Tag> results = realm.where(Tag.class).findAll();
        if (results.size() > 0) {
            tagList.addAll(results);
        }
        commitTransaction();
        return tagList;
    }

    public void saveTag(Tag tag) {
        beginTransaction();
        realm.insertOrUpdate(tag);
        commitTransaction();
    }


}
