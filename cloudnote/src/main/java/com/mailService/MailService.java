package com.mailService;

/**
 * @author :qiang
 * @date :2019/11/30 下午11:26
 * @description :
 * @other :
 */

public interface MailService {

    //发送验证码
    boolean sendSecurityCode(String receiver,String context);

    //发送日程提醒
    boolean sendSchedule(String receiver, String title, String content);

    //群发邮件

}
