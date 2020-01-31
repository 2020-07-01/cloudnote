package com.mapper;

import com.entity.Schedule;
import com.service.ScheduleService;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleMapper {

    //保存活动
    int insertSchedule(Schedule schedule);

    //查询所有活动
    List<Schedule> selectScheduleByUserId(Integer userId);

    //撤销活动
    void removeScheduleByScheduleId(Integer scheduleId);

}
