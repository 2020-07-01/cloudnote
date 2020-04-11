package com.controller;

import com.Util.TokenUtils;
import com.interceptor.PassToken;
import com.interceptor.UserLoginToken;
import com.job.ScheduleJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    @Autowired
    ScheduleJob scheduleJob;

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
    @PassToken
    @RequestMapping(value = "login")
    public String toEmailLogin() {
        return "login";
    }

    @RequestMapping("/to_note_page")
    @UserLoginToken
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

    @RequestMapping(value = "/image_page")
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

    @RequestMapping(value = "/file_page")
    public String toFilePage() {
        return "filePage";
    }

    @RequestMapping(value = "/refresh")
    @ResponseBody
    public String jobRefresh(){
        scheduleJob.scheduleRemind();
        return "SUCCESS";
    }

    @RequestMapping(value = "/rest_password")
    public String toRestPassword(){
        return "resetPasswordPage";
    }

}
