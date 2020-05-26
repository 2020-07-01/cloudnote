package com.entity.account;


import lombok.Data;

/**
 * @Description 返回实体类
 * @Author yq
 * @Date 2020/5/24 9:52
 */
@Data
public class AccountData {

    public AccountData(Account account){
        this.accountId = account.getAccountId();
        this.accountName = account.getAccountName();
        this.area = account.getArea();
        this.birthday = account.getBirthday();
        this.createTime  = account.getCreateTime();
        this.phone = account.getPhone();
        this.email = account.getEmail();
        this.sex = account.getSex();
        this.remark = account.getRemark();
        this.headImageUrl = account.getHeadImageUrl();
    }

    //账户id
    private String accountId;
    //账户名
    private String accountName;
    //邮件
    private String email;
    //手机号
    private String phone;
    //创建时间
    private String createTime;
    //性别
    private String sex;
    //头像地址
    private String headImageUrl;
    //区域
    private String area;
    //备注
    private String remark;
    //出生日期
    private String birthday;
    //空间大小
    private String size;

}
