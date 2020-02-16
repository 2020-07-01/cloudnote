package com.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.interceptorService.TokenUtil;

/**
 * @author :qiang
 * @date :2019/12/3 下午11:00
 * @description :
 * @other :
 */

@Controller
@RequestMapping(value = "/")
public class DefaultController {

    @Autowired
    TokenUtil tokenUtil;

    @RequestMapping(value = "register")
    public String toEmailRegister() {
        return "emailRegister";
    }

    @RequestMapping(value = "phoneRegister")
    public String toPhoneRegister() {
        return "phoneRegister";
    }

    /**
     * 用户名登录
     * 
     * @return
     */
    @RequestMapping(value = "login")
    public String toEmailLogin() {
        return "login";
    }

    /**
     * 动态验证登录
     * 
     * @return
     */
    @RequestMapping(value = "to_dynamicLogin")
    public String toPhoneLogin() {
        return "dynamicLogin";
    }

    @RequestMapping("to_index")
    public String toIndex(HttpServletRequest request) {
        String token = request.getParameter("token");
        // 对token进行验证
        tokenUtil.verifyToken(token);
        return "homePage";
    }

    @RequestMapping(value = "to_editPassword")
    public String toEditPassword() {

        return "editPassword";
    }

    @RequestMapping(value = "to_task")
    public String toTask() {
        return "schedule";
    }

    @RequestMapping(value = "to_timing_task")
    public String toTimingTask() {
        return "timingTask";
    }

    @RequestMapping(value = "/to_resource_image")
    public String toResource() {
        return "resource-image";
    }

}
