package com.entity;

/**
 * 发送给前端的实体
 */
public class AdminData {

    public AdminData(AccountPIMData accountPIMData){
        this.accountId = accountPIMData.getAccountId();
        this.accountName = accountPIMData.getAccountName();
        this.accountRole = accountPIMData.getAccountRole()
    }



    private Integer accountId;
    private String sex;
    private String zone;
    private String accountName;
    private String email;
    private String phone;
    private String createTime;
    private String lastLoginTime;
    private String accountRole;
    private String isClosed;

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getAccountRole() {
        return accountRole;
    }

    public void setAccountRole(String accountRole) {
        this.accountRole = accountRole;
    }

    public String getIsClosed() {
        return isClosed;
    }

    public void setIsClosed(String isClosed) {
        this.isClosed = isClosed;
    }
}
