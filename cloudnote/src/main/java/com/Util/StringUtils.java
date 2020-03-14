package com.Util;

/**
 * @program: StringUtils
 * @description: 字符串工具
 * @create: 2020-03-14 15:01
 **/
public class StringUtils {


    /**
     * @param string
     * @return
     */
    public static boolean isNotEmpty(String string) {
        if (string.trim() == null || string.trim().length() == 0) {
            return false;
        }
        return true;
    }

}
