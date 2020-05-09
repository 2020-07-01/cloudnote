package com.controller;

import com.Util.TokenUtils;
import com.interceptor.PassToken;
import com.interceptor.UserLoginToken;
import com.job.ScheduleJob;
import com.oss.OSSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @Autowired
    OSSUtil ossUtil;

    @RequestMapping(value = "register")
    @PassToken
    public String toEmailRegister() {
        return "emailRegister";
    }

    @RequestMapping(value = "phoneRegister")
    public String toPhoneRegister() {
        return "phoneRegister";
    }

    @PassToken
    @RequestMapping(value = "/login")
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

    @UserLoginToken
    @RequestMapping(value = "schedule_page")
    public String toTask() {
        return "schedulePage";
    }

    @RequestMapping(value = "/image_page")
    @UserLoginToken
    public String toResource() {
        return "imagePage";
    }

    @UserLoginToken
    @RequestMapping(value = "information")
    public String toInformation() {
        return "information";
    }

    @UserLoginToken
    @RequestMapping(value = "/admin")
    public String toAdmin() {
        return "admin";
    }

    @PassToken
    @RequestMapping(value = "/findPassword")
    public String toFindPassword() {
        return "findPassword";
    }

    @UserLoginToken
    @RequestMapping(value = "/resetPassword")
    public String toResetPassword() {
        return "resetPasswordPage";
    }

    @UserLoginToken
    @RequestMapping(value = "/file_page")
    public String toFilePage() {
        return "filePage";
    }

    @RequestMapping(value = "/refresh")
    @ResponseBody
    public String jobRefresh() {
        scheduleJob.scheduleRemind();
        return "SUCCESS";
    }

    @UserLoginToken
    @RequestMapping(value = "/recycle_bin")
    public String toRecycleBin() {
        return "recycleBin";
    }

    @PassToken
    @RequestMapping(value = "/test")
    @ResponseBody
    public String test1() {
        ossUtil.getSize("59051fa2d1644f44a781ad1d9dacd67b");
        return "SUCCESS";
    }

}
