package com.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.entity.User;
import com.interceptorService.TokenEntity;
import com.interceptorService.TokenServiceImpl;
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
    private UserServiceImpl userService;

    @Autowired
    private MailServiceImpl mailService;

    @Autowired
    TokenServiceImpl tokenService;

    /**
     * 邮箱注册
     *
     * @param jsonParam
     * @return
     */
    @RequestMapping(value = "/email_register")
    public Object emailRegister(@RequestBody String jsonParam, HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        JSONObject jsonObject;
        try {
            jsonObject = JSON.parseObject(jsonParam);
            User user = new User();
            user.setUserName(jsonObject.getString("userName"));
            user.setEmail(jsonObject.getString("email"));
            user.setUserPassword(jsonObject.getString("userPassword"));
            user.setCreateTime("now()");
            int row = userService.insert(user);
            if (row == 1) {
                result.put("code", "0");
                result.put("msg", "注册成功!");
                return result;
            } else {
                result.put("msg", "1");
                result.put("msg", "注册失败!");
                return result;
            }
        } catch (Exception e) {
            result.put("msg","2");
            result.put("msg", "出现异常!");
        }
        return result;
    }


    /**
     * 邮箱登录
     *
     * @param jsonParam
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/email_login")
    public Object emailLogin(@RequestBody String jsonParam, HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        JSONObject jsonObject;
        try {
            jsonObject = JSON.parseObject(jsonParam);

            User user = userService.findUser(new User(jsonObject.getString("email"), jsonObject.getString("userPassword")));
            if (user == null) {
                result.put("msg", "登录失败，用户不存在!");
                return result;
            } else {
                if (user.getUserPassword() != jsonObject.getString("userPassword") || user.getEmail() != jsonObject.getString("email")) {
                    result.put("msg", "用户名或者密码错误!");
                    return result;
                } else {
                    String token = tokenService.createToken(user);
                    result.put("token", token);
                    return result;
                }
            }

        } catch (Exception e) {
            result.put("msg", "登录异常!");
        }
        return result;
    }


    /**
     * 手机号登录
     *
     * @param jsonParam
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/phone_login")
    public String phoneLogin(@RequestBody String jsonParam, HttpServletRequest request, HttpServletResponse response) {
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


    /**
     * 用户名登录
     *
     * @param jsonParam
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/username_login")
    public String userNameLogin(@RequestBody String jsonParam, HttpServletRequest request, HttpServletResponse response) {
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

    /**
     * 发送验证码
     *
     * @param jsonParam
     * @param request
     * @param response
     * @return
     */
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
