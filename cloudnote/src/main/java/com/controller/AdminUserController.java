package com.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.entity.User;
import com.sendEmailService.MailServiceImpl;
import com.service.serviceImpl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author :qiang
 * @date :2019/12/4 下午11:14
 * @description :
 * @other :
 */
@RequestMapping(value = "/admin")
@RestController
public class AdminUserController {


    @Autowired
    private UserServiceImpl adminUserService;

    @Autowired
    private MailServiceImpl mailService;

    /**
     * 邮箱注册
     *
     * @param jsonParam
     * @return
     */
    @RequestMapping(value = "/email_register")
    public String emailRegister(@RequestBody String jsonParam, HttpServletRequest request, HttpServletResponse response) {
        String result;
        JSONObject jsonObject;
        try {
            jsonObject = JSON.parseObject(jsonParam);
            User user = new User();
            user.setUserName(jsonObject.getString("userName"));
            user.setEmail(jsonObject.getString("email"));
            user.setUserPassword(jsonObject.getString("userPassword"));

            //int row = adminUserService.insert(user);

            int row = 0;
            if (row == 0) {
                result = "用户" + jsonObject.getString("userName") + "注册成功!";
            } else {
                result = "注册失败!";
            }
        } catch (Exception e) {
            result = "出现异常!";
        }
        System.out.println(result);
        return result;
    }


    @RequestMapping(value = "/email_login")
    public String emailLogin(@RequestBody String jsonParam, HttpServletRequest request, HttpServletResponse response) {
        String result;
        JSONObject jsonObject;
        try {
            jsonObject = JSON.parseObject(jsonParam);

            result = "登录成功!";

        } catch (Exception e) {
            result = "登录失败!";
        }
        return result;
    }


    @RequestMapping(value = "/security_code")
    public String securityCode(@RequestBody String jsonParam, HttpServletRequest request, HttpServletResponse response) {

        String result;
        JSONObject jsonObject;
        try {
            jsonObject = JSON.parseObject(jsonParam);
            //在service层对邮件地址进行判断
            String emailAddress = jsonObject.getString("emailAddress");
            //此处进行邮件的发送
            String sender = "2422321558@qq.com";
            mailService.sendSecurityCode(sender, "2422321558@qq.com", "验证码", "000");

            result = "登录成功!";

        } catch (Exception e) {
            result = "登录失败!";
        }
        return result;
    }

}
