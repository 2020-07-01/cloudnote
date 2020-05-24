package com.controller;

import com.Util.*;
import com.alibaba.fastjson.JSONObject;
import com.cache.CacheService;
import com.entity.*;
import com.entity.schedule.Schedule;
import com.interceptor.TokenUtils;
import com.interceptor.UserLoginToken;
import com.service.serviceImpl.ScheduleServiceImpl;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
        String scheduleTitle = jsonObject.getString("scheduleTitle");
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


    /**
     * 删除日程
     *
     * @param scheduleId
     * @param request
     * @param response
     */
    @UserLoginToken
    @RequestMapping(value = "/delete_schedule.json")
    public void deleteSchedule(@RequestParam(value = "scheduleId") String scheduleId, HttpServletRequest request, HttpServletResponse response) {

        Result result;
        Schedule schedule = new Schedule();
        if (StringUtils.isNotEmpty(scheduleId)) {
            schedule.setScheduleId(scheduleId);
        }
        if (scheduleService.deleteSchedule(schedule)) {
            result = new Result(true, "SUCCESS");
        } else {
            result = new Result(false, "FAILURE");
        }
        Json.toJson(result, response);
    }

    /**
     * 修改日程
     * @param schedule
     * @param request
     * @param response
     */
    @UserLoginToken
    @RequestMapping(value = "/update_schedule.json")
    public void updateSchedule(@RequestBody Schedule schedule, HttpServletRequest request, HttpServletResponse response) {
        Result result;
        Schedule newSchedule = new Schedule();
        newSchedule.setScheduleId(schedule.getScheduleId());
        newSchedule.setScheduleTitle(schedule.getScheduleTitle());
        newSchedule.setScheduleContent(schedule.getScheduleContent());
        try {
            //不需要发送邮件
            if (schedule.getAheadTime().equals("0")) {
                schedule.setIsNeedRemind("NO");
                schedule.setRemindTime("");
            } else {
                //需要发送邮件
                schedule.setIsNeedRemind("YES");
                //重新计算发送邮件的时间
                Integer integerAheadTime = Integer.parseInt(schedule.getAheadTime());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date executeTimeDate = dateFormat.parse(schedule.getStartTime());
                Long remindTime = executeTimeDate.getTime() - integerAheadTime * 60 * 1000;
                String remindTimeString = dateFormat.format(remindTime).substring(0, 16);
                schedule.setRemindTime(remindTimeString);
            }
            Map<Boolean, String> map = scheduleService.updateSchedule(schedule);
            if (StringUtils.isNotEmpty(map.get(true))) {
                result = new Result(true, "SUCCESS!");
            } else {
                result = new Result(false, "FAILURE!");
            }
        } catch (Exception e) {
            result = new Result(false, "异常错误!");
            log.error(e.getMessage(), new Throwable(e));
        }
        Json.toJson(result, response);
    }
}
