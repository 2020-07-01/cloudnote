package com.service;

import com.entity.Condition;
import com.entity.Schedule;
import org.checkerframework.checker.units.qual.C;

import java.util.List;
import java.util.Map;

public interface ScheduleService {
    //保存活动
    Map insertSchedule(Schedule schedule);

    //查询活动
    Map getScheduleList(Integer accountId);

    //撤销活动
    Map removeSchedule(Condition condition);

    //查询时间
    Map slelectExecuteTime(Condition condition);

    //查询内容
    Map selectCotnentByCondition(Condition condition);

    //查询提前量
    Map selectAdvanceByCondition(Condition condition);

    //更新日程
    Map updateSchedule(Schedule schedule);

    Map selectScheduleByCondition(Condition condition);

    List<Schedule> selectScheduleByExecuteTime(Condition condition);
}
