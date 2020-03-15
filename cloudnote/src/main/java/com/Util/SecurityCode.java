package com.Util;

/**
 * @program: SecurityCode
 * @description: 生成验证码
 * @create: 2020-03-15 20:24
 **/
public class SecurityCode {

    public static String getSecurityCode() {

        Double code = Math.random() * 1000000;
        return code.toString().substring(0, 6);
    }
}
