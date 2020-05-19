package com.baidu;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.baidu.aip.contentcensor.AipContentCensor;

import lombok.extern.slf4j.Slf4j;

import org.checkerframework.checker.units.qual.A;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @Description 百度内容审核配置
 * @Author yq
 * @Date 2020/4/20 22:57
 */
@Component
public class BaiDuConfig {

    public static final String APP_ID = "19301381";
    public static final String API_KEY = "G10htS19elFlF65EHmGbci5L";
    public static final String SECRET_KEY = "RRng6jhqKY3oFLa7z0zNi1kS1mb642gb";

    @Bean
    public AipContentCensor baiDuClient() {
        return new AipContentCensor(APP_ID, API_KEY, SECRET_KEY);
    }
}


