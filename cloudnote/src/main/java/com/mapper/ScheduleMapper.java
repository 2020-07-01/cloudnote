package com.mapper;

import com.entity.Condition;
import com.entity.Schedule;
import com.service.ScheduleService;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleMapper {

    //保存活动
    int insertSchedule(Schedule schedule);

    //查询所有活动
    List<Schedule> selectScheduleByCondition(Condition condition);

    //撤销活动
    void removeScheduleByCondition(Condition condition);


    List<Schedule> selectExcuteTimeByCondition(Condition condition);

    List<Schedule> selectContentByCondition(Condition condition);

    //获取提前量
    Schedule selectAdvanceByCondition(Condition condition);

    //更新日程
    void updateIsNeedRemind(List<Schedule> list);

    void updateSchedule(Schedule schedule);
}
