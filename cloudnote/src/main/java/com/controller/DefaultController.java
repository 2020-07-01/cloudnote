package com.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author :qiang
 * @date :2019/12/3 下午11:00
 * @description :
 * @other :
 */

@Controller
@RequestMapping(value = "/")
public class DefaultController {

    @RequestMapping(value = "login")
    public String toEmailRegister(){

        return "emailRegister";
    }

}
