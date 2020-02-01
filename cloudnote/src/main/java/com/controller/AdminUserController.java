package com.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.entity.Condition;
import com.entity.User;
import com.interceptorService.TokenUtil;
import com.Util.Json;
import com.Util.Result;
import com.mailService.MailServiceImpl;
import com.service.serviceImpl.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author :qiang
 * @date :2019/12/4 下午11:14
 * @description :
 * @other :
 */
@RequestMapping(value = "/admin")
@RestController
public class AdminUserController {

    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminUserController.class);

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private MailServiceImpl mailService;

    @Autowired
    TokenUtil tokenService;

    /**
     * 邮箱注册
     *
     * @param jsonParam
     * @return
     */
    @RequestMapping(value = "/register")
    public void emailRegister(@RequestBody String jsonParam, HttpServletRequest request, HttpServletResponse response) {

        JSONObject jsonObject;
        try {
            jsonObject = JSON.parseObject(jsonParam);
            User user = new User();
            user.setUserName(jsonObject.getString("userName"));
            user.setEmail(jsonObject.getString("email"));
            user.setUserPassword(jsonObject.getString("userPassword"));
            Map result = userService.insert(user);
            if (result.get("true") != null) {
                Json.toJson(new Result(true,(String)result.get("true")), response);
            } else {
                Json.toJson(new Result(false,(String)result.get("false")), response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Json.toJson(new Result(false,"出现异常"), response);
        }
    }

    /**
     * 用户名登录
     *
     * @param jsonParam
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/login")
    public void emailLogin(@RequestBody String jsonParam, HttpServletRequest request, HttpServletResponse response) {
        JSONObject jsonObject;
        try {
            jsonObject = JSON.parseObject(jsonParam);
            Condition condition = new Condition();
            condition.setName(jsonObject.getString("name"));
            condition.setPassword(jsonObject.getString("userPassword"));
            Map result = userService.findUser(condition);
            if (result.get("false") != null) {
                Json.toJson(new Result(false, (String) result.get("false")), response);
            } else {
                String token = tokenService.createToken(String.valueOf(result.get("userId")));
                HashMap data = new HashMap();
                data.put("token", token);
                Json.toJson(new Result(true, (String) result.get("true"),data), response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Json.toJson(new Result(false, "出现异常"), response);
        }
    }

    /**
     * 动态登录
     * 手机号/邮箱登录/验证码进行登录
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
     * 用户名/邮箱地址/手机号/密码登录
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
    public void securityCode(@RequestBody String jsonParam, HttpServletRequest request, HttpServletResponse response) {
        JSONObject jsonObject;
        jsonObject = JSON.parseObject(jsonParam);
        String emailAddress = jsonObject.getString("emailAddress");
        if (emailAddress == null){
            Json.toJson(new Result(false,"邮箱不能为空!"), response);
        }
        Map result = userService.findUerByEmail(emailAddress);
        if(result.get("true") != null){
            String sender = "2422321558@qq.com";
            mailService.sendSecurityCode(sender, emailAddress, "验证码", "000");
            Json.toJson(new Result(true,"发送成功!"),response);
        }else {
            Json.toJson(new Result(false, "邮箱已注册!"), response);
        }
    }

    /**
     * 手机号进行注册
     * @param jsonParam
     * @param request
     * @param response
     */
    @RequestMapping(value = "/phone")
    public void phoneRegister(@RequestBody String jsonParam, HttpServletRequest request, HttpServletResponse response) {
        Result result = null;
        JSONObject jsonObject;
        try {
            jsonObject = JSON.parseObject(jsonParam);
            User user = new User();
            user.setUserName(jsonObject.getString("userName"));
            user.setPhone(jsonObject.getString("phone"));
            user.setUserPassword(jsonObject.getString("userPassword"));

        } catch (Exception e) {
            result = new Result(false, "请检查您的网络是否稳定!");
        }
        try {
            Json.toJson(result, response);
        } catch (Exception e) {
        }
    }


}
