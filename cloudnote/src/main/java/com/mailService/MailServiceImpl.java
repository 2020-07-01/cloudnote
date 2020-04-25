package com.mailService;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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
     * 发送普通文本邮件
     *
     * @param sender
     * @param receiver
     * @param title
     * @param context
     * @return
     */
    @Override
    public String sendSecurityCode(String sender, String receiver, String title, String context) {

        //简单邮件模型，对邮件的属性进行了封装
        SimpleMailMessage mainMessage = new SimpleMailMessage();
        mainMessage.setFrom(sender);
        mainMessage.setTo(receiver);
        mainMessage.setSubject(title);
        mainMessage.setText(context);

        try {
            javaMailSender.send(mainMessage);
        } catch (Exception e) {
            log.error(e.getMessage(), new Throwable(e));
        }
        return "success";
    }

    @Override
    public boolean sendSchedule(String sender, String receiver, String title, String content) {
        SimpleMailMessage mainMessage = new SimpleMailMessage();
        mainMessage.setFrom(sender);
        mainMessage.setTo(receiver);
        mainMessage.setSubject(title);
        mainMessage.setText(content);
        javaMailSender.send(mainMessage);
        return true;
    }

}
