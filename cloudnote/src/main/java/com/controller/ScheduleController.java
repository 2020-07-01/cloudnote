package com.controller;

import com.Util.DateUtils;
import com.Util.Json;
import com.Util.Result;
import com.alibaba.fastjson.JSONObject;
import com.entity.Condition;
import com.entity.Schedule;
import com.entity.Task;
import com.interceptorService.TokenUtil;
import com.service.serviceImpl.ScheduleServiceImpl;
import com.service.serviceImpl.TaskServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @program: TaskController
 * @description:
 * @create: 2020-01-30 00:07
 **/
@RestController
@RequestMapping(value = "/schedule")
public class ScheduleController {

    @Autowired
    TokenUtil tokenUtil;

    @Autowired
    ScheduleServiceImpl scheduleService;

    @Autowired
    TaskServiceImpl taskService;
    ;

    @RequestMapping(value = "/save_task")
    public void insertNote(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) throws ParseException {
        String token = request.getHeader("token");
        Integer userId = tokenUtil.getAccountIdByToken(token);
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        Schedule schedule = new Schedule();
        schedule.setUserId(userId);
        schedule.setScheduleContent(jsonObject.getString("taskContent"));
        schedule.setExecuteTime(jsonObject.getString("executeTime"));
        schedule.setIsObsolete("1");
        //获取执行时间
        String executeTime = jsonObject.getString("executeTime");

        if (DateUtils.isBefore(executeTime, DateUtils.getCurrentDate())) {
            Json.toJson(new Result(false, "不能创建空白任务"), response);
            return;
        }

        //计算showExecuteTime
        String showExecuteTime = executeTime.substring(0, 10);
        schedule.setShowExecuteTime(showExecuteTime);
        //获取提前量
        Long hour = 0L;
        Long minute = 0L;
        String advanceHour = jsonObject.getString("hour");
        String advanceMinute = jsonObject.getString("minute");

        if (!advanceHour.equals("")) {
            hour = Long.parseLong(advanceHour.substring(0, advanceHour.length() - 2));
        }
        if (!advanceMinute.equals("")) {
            minute = Long.parseLong(advanceMinute.substring(0, advanceMinute.length() - 2));
        }
        schedule.setAdvanceHour(hour.intValue());
        schedule.setAdvanceMinute(minute.intValue());
        //计算发送邮件的时间
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = dateFormat.parse(executeTime);

        Long milliSecond1 = date1.getTime();
        Long milliSecond2 = milliSecond1 - hour * 60 * 60 * 1000 - minute * 60 * 1000;

        String s = getRemindTime(milliSecond2);

        //设置发送邮件的时间
        schedule.setRemindTime(s);
        //设置是否进行邮件提醒
        if (jsonObject.getString("isRemind").equals("true")) {
            schedule.setIsRemind("1");
            Task task = new Task();
            task.setUserId(userId);
            task.setSendTime(s);

            taskService.insert(task);
        } else {
            schedule.setIsRemind("0");
        }

        Map result = scheduleService.insertSchedule(schedule);
        if (result.get("true") != null) {
            Json.toJson(new Result(true, (String) result.get("true")), response);
        } else {
            Json.toJson(new Result(false, (String) result.get("false")), response);
        }
    }

    private String getRemindTime(long milliSecond) {
        Date date = new Date(milliSecond);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(format.format(date));
        return format.format(date);
    }


    @RequestMapping(value = "/schedule_list")
    public void scheduleList(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("token");
        Integer userId = tokenUtil.getAccountIdByToken(token);

        Map data = scheduleService.selectSchedule(userId);

        Json.toJson(new Result(true, (String) "查询成功", data), response);
    }


