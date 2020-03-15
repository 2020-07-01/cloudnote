package com.Util;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @program: MD5Util
 * @description: md5加密工具类
 * @create: 2020-01-16 10:35
 **/
public class MD5Utils {


    /**
     * 对用户密码进行加密
     *
     * @param str
     * @return 返回固定长度的加密字符串，
     */
    public static String getMD5String(String str) {
        if (str == null) {
            throw new NullPointerException();
        }
        return DigestUtils.md5Hex(str);
    }

    public static void main(String[] args) {
        System.out.println(MD5Utils.getMD5String("123发撒法发是否是发送发是否是发是否都是发第三方撒发撒发送发是否是非锁定发士大夫的撒发第三方都是发第三方都是"));
    }
}
