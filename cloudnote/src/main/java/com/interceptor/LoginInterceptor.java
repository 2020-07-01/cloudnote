package com.interceptor;

import com.Util.TokenUtils;
import com.alibaba.fastjson.JSON;

import com.cache.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @program: LoginInterceptor
 * @description: 创建拦截器
 * @create: 2020-03-15 14:18
 **/
@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    TokenUtils tokenUtil;

    @Autowired
    CacheService cacheService;

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
        log.info("请求url：" + request.getRequestURL());
        log.info("请求参数MAP：" + JSON.toJSONString(request.getParameterMap()));

        String token = request.getHeader("token");
        if (token == null) {
            token = request.getParameter("token");
        }
        Method method = ((HandlerMethod) handler).getMethod();
        //检查是否有passtoken注释，有则跳过认证
        if (method.isAnnotationPresent(PassToken.class)) {
            PassToken passToken = method.getAnnotation(PassToken.class);
            if (passToken.required()) {
                return true;
            }
        }
        //检查有没有需要用户权限的注解
        if (method.isAnnotationPresent(UserLoginToken.class)) {
            UserLoginToken userLoginToken = method.getAnnotation(UserLoginToken.class);
            if (userLoginToken.required()) {
                if (token == null) {
                    log.info("无token请重新登录!");
                    response.sendRedirect("/login");
                    return false;
                }
                //获取token中的accountId
                Integer accountId = tokenUtil.getAccountIdByToken(token);
                Map<String, String> cacheMap = cacheService.getValue(accountId.toString());
                if (cacheMap != null) {
                    String cacheAccountId = cacheMap.get("accountId");
                    if (cacheAccountId.equals(accountId.toString())) {
                        log.info("token验证成功!");
                        return true;
                    }
                }
                response.sendRedirect("/login");
                log.info("token验证失败，请重新登录!");
                return false;
            }
        }
        return false;
    }
}
