package com.mailService;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author :qiang
 * @date :2019/11/30 下午11:24
 * @description :发送邮件服务类
 * @other :
 */
@Service
@Slf4j
public class MailServiceImpl implements MailService {

    @Autowired
    JavaMailSender javaMailSender;

    /**
     * 发送验证码
     * 进行封装
     *
     * @param receiver
     * @param context
     * @return
     */
    @Override
    public boolean sendSecurityCode(String receiver, String context) {
        //简单邮件模型，对邮件的属性进行了封装
        SimpleMailMessage mainMessage = new SimpleMailMessage();
        mainMessage.setFrom("2422321558@qq.com");
        mainMessage.setTo(receiver);
        mainMessage.setSubject("【云笔记】：验证码");
        mainMessage.setText("【云笔记】尊敬的用户：您的验证码为：" + context + ",请勿泄露给他人!");
        try {
            javaMailSender.send(mainMessage);
        } catch (Exception e) {
            log.error(e.getMessage(), new Throwable(e));
        }
        return true;
    }

    //日程提醒
    @Override
    public boolean sendSchedule(String receiver, String title, String content) {
        SimpleMailMessage mainMessage = new SimpleMailMessage();
        mainMessage.setFrom("2422321558@qq.com");
        mainMessage.setTo(receiver);
        mainMessage.setSubject("【云笔记】：您有新的活动即将开始:" + title);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("活动时间：");
        stringBuffer.append("活动内容：" + content);
        mainMessage.setText(content);
        javaMailSender.send(mainMessage);
        return true;
    }

    //管理员群发邮件
    @Override
    public boolean sendEmailsByAdmin(List<String> receiverList, String title, String content) {
        SimpleMailMessage mainMessage = new SimpleMailMessage();
        mainMessage.setFrom("2422321558@qq.com");
        mainMessage.setSubject(title);
        mainMessage.setText(content);
        if (CollectionUtils.isNotEmpty(receiverList)) {
            for (String emailAddress : receiverList) {
                mainMessage.setTo(emailAddress);
                javaMailSender.send(mainMessage);
            }
        }
        return true;
    }
}
