package com.entity.admin;

import lombok.Data;

/**
 * 用户信息列表返回实体类
 */
@Data
public class AdminData {

    private Integer accountId;
    private String accountName;
    private String sex;
    private String birthday;
    private String area;
    private String email;
    private String phone;
    private String createTime;
    private String isOnline;
    private String lastLoginTime;
    private String isLocked;
    private String loginCount;

}
