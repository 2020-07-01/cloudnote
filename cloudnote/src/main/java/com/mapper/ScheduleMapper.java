package com.mapper;

import com.entity.Condition;
import com.entity.schedule.Schedule;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleMapper {

    //保存活动
    int insertSchedule(Schedule schedule);

    //所有活动
    List<Schedule> selectScheduleByCondition(Condition condition);

}
