package com.job;

import com.entity.Condition;
import com.entity.Schedule;
import com.entity.TaskData;
import com.mailService.MailServiceImpl;
import com.service.ScheduleService;
import com.service.TaskJobService;
import com.service.serviceImpl.ScheduleServiceImpl;
import com.service.serviceImpl.TaskJobServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @program: ScheduleJob
 * @description: 创建一个job
 * @create: 2020-01-31 14:45
 **/
@Component
@Configuration
@EnableScheduling
public class ScheduleJob {

    @Autowired
    MailServiceImpl mailService;

    @Autowired
    TaskJobServiceImpl taskJobService;

    @Autowired
    ScheduleServiceImpl scheduleService;

    /**
     * 日程提醒
     * 2分钟刷新一次 发送邮件
     */
    //@Scheduled(cron = "0 */2 * * * ?")
    public void scheduleRemind() {

        /**
         * 两分钟刷新一次，当前时间点再延迟一分钟
         * 包括前面但是不包括后面
         * 设置一个时间范围
         */
        Condition condition = new Condition();
        condition.setIsNeedRemind("1");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date();
        Long time = date.getTime() + 60 * 1000;

        String endTime = format.format(new Date(time));
        String startTime = format.format(new Date());

        condition.setStartTime(startTime);
        condition.setEndTime(endTime);

        List<TaskData> list = taskJobService.selectTaskDataByCondition(condition);

        List<Schedule> listScheduleId = new ArrayList<>();

        if (listScheduleId == null) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            TaskData taskData = list.get(i);
            String recevier = taskData.getEmail();
            String sender = "2422321558@qq.com";
            String title = taskData.getScheduleTitle();
            String content = taskData.getScheduleContent();
            mailService.sendSchedule(sender, recevier, title, content);
            //存储id
            listScheduleId.add(new Schedule(taskData.getScheduleId(), "0"));
        }
        //scheduleService.updateIsNeedRemind(listScheduleId);
    }


    /**
     * 每天刷新一次，生日祝福
     */
    public void birthdayBlessing(){

    }


}
