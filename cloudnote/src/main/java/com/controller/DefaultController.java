package com.controller;

import org.hibernate.validator.constraints.pl.REGON;
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


    @RequestMapping(value = "register")
    public String toEmailRegister() {

        return "emailRegister";
    }


    @RequestMapping(value = "to_emailLogin")
    public String toEmailLoginHtml(){
        return "emailLogin";
    }

    @RequestMapping("to_index")
    public String toIndex() {
        return "index";
    }

    @RequestMapping(value = "to_editPassword")
    public String toEditPassword(){

        return "editPassword";
    }

    @RequestMapping(value = "to_basicInformation")
    public String toInformation(){
        return "basicInformation";
    }

}
