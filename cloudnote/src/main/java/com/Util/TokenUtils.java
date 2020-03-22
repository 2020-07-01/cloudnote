package com.Util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.entity.Account;
import com.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author :qiang
 * @date :2019/12/9 下午8:22
 * @description :
 * @other :
 */
@Component
public class TokenUtils {

    @Autowired
    AccountService userService;

    // 私钥
    private static final String TOKEN_SECRET = "privateKey";

    /**
     * 创建token Algorithm.HMAC256(
     */
    public String createToken(String accountId) {
        // 通过私钥来生成签名
        Algorithm ALGORITHM = Algorithm.HMAC256(TOKEN_SECRET);
        // 设置头部信息
        Map<String, Object> header = new HashMap<>();
        header.put("Type", "Jwt");
        header.put("alg", "HS256");
        String token = JWT.create()
                // 添加头部信息
                .withHeader(header)
                // 设置签发者
                .withIssuer("admin")
                // 设置用户信息
                .withAudience(accountId)
                // 设置创建时间
                .withIssuedAt(new Date(System.currentTimeMillis()))
                // 设置过期时间一天
                .withExpiresAt(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
                // 设置签名
                .sign(ALGORITHM);
        return token;
    }

    /**
     * 校验token是否正确
     *
     * @param token
     * @return
     */
    public boolean verifyToken(String token) {

        try {
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).withIssuer("admin").build();
            DecodedJWT jwt = verifier.verify(token);
            String accountId = jwt.getAudience().get(0);
            if (accountId == null) {
                throw new RuntimeException("token校验失败,原因：token中不存在accountId");
            }
            Account user = userService.findUserById(Integer.parseInt(accountId));
            if (user == null) {
                throw new RuntimeException("token校验失败，原因：用户不存在");
            }
            return true;
        } catch (Exception e) {
            e.toString();
        }
        return false;
    }

    /**
     * 解析出token中的accountId
     *
     * @param token
     * @return
     */
    public Integer getAccountIdByToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).withIssuer("admin").build();
            DecodedJWT jwt = verifier.verify(token);// 进行验证
            String accountId = jwt.getAudience().get(0);
            if (accountId != null) {
                return Integer.decode(accountId);
            } else {
                throw new RuntimeException("token中不存在accountId");
            }
        } catch (Exception e) {
            throw new RuntimeException("token校验抛出异常");
        }
    }
}
