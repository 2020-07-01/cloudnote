package com.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.entity.User;
import com.service.serviceImpl.AdminUserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    AdminUserServiceImpl adminUserService;

    /**
     * 邮箱注册
     *
     * @param jsonParam
     * @return
     */
    @RequestMapping(value = "/email_register")
    public String emailRegister(@RequestBody String jsonParam) {
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

        return result;
    }


    @RequestMapping(value = "/email_login")
    public String emailLogin(@RequestBody String jsonParam) {
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


    @RequestMapping(value = "/secrityCode")
    public String securityCode() {
        String code = "123456";
        return code;
    }

}
