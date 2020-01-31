package com.controller;


import com.alibaba.fastjson.JSONObject;
import com.entity.Schedule;
import com.entity.Task;
import com.interceptorService.TokenUtil;
import com.resultUtil.Json;
import com.resultUtil.Result;
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
    TaskServiceImpl taskService;;

    @RequestMapping(value = "/save_task")
    public void insertNote(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) throws ParseException {
        String token = request.getHeader("token");
        Integer userId = tokenUtil.getUserIdByToken(token);
        JSONObject jsonObject = JSONObject.parseObject(jsonString);

        Schedule schedule = new Schedule();
        schedule.setUserId(userId);
        schedule.setScheduleContent(jsonObject.getString("taskContent"));
        schedule.setExecuteTime(jsonObject.getString("executeTime"));
        schedule.setIsObsolete("1");
        //获取执行时间
        String executeTime = jsonObject.getString("executeTime");
        //计算showExecuteTime
        String showExecuteTime = executeTime.substring(0,10);
        schedule.setShowExecuteTime(showExecuteTime);
        //获取提前量
        Long hour = 0L;
        Long minute = 0L;
        String hourTemp = jsonObject.getString("hour");
        String minuteTemp = jsonObject.getString("minute");
        if( !hourTemp.equals("")){
            hour = Long.parseLong(hourTemp .substring(0,hourTemp.length()-2));
        }
        if( !minuteTemp.equals("")){
            minute = Long.parseLong(minuteTemp.substring(0,minuteTemp.length()-2));
        }

        //计算发送邮件的时间
        SimpleDateFormat dateFormat =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 =  dateFormat.parse(executeTime);

        Long milliSecond1 = date1.getTime();
        Long milliSecond2 = milliSecond1 -  hour * 60 * 60 * 1000 - minute * 60 * 1000;

        String s  = getRemindTime(milliSecond2);

        //设置发送邮件的时间
        schedule.setRemindTime(s);
        //设置是否进行邮件提醒
        if(jsonObject.getString("isRemind").equals("true")){
            schedule.setIsRemind("1");
            Task task = new Task();
            task.setUserId(userId);
            task.setSendTime(s);

            taskService.insert(task);
        }else {
            schedule.setIsRemind("0");
        }


        Map result = scheduleService.insertSchedule(schedule);
        if (result.get("true") != null) {
            Json.toJson(new Result(true, (String) result.get("true")), response);
        } else {
            Json.toJson(new Result(false, (String) result.get("false")), response);
        }
    }


    private String getRemindTime(long milliSecond){
        Date date = new Date(milliSecond);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(format.format(date));
        return format.format(date);
    }


    @RequestMapping(value = "/schedule_list")
    public void scheduleList(HttpServletRequest request, HttpServletResponse response){
        String token = request.getHeader("token");
        Integer userId = tokenUtil.getUserIdByToken(token);

        Map data = scheduleService.selectSchedule(userId);

        Json.toJson(new Result(true, (String) "查询成功",data), response);
    }


    /**
     * 撤销操作
     * @param jsonString
     * @param request
     * @param response
     */
    @RequestMapping(value = "/remove_schedule")
    public void delete(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response){
        String token = request.getHeader("token");
        Integer userId = tokenUtil.getUserIdByToken(token);
        JSONObject jsonObject = JSONObject.parseObject(jsonString);

        String scheduleId = jsonObject.getString("executeTime");
        //Map result = scheduleService.insertSchedule();

      /*  if (result.get("true") != null) {
            Json.toJson(new Result(true, (String) result.get("true")), response);
        } else {
            Json.toJson(new Result(false, (String) result.get("false")), response);
        }*/


    }

}
