package com.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.hibernate.validator.constraints.pl.REGON;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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


    /**
     * 邮箱注册
     * @param jsonParam
     * @return
     */
    @RequestMapping(value = "/email_register")
    public String emailRegister(@RequestBody String jsonParam) {

        String result = null;


        JSONObject jsonObject = (JSONObject) JSON.parse(jsonParam);
        System.out.println(jsonObject.get("name"));

        try {
            result = "用户注册成功!";
        } catch (Exception e) {
            result = "用户注册失败!";
        }
        return result;
    }


    @RequestMapping(value = "/secrityCode")
    public String securityCode(){
        String code = "123456";

        return code;
    }

}
