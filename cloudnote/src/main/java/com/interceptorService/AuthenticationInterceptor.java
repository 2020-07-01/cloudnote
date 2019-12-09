package com.interceptorService;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.entity.User;
import com.service.serviceImpl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author :qiang
 * @date :2019/12/9 下午8:05
 * @description :配置token验证拦截器
 * @other :
 */
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Autowired
    UserServiceImpl userService;

    @Autowired
    TokenServiceImpl tokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");//从http头中获取token


        //如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod))
            return true;

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        //检查是否有密码注释，有则跳过
        if (method.isAnnotationPresent(PassToken.class)) {
            PassToken passToken = method.getAnnotation(PassToken.class);
            if (passToken.required())
                return true;
        }
        //检查有没有需要用户权限的注解
        UserLoginToken userLoginToken = method.getAnnotation(UserLoginToken.class);
        if (userLoginToken.required()) {
            //执行认证
            if (token == null)
                throw new RuntimeException("无Token，请重新登录!");

            Integer userId;

            try {
                userId = Integer.parseInt(JWT.decode(token).getAudience().get(0));
            } catch (Exception e) {
                throw new RuntimeException("401");
            }
            User user = userService.findUserById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在，请重新登录!");
            }

            //验证token
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getUserPassword())).build();
            try {
                jwtVerifier.verify(token);
            } catch (Exception e) {
                throw new RuntimeException("401");
            }

        }

        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
