package com.service;

import com.entity.Condition;
import com.entity.schedule.Schedule;

import java.nio.channels.ShutdownChannelGroupException;
import java.util.List;
import java.util.Map;

public interface ScheduleService {
    //保存活动
    Map insertSchedule(Schedule schedule);

    //查询活动
    Map getScheduleList(Condition condition);

    //初始当日活动
    List<Schedule> getCurrentDaySchedule(Condition condition);

    //根据Condition获取日程信息
    Schedule getSchedule(Condition condition);

    //更新单个日程
    Map updateSchedule(Schedule schedule);

    //批量更新日程
    boolean updateScheduleList(List<Schedule> schedules);

    //删除日程
    Boolean deleteSchedule(Schedule schedule);

}
