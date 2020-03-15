package com.controller;

import com.Util.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

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
    TokenUtils tokenUtil;

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
        return "notePage";
    }

    @RequestMapping("/to_note_page")
    public String toNotePage() {
        return "notePage";
    }

    @RequestMapping(value = "to_editPassword")
    public String toEditPassword() {
        return "editPassword";
    }

    @RequestMapping(value = "to_schedule_page")
    public String toTask() {
        return "schedulePage";
    }

    @RequestMapping(value = "to_timing_task")
    public String toTimingTask() {
        return "timingTask";
    }

    @RequestMapping(value = "/to_image_page")
    public String toResource() {
        return "imagePage";
    }

    @RequestMapping(value = "to_userInformation")
    public String touserInformation() {
        return "userInformation";
    }

    @RequestMapping(value = "information")
    public String toInformation() {
        return "information";
    }

    @RequestMapping(value = "/admin")
    public String toAdmin() {
        return "admin";
    }

    @RequestMapping(value = "/findPassword")
    public String toFindPassword() {
        return "findPassword";
    }

    @RequestMapping(value = "/resetPassword")
    public String toResetPassword() {
        return "resetPassword";
    }

}
