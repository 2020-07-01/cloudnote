package com.service.serviceImpl;

import com.cache.CacheService;
import com.entity.Condition;
import com.entity.Schedule;
import com.entity.TextValue;
import com.mapper.ScheduleMapper;
import com.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public Map getScheduleList(Integer accountId) {
        Condition condition = new Condition();
        condition.setAccountId(accountId);
        List<Schedule> listSchedule = scheduleMapper.selectScheduleByCondition(condition);
        HashMap<String, String> data = new HashMap();
        String styleStart = "<p style=\"text-align:left;padding-left:15px;line-height :19px\">";
        String styleEnd = "<br></p>";
        String styleBr = "<br>";
        for (int i = 0; i < listSchedule.size(); i++) {

            StringBuilder stringBuilder = new StringBuilder();
            String date = listSchedule.get(i).getExecuteTime().substring(0, 10);
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
        if (scheduleList != null) {

            for (Schedule schedule : scheduleList) {
                result.put(schedule.getScheduleContent(), schedule.getExecuteTime());
            }
        } else {
            result.put("false", "该用户在当前日期下未创建日程!");
        }
        return result;
    }

    @Override
    public Map selectCotnentByCondition(Condition condition) {
        HashMap result = new HashMap();
        List<Schedule> scheduleList = scheduleMapper.selectContentByCondition(condition);

        if (scheduleList != null) {
            for (Schedule schedule : scheduleList) {
                result.put(schedule.getScheduleId(), schedule.getScheduleContent());
            }
        } else {
            result.put("false", "该用户在当前日期下未创建日程!");
        }
        return result;
    }

    @Override
    public Map selectAdvanceByCondition(Condition condition) {
        HashMap<String, String> result = new HashMap();
        Schedule schedule = scheduleMapper.selectAdvanceByCondition(condition);

        return result;
    }

    @Override
    public Map updateIsNeedRemind(List<Schedule> list) {
        HashMap<String, String> result = new HashMap<>();
        try {
            scheduleMapper.updateIsNeedRemind(list);
            result.put("true", "更新成功!");
        } catch (Exception e) {
            e.printStackTrace();
            e.getMessage();
            result.put("false", "更新失败!");
        }
        return result;
    }

    @Override
    public Map selectScheduleByCondition(Condition condition) {
        List<Schedule> schedules = scheduleMapper.selectScheduleByCondition(condition);
        HashMap data = new HashMap();
        ArrayList<TextValue> textValues = new ArrayList<>();
        for (Schedule schedule : schedules) {
            TextValue textValue = new TextValue();
            textValue.setKey(schedule.getExecuteTime());
            textValue.setValue(schedule.getExecuteTime());

            textValues.add(textValue);
        }

        data.put("textValues", textValues);
        return data;
    }

    @Override
    public List<Schedule> selectScheduleByExecuteTime(Condition condition) {
        List<Schedule> schedules = scheduleMapper.selectScheduleByCondition(condition);
        return schedules;
    }

    @Override
    public boolean updateSchedule(Schedule schedule) {
        try {
            scheduleMapper.updateSchedule(schedule);
        } catch (Exception e) {
            e.printStackTrace();
            e.getMessage();
        }
        return true;
    }

}
