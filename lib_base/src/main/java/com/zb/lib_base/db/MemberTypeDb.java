package com.zb.lib_base.db;

import com.zb.lib_base.model.MemberType;

import io.realm.Realm;

public class MemberTypeDb extends BaseDao {

    public MemberTypeDb(Realm realm) {
        super(realm);
    }

    public void saveMemberType(MemberType memberType) {
        beginTransaction();
        realm.insertOrUpdate(memberType);
        commitTransaction();
    }

    public MemberType getMemberType(long userId) {
        beginTransaction();
        MemberType memberType = realm.where(MemberType.class).equalTo("userId", userId).findFirst();
        commitTransaction();
        return memberType;
    }
}
