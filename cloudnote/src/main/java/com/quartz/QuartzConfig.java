package com.quartz;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;

/**
 * @program: QuartzConfig
 * @description:
 * @create: 2020-01-31 15:04
 **/
@Configuration
public class QuartzConfig {

    /**
     * 创建job对象
     *
     * @return
     */
    @Bean
    public JobDetail printTimeJobDetail() {

        return JobBuilder.newJob(ScheduleJob.class)//PrintTimeJob我们的业务类
                .withIdentity("DateTimeJob")//可以给该JobDetail起一个id
                .storeDurably()//即使没有Trigger关联时，也不需要删除该JobDetail
                .build();
    }

    /**
     * 创建调度参数的配置
     *
     * @return
     */
    @Bean
    public Trigger printTimeJobTrigger() {
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule("0 0/5 * * * ?");
        return TriggerBuilder.newTrigger()
                .forJob(printTimeJobDetail())//关联上述的JobDetail
                .withIdentity("quartzTaskService")//给Trigger起个名字
                .withSchedule(cronScheduleBuilder)
                .build();
    }

}