package com.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author :qiang
 * @date :2019/12/9 下午8:06
 * @description :拦截器配置类
 * @other :
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    LoginInterceptor loginInterceptor;

    /**
     * 拦截所有请求
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        //不拦截的路径
        List<String> list = new ArrayList<>();
        list.add("/static/**");//静态资源
        list.add("/js/*");//静态资源
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**").excludePathPatterns(list);
    }
}
