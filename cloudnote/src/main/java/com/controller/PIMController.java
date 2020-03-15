package com.controller;

import com.Util.Json;
import com.Util.Result;
import com.Util.TokenUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.entity.PIM;
import com.service.serviceImpl.PIMServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
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
public class PIMController {

    @Autowired
    PIMServiceImpl pimService;

    @Autowired
    TokenUtils tokenUtil;

    @RequestMapping(value = "/updatePIM.json")
    public void updatePIM(@RequestBody String jsonString, HttpServletResponse response, HttpServletRequest request) {

        String token = request.getHeader("token");
        Integer accountId = tokenUtil.getAccountIdByToken(token);

        JSONObject jsonObject = JSON.parseObject(jsonString);
        String sex = jsonObject.getString("sex");
        JSONObject field = jsonObject.getJSONObject("field");

        String headImageUrl = field.getString("headImageUrl");
        String province = field.getString("province");
        String city = field.getString("city");
        String remark = field.getString("remark");
        //进行验证

        PIM pim = new PIM();
        pim.setAccountId(accountId);
        pim.setSex(sex);
        pim.setProvince(province);
        pim.setCity(city);
        pim.setHeadImageUrl(headImageUrl);
        pim.setRemark(remark);
        if (pimService.updatePIM(pim)) {
            Json.toJson(new Result(true, "修改成功!"), response);
        } else {
            Json.toJson(new Result(false, "修改失败"), response);
        }

    }
}
