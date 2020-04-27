package com.entity;

import lombok.Data;

/**
 * @program: Condition
 * @description: 检索条件
 * @create: 2019-12-26 18:09
 **/
@Data
public class Condition {


    public Condition() {

    }

    public Condition(String currentDay) {
        this.currentDay = currentDay;
    }

    private String key;
    private String name;
    private String accountPassword;
    private String email;
    private String accountName;
    private String phone;
    private String currentDay;
    private String showExecuteTime;
    private String executeTime;
    private String imageType;
    private String imageName;
    private Integer imageId;
    private String isRecycle;
    private String type;
    private String accountId;
    private String star;
    private String startTime;
    private String endTime;
    private String isNeedRemind;
    private Integer startNumber;
    private Integer pageSize;
    private String scheduleId;
}
