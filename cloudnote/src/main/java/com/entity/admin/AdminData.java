package com.entity.admin;

import com.entity.Account;
import lombok.Data;

/**
 * 用户信息列表返回实体类
 */
@Data
public class AdminData {

    public AdminData(Account account) {
        this.accountId = account.getAccountId();
        this.accountName = account.getAccountName();
        this.email = account.getEmail();
        this.isLocked = account.getIsLocked();
        this.isOnline = account.getIsOnline();
        this.createTime = account.getCreateTime();
        this.loginCount = account.getLoginCount();
        this.phone = account.getPhone();
        this.lastLoginTime = account.getLastLoginTime();
    }

    private String accountId;
    private String accountName;
    private String email;
    private String phone;
    private String createTime;
    private String isOnline;
    private String isLocked;
    private Integer loginCount;
    private String note;
    private String file;
    private String image;
    private String lastLoginTime;

}
