package com.controller;

import com.Util.Json;
import com.Util.Result;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @program: PimContorller
 * @description:
 * @create: 2020-03-14 22:39
 **/
@RequestMapping(value = "/PIM")
@RestController
public class PIMContorller {

    @RequestMapping(value = "/updatePIM.json")
    public void updatePIM(@RequestBody String jsonString, HttpServletResponse response, HttpServletRequest request) {

        JSONObject jsonObject = JSON.parseObject(jsonString);
        String sex = jsonObject.getString("sex");
        JSONObject field = jsonObject.getJSONObject("field");

        String headImageUrl = field.getString("headImageUrl");
        String province = field.getString("province");
        String city = field.getString("city");
        String remark = field.getString("remark");
        //进行验证


        Json.toJson(new Result(false, "出现异常"), response);


    }
}
