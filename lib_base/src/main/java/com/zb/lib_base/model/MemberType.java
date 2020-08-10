package com.zb.lib_base.model;

import io.realm.RealmObject;

public class MemberType extends RealmObject {
    long userId;
    int memberType;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getMemberType() {
        return memberType;
    }

    public void setMemberType(int memberType) {
        this.memberType = memberType;
    }
}
