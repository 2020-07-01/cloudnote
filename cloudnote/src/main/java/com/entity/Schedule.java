package com.entity;

/**
 * @program: ScheduleService
 * @description:
 * @create: 2020-01-30 13:08
 **/
public class Schedule {

    //任务id
    private Integer scheduleId;
    //用户id
    private Integer userId;
    //任务内容
    private String scheduleContent;
    //创建时间
    private String createTime;
    //修改时间
    private String updateTime;
    //执行时间
    private String executeTime;
    //提醒时间
    private String remindTime;
    //是否已经过期
    private String isObsolete;
    //受否使用邮件进行提醒
    private String isRemind;

    private String showExecuteTime;


    public Integer getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getScheduleContent() {
        return scheduleContent;
    }

    public void setScheduleContent(String scheduleContent) {
        this.scheduleContent = scheduleContent;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }


    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(String executeTime) {
        this.executeTime = executeTime;
    }

    public String getRemindTime() {
        return remindTime;
    }

    public void setRemindTime(String remindTime) {
        this.remindTime = remindTime;
    }

    public String getIsObsolete() {
        return isObsolete;
    }

    public void setIsObsolete(String isObsolete) {
        this.isObsolete = isObsolete;
    }

    public String getIsRemind() {
        return isRemind;
    }

    public void setIsRemind(String isRemind) {
        this.isRemind = isRemind;
    }

    public String getShowExecuteTime() {
        return showExecuteTime;
    }

    public void setShowExecuteTime(String showExecuteTime) {
        this.showExecuteTime = showExecuteTime;
    }
}
