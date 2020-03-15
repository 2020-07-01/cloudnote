package com.entity;

/**
 * @program: PIM
 * @description: 个人信息
 * @create: 2020-03-14 21:53
 **/
public class PIM {

    private Integer PIMId;
    private Integer accountId;
    private String sex;
    private String headImageUrl;
    private String province;
    private String city;
    private String remark;
    private String updateTime;

    public Integer getPIMId() {
        return PIMId;
    }

    public void setPIMId(Integer PIMId) {
        this.PIMId = PIMId;
    }

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

    public String getHeadImageUrl() {
        return headImageUrl;
    }

    public void setHeadImageUrl(String headImageUrl) {
        this.headImageUrl = headImageUrl;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
