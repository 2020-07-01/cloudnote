package com.interceptor;

import com.Util.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @program: LoginInterceptor
 * @description: 创建拦截器
 * @create: 2020-03-15 14:18
 **/
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    TokenUtils tokenUtil;

    /**
     * 进入controller方法之前进行拦截
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //在此处进行token的验证

        return true;
    }

}
