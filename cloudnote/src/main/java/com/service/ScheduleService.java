package com.service;

import com.entity.Schedule;

import java.util.Map;

public interface ScheduleService {
    //保存活动
    Map insertSchedule(Schedule schedule);

    //查询活动
    Map selectSchedule(Integer userId);

    //撤销活动
    Map removeSchedule(Integer scheduleId);
}
