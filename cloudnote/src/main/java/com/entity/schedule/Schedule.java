package com.entity.schedule;

import lombok.Data;

/**
 * @program: ScheduleService
 * @description: 日程实体类
 * @create: 2020-01-30 13:08
 **/
@Data
public class Schedule {

    public Schedule() {
    }

    public Schedule(String scheduleId, String isNeedRemind) {
        this.scheduleId = scheduleId;
        this.isNeedRemind = isNeedRemind;
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
