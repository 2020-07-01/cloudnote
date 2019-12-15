package com.interceptorService;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.entity.User;
import org.springframework.stereotype.Component;

/**
 * @author :qiang
 * @date :2019/12/9 下午8:22
 * @description :
 * @other :
 */
@Component
public class TokenServiceImpl {


    /**
     * 创建token
     * Algorithm.HMAC256(
     *
     * @param user
     * @return
     */
    public String createToken(User user) {
        String token = JWT.create().withAudience(String.valueOf(user.getUserId()))
                .sign(Algorithm.HMAC256(user.getUserPassword()));
        //此处先不设置过期时间
        return token;
    }

    //设置token过期

    public void expireToken(Integer userId) {

    }
}
