package com.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
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



    @RequestMapping(value = "/email_register")
    public String emailRegister(@RequestBody String jsonString) {
        String result = null;


        try {


            result = "用户注册成功!";
        } catch (Exception e) {
            result = "用户注册失败!";
        }
        return result;
    }

}
