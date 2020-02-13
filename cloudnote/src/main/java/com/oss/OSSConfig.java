package com.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @program: ConstantProperties
 * @description:
 * @create: 2020-02-03 22:21
 **/
@Component
public class OSSConfig {

    @Value("${aliyun.oss.endpoint}")
    private String aliyun_oss_endpoint;

    @Value("${aliyun.oss.keyid}")
    private String aliyun_oss_keyid;

    @Value("${aliyun.oss.keysecret}")
    private String aliyun_oss_keysecret;

    @Value("${aliyun.oss.bucketname}")
    private String aliyun_oss_bucketname;

    @Bean
    public OSS oSSClient() {
        return new OSSClientBuilder().build(aliyun_oss_endpoint, aliyun_oss_keyid, aliyun_oss_keysecret);
    }

}
