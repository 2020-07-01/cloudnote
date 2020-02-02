package com.service;

import com.entity.Condition;
import com.entity.Schedule;

import java.util.Map;

public interface ScheduleService {
    //保存活动
    Map insertSchedule(Schedule schedule);

    //查询活动
    Map selectSchedule(Integer userId);

    //撤销活动
    Map removeSchedule(Condition condition);

    //查询时间
    Map slelectExecuteTime(Condition condition);

    //查询内容
    Map selectCotnentByCondition(Condition condition);

    //查询提前量
    Map selectAdvanceByCondition(Condition condition);
}
