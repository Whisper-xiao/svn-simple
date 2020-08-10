package com.sduept.simple.vo;

import com.sduept.simple.entity.User;

public class UserVo extends User {

    private String roleName;

    private String type;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
