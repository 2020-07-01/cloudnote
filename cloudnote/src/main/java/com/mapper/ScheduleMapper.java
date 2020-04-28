package com.mapper;

import com.entity.Condition;
import com.entity.schedule.Schedule;
import com.entity.schedule.ScheduleData;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleMapper {

    //保存活动
    Integer insertSchedule(Schedule schedule);

    //所有活动
    List<Schedule> selectScheduleByCondition(Condition condition);

    //更新日程
    Integer updateSchedule(Schedule schedule);

    //批量更新日程
    void updateScheduleList(List<Schedule> schedules);
}
