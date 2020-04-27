package com.controller;

import com.Util.*;
import com.alibaba.fastjson.JSONObject;
import com.cache.CacheService;
import com.entity.*;
import com.entity.schedule.Schedule;
import com.entity.schedule.ScheduleData;
import com.interceptor.UserLoginToken;
import com.service.serviceImpl.ScheduleServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @program: TaskController
 * @description:
 * @create: 2020-01-30 00:07
 **/
@RestController
@RequestMapping(value = "/schedule")
public class ScheduleController {

    @Autowired
    TokenUtils tokenUtil;

    @Autowired
    ScheduleServiceImpl scheduleService;

    @Autowired
    CacheService cacheService;


    /**
     * 创建日程
     * 同一天不能有两个相同的标题
     *
     * @param jsonString
     * @param request
     * @param response
     * @throws ParseException
     */
    @UserLoginToken
    @RequestMapping(value = "/save_schedule.json")
    public void insertNote(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) throws ParseException {

        String token = request.getHeader("token");
        String accountId = tokenUtil.getAccountIdByToken(token);
        JSONObject jsonObject = JSONObject.parseObject(jsonString);

        String scheduleContent = jsonObject.getString("scheduleContent");
        if (scheduleContent.trim().length() == 0) {
            Json.toJson(new Result(false, "内容不能为空!"), response);
        }
        String scheduleTitle = jsonObject.getString("scheduleTitle");
        if (scheduleTitle.trim().length() == 0) {
            Json.toJson(new Result(false, "标题不能为空!"), response);
        }

        Schedule schedule = new Schedule();
        schedule.setScheduleId(UUIDUtils.getUUID());
        schedule.setAccountId(accountId);
        schedule.setScheduleContent(scheduleContent);
        schedule.setScheduleTitle(scheduleTitle);
        String startTime = jsonObject.getString("startTime");
        if (StringUtils.isNotEmpty(startTime)) {
            schedule.setStartTime(startTime);
        }

        String aheadTime = jsonObject.getString("aheadTime");
      /*  //获取数据
        HashMap<String, String> aheadTimeMap = getAheadTimeMapCache(accountId);*/
        schedule.setAheadTime(aheadTime);
        //是否发送邮件
        if (aheadTime.equals("0")) {
            schedule.setIsNeedRemind("NO");
            schedule.setRemindTime("");
        } else {
            //需要发送邮件
            schedule.setIsNeedRemind("YES");
            //计算发送邮件的时间
            Integer integerAheadTime = Integer.parseInt(aheadTime);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date executeTimeDate = dateFormat.parse(startTime);
            Long remindTime = executeTimeDate.getTime() - integerAheadTime * 60 * 1000;
            String remindTimeString = dateFormat.format(remindTime).substring(0, 16);
            schedule.setRemindTime(remindTimeString);
        }

        Map result = scheduleService.insertSchedule(schedule);
        if (result.get("true") != null) {
            Json.toJson(new Result(true, "创建成功!"), response);
        } else {
            Json.toJson(new Result(false, "创建失败!"), response);
        }
    }

    /**
     * 获取缓存中的aheadTime
     *
     * @param accountId
     * @return
     */
    private HashMap getAheadTimeMapCache(String accountId) {
        String key = accountId + "aheadTIme";
        if (cacheService.getValue(key) == null) {
            HashMap<String, String> aheadTimeMap = new HashMap();
            aheadTimeMap.put("0", "不提醒");
            aheadTimeMap.put("5", "提前5分钟");
            aheadTimeMap.put("15", "提前15分钟");
            aheadTimeMap.put("30", "提前30分钟");
            aheadTimeMap.put("60", "提前1小时");
            aheadTimeMap.put("120", "提前两小时");
            cacheService.putValue(key, aheadTimeMap);
        }
        return (HashMap) cacheService.getValue(key);
    }


    /**
     * 日程列表
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/schedule_list.json")
    @UserLoginToken
    public void scheduleList(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("token");
        String accountId = tokenUtil.getAccountIdByToken(token);
        Condition condition = new Condition();
        condition.setAccountId(accountId);
        Map data = scheduleService.getScheduleList(condition);
        Json.toJson(new Result(true, "查询成功", data), response);
    }

    /**
     * 初始化当日所有的日程
     *
     * @param date
     * @param request
     * @param response
     */
    @UserLoginToken
    @RequestMapping(value = "/init_currentDay_schedule.json")
    public void getCurrentDayScheduleLit(@RequestParam(value = "date", defaultValue = "") String date,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {

        String token = request.getHeader("token");
        String accountId = tokenUtil.getAccountIdByToken(token);
        Condition condition = new Condition();
        condition.setAccountId(accountId);
        if (StringUtils.isNotEmpty(date)) {
            String currentDay = date.substring(0, 10);
            condition.setStartTime(currentDay);
        }
        HashMap data = new HashMap();
        List<Schedule> scheduleList = scheduleService.getCurrentDaySchedule(condition);
        if (CollectionUtils.isNotEmpty(scheduleList)) {
            ArrayList<TextValue> textValues = new ArrayList<>();
            for (Schedule item : scheduleList) {
                TextValue textValue = new TextValue(item.getScheduleId(), item.getScheduleTitle());
                textValues.add(textValue);
            }
            data.put("textValues", textValues);
        }
        Json.toJson(new Result(true, "SUCCESS", data), response);
    }

    /**
     * 根据scheduleId 获取单个日程
     *
     * @param jsonString
     * @param request
     * @param response
     */
    @UserLoginToken
    @RequestMapping(value = "/get_schedule.json")
    public void getSchedule(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {

        Result result;
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        String scheduleId = jsonObject.getString("scheduleId");
        if (StringUtils.isNotEmpty(scheduleId)) {
            Condition condition = new Condition();
            condition.setScheduleId(scheduleId);
            Schedule schedule = scheduleService.getSchedule(condition);
            result = new Result(true, "SUCCESS", schedule);
        } else {
            result = new Result(true, "SUCCESS", null);
        }
        Json.toJson(result, response);
    }
}
