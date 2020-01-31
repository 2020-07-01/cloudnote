package com.mailService;


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
            return "fail";
        }
        return "success";
    }


    @Override
    public String sendHtml(String sender, String receiver, String subject) {
        return null;
    }

   /* @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;


    *//**
     * 发送简单邮件
     *
     * @param sender   发送者
     * @param receiver 接受者
     * @param title    邮件标题
     * @param context  邮件内容
     * @return
     *//*
    @Override
    public String send(String sender, String receiver, String title, String context) {


    }

    *//**
     * 使用thymleaf模板发送邮件
     *
     * @param sender
     * @param receiver
     * @param subject
     * @param
     * @return
     *//*
    @Override
    public String sendHtml(String sender, String receiver, String subject, EmailParam emailParam) {

        try {
            MimeMessage mainMessage = jms.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mainMessage, true);

            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(receiver);
            mimeMessageHelper.setSubject(subject);


            // 添加正文（使用thymeleaf模板）
            Context context = new Context();
            //为模板设置参数上下文
            context.setVariable("emailParam", emailParam);

            //html模板的相对路径
            String templatePath = "email";
            String emailText = templateEngine.process(templatePath, context);

            mimeMessageHelper.setText(emailText, true);

            jms.send(mainMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
            return "fail";
        }
        return "success";
    }*/
}
