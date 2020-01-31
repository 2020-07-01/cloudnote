package com.service.serviceImpl;

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
    public Map removeSchedule(Integer scheduleId) {
        HashMap<String, String> result = new HashMap();
        try {
            scheduleMapper.removeScheduleByScheduleId(scheduleId);
            result.put("true", "撤销成功!");
        } catch (Exception e) {

        }
        return result;
    }

}
