package com.sendEmailService;

/**
 * @author :qiang
 * @date :2019/11/30 下午11:26
 * @description :
 * @other :
 */


public interface MailService {

    //发送验证码
    String sendSecurityCode(String sender, String receiver, String title, String context);

    //发送thymleaf模板邮件
    String sendHtml(String sender, String receiver, String subject);

}
