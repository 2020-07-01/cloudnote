package com.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
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
//@Configuration
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
        list.add("/register");
        list.add("/phoneRegister");
        list.add("/login");
        list.add("/to_dynamicLogin");
        list.add("/findPassword");
        list.add("/static/**");

        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**").excludePathPatterns(list);
    }
}
