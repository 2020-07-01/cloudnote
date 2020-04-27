package com.service.serviceImpl;

import com.entity.Condition;
import com.entity.schedule.Schedule;
import com.mapper.ScheduleMapper;
import com.service.ScheduleService;
import org.apache.commons.collections4.CollectionUtils;
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


    //创建日程
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

    //获取日程列表
    @Override
    public Map getScheduleList(Condition condition) {
        List<Schedule> listSchedule = scheduleMapper.selectScheduleByCondition(condition);
        HashMap<String, String> data = new HashMap();
        String styleStart = "<p style=\"text-align:left;padding-left:15px;line-height :19px\">";
        String styleEnd = "<br></p>";
        String styleBr = "<br>";
        for (int i = 0; i < listSchedule.size(); i++) {
            StringBuilder stringBuilder = new StringBuilder();
            String date = listSchedule.get(i).getStartTime().substring(0, 10);
            String title = listSchedule.get(i).getScheduleTitle().length() > 10 ? listSchedule.get(i).getScheduleTitle().substring(0, 8) + "..." : listSchedule.get(i).getScheduleTitle();
            if (data.get(date) != null) {

                String oldData = data.get(date);
                String newData = oldData.replace(styleEnd, "");
                stringBuilder.append(newData);
                stringBuilder.append(styleBr);

                stringBuilder.append(title);
                stringBuilder.append(styleEnd);
                data.put(date, stringBuilder.toString());
            } else {
                stringBuilder.append(styleStart);
                stringBuilder.append(title);
                stringBuilder.append(styleEnd);
                data.put(date, stringBuilder.toString());
            }
        }
        return data;
    }

    //初始化当日的日程
    @Override
    public List<Schedule> getCurrentDaySchedule(Condition condition) {
        List<Schedule> scheduleList = scheduleMapper.selectScheduleByCondition(condition);
        return scheduleList;
    }

    //根据id获取日程信息
    @Override
    public Schedule getSchedule(Condition condition) {
        List<Schedule> scheduleList = scheduleMapper.selectScheduleByCondition(condition);
        if (CollectionUtils.isNotEmpty(scheduleList)) {
            return scheduleList.get(0);
        } else {
            return null;
        }
    }

}
