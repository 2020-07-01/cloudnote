package com.entity.schedule;

/**
 * @Description TODO
 * @Author yq
 * @Date 2020/4/27 17:08
 */
public class ScheduleData {


    public ScheduleData(Schedule schedule) {
        this.accountId = schedule.getAccountId();
        this.scheduleId = schedule.getScheduleId();
        this.scheduleTitle = schedule.getScheduleTitle();
        this.scheduleContent = schedule.getScheduleContent();
        this.startTime = schedule.getStartTime();
        this.isNeedRemind = schedule.getIsNeedRemind();
    }


    //任务id
    private String scheduleId;
    //账户id
    private String accountId;
    //任务内容
    private String scheduleContent;
    //创建时间
    private String createTime;
    //修改时间
    private String updateTime;
    //执行时间
    private String startTime;
    //提前时间
    private String aheadTime;
    //是否需要提醒
    private String isNeedRemind;
    //标题
    private String scheduleTitle;
    //发送邮件的时间
    public String remindTime;

}
