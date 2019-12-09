package com.interceptorService;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author :qiang
 * @date :2019/12/9 下午8:06
 * @description :注册拦截器
 * @other :
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    /**
     * 拦截所有请求
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //不拦截的路径
        List<String> list = new ArrayList<>();
        list.add("/admin/email_register");//邮箱注册
        list.add("/register");//进入邮箱注册页面
        list.add("/to_emailLogin");//进入邮箱登录页面

        registry.addInterceptor(authenticationInterceptor())
                .addPathPatterns("/**").excludePathPatterns(list);
    }


    public AuthenticationInterceptor authenticationInterceptor() {
        return new AuthenticationInterceptor();
    }

}
