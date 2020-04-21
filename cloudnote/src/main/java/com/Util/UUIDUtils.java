package com.Util;

import java.util.UUID;

/**
 * @Description 获取唯一主键
 * @Author yq
 * @Date 2020/4/21 17:01
 */
public class UUIDUtils {

    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
