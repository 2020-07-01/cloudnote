package com.controller;

import com.Util.DateUtils;
import com.Util.Json;
import com.Util.Result;
import com.Util.TokenUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cache.CacheService;
import com.entity.Condition;
import com.entity.Schedule;
import com.entity.Task;
import com.service.serviceImpl.ScheduleServiceImpl;
import com.service.serviceImpl.TaskServiceImpl;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
    TokenUtils tokenUtil;

    @Autowired
    ScheduleServiceImpl scheduleService;

    @Autowired
    TaskServiceImpl taskService;

    @Autowired
    CacheService cacheService;

    @RequestMapping(value = "/save_task")
    public void insertNote(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) throws ParseException {
        String token = request.getHeader("token");
        Integer accountId = tokenUtil.getAccountIdByToken(token);
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
        schedule.setAccountId(accountId);
        schedule.setScheduleContent(scheduleContent);
        schedule.setScheduleTitle(scheduleTitle);
        String aheadTime = jsonObject.getString("aheadTime");
        int integerAheadTime = Integer.parseInt(aheadTime);
        //获取数据
        HashMap<String, String> aheadTimeMap = getAheadTimeMapCache(accountId);
        schedule.setAheadTime(aheadTimeMap.get(aheadTime));
        schedule.setScheduleContent(jsonObject.getString("taskContent"));

        String executeTime = jsonObject.getString("executeTime");
        schedule.setExecuteTime(executeTime);
        //是否发送邮件
        if (integerAheadTime == 0) {
            schedule.setIsNeedRemind("0");
        } else {
            schedule.setIsNeedRemind("1");
            //计算发送邮件的时间
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date executeTimeDate = dateFormat.parse(executeTime);
            Long remindTime = executeTimeDate.getTime() - integerAheadTime * 60 * 1000;
            String remindTimeString = dateFormat.format(remindTime);
            schedule.setRemindTime(remindTimeString);
        }

        Map result = scheduleService.insertSchedule(schedule);
        if (result.get("true") != null) {
            Json.toJson(new Result(true, (String) result.get("true")), response);
        } else {
            Json.toJson(new Result(false, (String) result.get("false")), response);
        }
    }

    /**
     * 获取缓存中的aheadTime
     *
     * @param accountId
     * @return
     */
    private HashMap getAheadTimeMapCache(int accountId) {
        String key = accountId + "aheadTIme";
        if (cacheService.getValue(key) != null) {
            HashMap<String, String> aheadTimeMap = new HashMap();
            aheadTimeMap.put("0", "不提醒");
            aheadTimeMap.put("15", "提前15分钟");
            aheadTimeMap.put("30", "提前30分钟");
            aheadTimeMap.put("60", "提前1小时");
            aheadTimeMap.put("120", "提前两小时");
            cacheService.putValue(key, aheadTimeMap);
        }
        return (HashMap) cacheService.getValue(key);
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


    /**
     * 判断当前点击的日期的是否过时
     */
    @RequestMapping(value = "/is_pass.json")
    public void isPass(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("token");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            JSONObject jsonObject = JSON.parseObject(jsonString);
            Date selectDate = format.parse(jsonObject.getString("selectDate"));
            Date currentDate = new Date();
            if (currentDate.getTime() + 60 * 10000 < selectDate.getTime()) {
                Json.toJson(new Result(true, "true"), response);
            } else {
                Json.toJson(new Result(true, "false"), response);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Json.toJson(new Result(false, "失败"), response);
        }
    }

}

