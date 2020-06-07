package com.mailService;


import com.entity.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;

/**
 * @author :qiang
 * @date :2019/11/30 下午11:24
 * @description :发送邮件服务类
 * @other :
 */
@Service
@Slf4j
public class MailServiceImpl implements MailService {


    /**
     * # 邮件配置
     * spring.mail.host=smtp.qq.com
     * spring.mail.username=2422321558@qq.com
     * spring.mail.password=bpdlxcsctpwwdijc
     * spring.mail.default-encoding=UTF-8
     * spring.mail.properties.mail.smtp.auth=true
     * spring.mail.properties.mail.smtp.starttls.enable=true
     * spring.mail.properties.mail.smtp.starttls.required=true
     * spring.mail.port=465
     * spring.mail.properties.mail.smtp.socketFactory.port = 465
     * spring.mail.properties.mail.smtp.socketFactory.class = javax.net.ssl.SSLSocketFactory
     * spring.mail.properties.mail.smtp.socketFactory.fallback = false
     */


    /**
     * 初始化session
     *
     * @return
     */
    private Session initSession() {
        Properties properties = new Properties();
        properties.setProperty("mail.host", "smtp.qq.com");
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.setProperty("mail.smtp.port", "465");
        Session session = Session.getDefaultInstance(properties, new SimpleAuthenticator("2422321558@qq.com", "tazuzkuclfbkdjhh"));
        session.setDebug(true);
        return session;
    }

    /**
     * 发送验证码
     *
     * @param form
     * @param receiver
     * @param subject
     * @param content
     * @return
     */
    @Override
    public boolean sendSecurityCode(String form, String receiver, String subject, String content) {

        Session session = initSession();
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(Constant.email_address_1);
            message.setRecipients(Message.RecipientType.TO, receiver);//设置邮件收信人
            message.setSubject(subject);
            message.setContent(content, "text/html;charset=utf-8");
            Transport transport = session.getTransport();
            transport.send(message);
        } catch (Exception e) {
            log.error(e.getMessage(), new Throwable(e));
            return false;
        }
        return true;
    }

    /**
     * 日程发送邮件
     *
     * @param receiver 接收者
     * @param title    日程标题
     * @param content  内容
     * @return
     */
    @Override
    public boolean sendSchedule(String receiver, String title, String content) {
        Session session = initSession();
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(Constant.email_address_1);
            message.setRecipients(Message.RecipientType.TO, receiver);//设置邮件收信人
            message.setSubject(title);
            message.setContent(content, "text/html;charset=utf-8");
            Transport transport = session.getTransport();
            transport.send(message);
        } catch (Exception e) {
            log.error(e.getMessage(), new Throwable(e));
            return false;
        }
        return true;

    }


    /**
     * 管理员群发邮件
     *
     * @param receiverList
     * @param subject
     * @param content
     * @return
     */
    @Override
    public boolean sendEmailsByAdmin(List<String> receiverList, String subject, String content) {

        Session session = initSession();
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(Constant.email_address_1);
            message.setSubject(subject);
            message.setContent(content, "text/html;charset=utf-8");
            Transport transport = session.getTransport();

            if (CollectionUtils.isNotEmpty(receiverList)) {
                for (String emailAddress : receiverList) {
                    message.setRecipients(Message.RecipientType.TO, emailAddress);//设置邮件收信人
                    transport.send(message);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), new Throwable(e));
            return false;
        }
        return true;
    }

    /**
     * 发送意见反馈
     *
     * @param receiver
     * @param subject
     * @param content
     * @return
     */
    @Override
    public boolean sendFeedback(String receiver, String subject, String content) {

        Session session = initSession();
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(Constant.email_address_1);
            message.setRecipients(Message.RecipientType.TO, receiver);//设置邮件收信人
            message.setSubject(subject);
            message.setContent(content, "text/html;charset=utf-8");
            Transport transport = session.getTransport();
            transport.send(message);
        } catch (Exception e) {
            log.error(e.getMessage(), new Throwable(e));
            return false;
        }
        return true;
    }


}
