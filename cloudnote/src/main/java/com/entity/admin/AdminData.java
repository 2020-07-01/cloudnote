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
        this.area = account.getArea();
        this.birthday = account.getBirthday();
        this.sex = account.getSex();
        this.email = account.getEmail();
        this.isLocked = account.getIsLocked();
        this.isOnline = account.getIsOnline();
        this.phone = account.getPhone();
        this.createTime = account.getCreateTime();

    }

    private Integer accountId;
    private String accountName;
    private String sex;
    private String birthday;
    private String area;
    private String email;
    private String phone;
    private String createTime;
    private String isOnline;
    private String isLocked;

}
