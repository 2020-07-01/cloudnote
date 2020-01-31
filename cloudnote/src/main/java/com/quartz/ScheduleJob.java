package com.quartz;

import com.mailService.MailServiceImpl;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @program: QuzrtzJob
 * @description: 创建一个job
 * @create: 2020-01-31 14:45
 **/
public class ScheduleJob extends QuartzJobBean {

    @Autowired
    MailServiceImpl mailService;

    /**
     * 此方法下编写所要执行的job任务，具体的业务逻辑代码
     * @param jobExecutionContext
     * @throws JobExecutionException
     */
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        //查询数据库进行日期的比较

    }
}
