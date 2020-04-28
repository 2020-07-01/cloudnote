package com.entity;

import lombok.Data;

/**
 * 需要发送邮件的实体类
 */
@Data
public class TaskData {

    private String scheduleId;
    private String accountId;
    private String email;
    private String scheduleContent;
    private String scheduleTitle;
    private String isNeedRemind;
    private String remindTime;
}