    /**
     * 撤销操作
     *
     * @param jsonString
     * @param request
     * @param response
     */
    @RequestMapping(value = "/remove_schedule")
    public void delete(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("token");
        Integer userId = tokenUtil.getAccountIdByToken(token);
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        String executeTime = jsonObject.getString("executeTime");
        Condition condition = new Condition();
        condition.setUserId(userId);
        condition.setExecuteTime(executeTime);
        Map map = scheduleService.removeSchedule(condition);
        if (map.get("true") != null) {
            Result result = new Result(true, (String) map.get("true"));
            Json.toJson(result, response);
        } else {
            Result result = new Result(false, (String) map.get("false"));
            Json.toJson(result, response);
        }
    }

    /**
     * 查询执行时间初始化下拉框
     *
     * @param jsonString
     * @param request
     * @param response
     */
    @RequestMapping(value = "/get_execute_time")
    public void getExecuteTime(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("token");
        Integer userId = tokenUtil.getAccountIdByToken(token);
        JSONObject jsonObject = JSONObject.parseObject(jsonString);

        String showExecuteTime = jsonObject.getString("showExecuteTime");

        Condition condition = new Condition();
        condition.setUserId(userId);
        condition.setShowExecuteTime(showExecuteTime);
        Map data = scheduleService.slelectExecuteTime(condition);
        Json.toJson(new Result(true, (String) "查询成功", data), response);

    }

    /**
     * 查询内容
     *
     * @param jsonString
     * @param request
     * @param response
     */
    @RequestMapping(value = "/get_execute_content")
    public void getExecuteContent(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("token");
        Integer userId = tokenUtil.getAccountIdByToken(token);
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        String executeTime = jsonObject.getString("executeTime");
        Condition condition = new Condition();

        condition.setUserId(userId);
        condition.setExecuteTime(executeTime);

        Map data = scheduleService.selectCotnentByCondition(condition);
        Json.toJson(new Result(true, (String) "查询成功", data), response);
    }


    /**
     * 获取日程的提前量
     *
     * @param jsonString
     * @param request
     * @param response
     */
    @RequestMapping(value = "/get_advance_time")
    public void getAdvanceTime(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("token");
        Integer userId = tokenUtil.getAccountIdByToken(token);
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        String executeTime = jsonObject.getString("executeTime");
        Condition condition = new Condition();
        condition.setUserId(userId);
        condition.setExecuteTime(executeTime);
        Map<String, String> data = scheduleService.selectAdvanceByCondition(condition);
        Result result = new Result(true, "", data);
        Json.toJson(result, response);
    }


    /**
     * 更新日程
     *
     * @param jsonString
     * @param request
     * @param response
     */
    @RequestMapping(value = "/update_schedule")
    public void updateSchedule(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {

        String token = request.getHeader("token");
        Integer userId = tokenUtil.getAccountIdByToken(token);
        JSONObject jsonObject = JSONObject.parseObject(jsonString);

        Schedule schedule = new Schedule();
        schedule.setUserId(userId);
        String executeTime = jsonObject.getString("executeTime");
        schedule.setExecuteTime(executeTime);
        String advanceHour = jsonObject.getString("advanceHour");
        String advanceMinute = jsonObject.getString("advanceMinute");
        //获取提前量
        Long hour = 0L;
        Long minute = 0L;

        if (!advanceHour.equals("")) {
            hour = Long.parseLong(advanceHour.substring(0, advanceHour.length() - 2));
        }
        if (!advanceMinute.equals("")) {
            minute = Long.parseLong(advanceMinute.substring(0, advanceMinute.length() - 2));
        }
        schedule.setAdvanceHour(hour.intValue());
        schedule.setAdvanceMinute(minute.intValue());
        schedule.setScheduleContent(jsonObject.getString("scheduleContent"));


        String remindTime = DateUtils.parse(hour, minute, executeTime);
        schedule.setRemindTime(remindTime);
        Map<String, String> data = scheduleService.updateSchedule(schedule);
        if (data.get("true") != null) {
            Result result = new Result(true, data.get("true"));
            Json.toJson(result, response);
        } else {
            Result result = new Result(false, data.get("false"));
            Json.toJson(result, response);
        }
    }

}

