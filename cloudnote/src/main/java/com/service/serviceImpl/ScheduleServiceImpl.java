package com.service.serviceImpl;

import com.entity.Condition;
import com.entity.Schedule;
import com.mapper.ScheduleMapper;
import com.service.ScheduleService;
import com.service.TaskService;
import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: ScheduleImpl
 * @description:
 * @create: 2020-01-30 13:41
 **/
@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    ScheduleMapper scheduleMapper;


    @Override
    public Map insertSchedule(Schedule schedule) {
        Map<String, String> result = new HashMap();
        try {
            scheduleMapper.insertSchedule(schedule);
            result.put("true", "创建成功!");
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            result.put("false", "出现异常!");
        }
        return result;
    }

    @Override
    public Map selectSchedule(Integer userId) {
        List<Schedule> listSchedule = scheduleMapper.selectScheduleByUserId(userId);
        HashMap<String, String> data = new HashMap();

        for (Schedule schedule : listSchedule) {
            if(data.get(schedule.getShowExecuteTime()) == null){
                data.put(schedule.getShowExecuteTime(), schedule.getScheduleContent());
            }else{
                String string = data.get(schedule.getShowExecuteTime());
                string = string +"\r\n"+ schedule.getScheduleContent();
                data.put(schedule.getShowExecuteTime(), string);
            }

        }
        return data;
    }


    @Override
    public Map removeSchedule(Condition condition) {
        HashMap<String, String> result = new HashMap();
        try {
            scheduleMapper.removeScheduleByCondition(condition);
            result.put("true", "撤销成功!");
        } catch (Exception e) {

        }
        return result;
    }


    @Override
    public Map slelectExecuteTime(Condition condition) {
        HashMap result = new HashMap();
        List<Schedule> scheduleList = scheduleMapper.selectExcuteTimeByCondition(condition);
        if(scheduleList != null){

            for (Schedule schedule:scheduleList) {
                result.put(schedule.getScheduleContent(),schedule.getExecuteTime());
            }
        }else {
            result.put("false","该用户在当前日期下未创建日程!");
        }
        return result;
    }

    @Override
    public Map selectCotnentByCondition(Condition condition) {
        HashMap result = new HashMap();
        List<Schedule> scheduleList = scheduleMapper.selectContentByCondition(condition);

        if(scheduleList != null){
            for (Schedule schedule:scheduleList) {
                result.put(schedule.getScheduleId(),schedule.getScheduleContent());
            }
        }else {
            result.put("false","该用户在当前日期下未创建日程!");
        }
        return result;
    }

    @Override
    public Map selectAdvanceByCondition(Condition condition) {
        HashMap<String,String> result = new HashMap();
        Schedule schedule = scheduleMapper.selectAdvanceByCondition(condition);
            result.put("advanceHour",String.valueOf(schedule.getAdvanceHour()));
            result.put("advanceMinute",String.valueOf(schedule.getAdvanceMinute()));
        return result;
    }

    @Override
    public Map updateSchedule(Schedule schedule) {
        HashMap<String,String> result = new HashMap<>();
        try {
            scheduleMapper.updateSchedule(schedule);
            result.put("true","更新成功!");
        }catch (Exception e){
            result.put("false","更新失败!");
        }
        return result;
    }
}
