package com.quartz;

import com.entity.Condition;
import com.entity.Task;
import com.mailService.MailServiceImpl;
import com.service.TaskService;
import com.service.serviceImpl.TaskServiceImpl;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.xml.crypto.Data;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @program: QuzrtzJob
 * @description: 创建一个job
 * @create: 2020-01-31 14:45
 **/
public class ScheduleJob extends QuartzJobBean {

    @Autowired
    MailServiceImpl mailService;

    @Autowired
    TaskServiceImpl taskService;

    /**
     * 此方法下编写所要执行的job任务，具体的业务逻辑代码
     * @param jobExecutionContext
     * @throws JobExecutionException
     */
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        //查询数据库进行日期的比较
        Condition condition = new Condition();
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDate = format.format(date);
        System.out.println("current date:"+ currentDate);
        condition.setCurrentDate(currentDate);
        List<Task> tasks = taskService.select(condition);
        if(tasks != null){
            for(Task task : tasks){
                System.out.println(task.toString());
            }
        }
        else {
            System.out.println("当前没有需要执行的定时任务");
        }

    }
}
