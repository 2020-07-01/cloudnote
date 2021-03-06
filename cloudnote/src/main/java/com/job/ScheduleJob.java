package com.job;

import com.entity.Condition;
import com.entity.Constant;
import com.entity.schedule.Schedule;
import com.entity.TaskData;
import com.mailService.MailServiceImpl;
import com.service.serviceImpl.ScheduleServiceImpl;
import com.service.serviceImpl.TaskJobServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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
@Slf4j
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
    @Scheduled(cron = "0 */2 * * * ?")
    public void scheduleRemind() {

        /**
         * 两分钟刷新一次，当前时间点再延迟一分钟
         * 设置一个时间范围
         */
        Condition condition = new Condition();
        condition.setIsNeedRemind(Constant.YES);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date();
        Long time = date.getTime() + 60 * 1000;

        String endTime = format.format(new Date(time));
        String startTime = format.format(new Date());

        condition.setStartTime(startTime);
        condition.setEndTime(endTime);
        List<TaskData> list = taskJobService.selectTaskDataByCondition(condition);
        if (CollectionUtils.isNotEmpty(list)) {
            List<Schedule> listSchedule = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                TaskData taskData = list.get(i);
                String receiver = taskData.getEmail();
                String title = taskData.getScheduleTitle();
                String content = taskData.getScheduleContent();
                 /*
                 对标题、内容、时间格式进行封装
                  */
                title = "【云笔记日程提醒】" + title;
                content = "<span style=\"font-size: 16px\">开始时间：</span>"+startTime +"<br>"+"<span style=\"font-size: 16px\">内容：</span>"+content;
                mailService.sendSchedule(receiver, title, content);
                //存储id
                listSchedule.add(new Schedule(taskData.getScheduleId(), Constant.NO));
            }
            //设置已发送
            scheduleService.updateScheduleList(listSchedule);
        }
        log.info(startTime + "刷新日程:SUCCESS");
    }
}
