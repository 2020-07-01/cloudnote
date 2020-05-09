package com.Util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cache.CacheService;
import com.entity.Account;
import com.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
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
@Slf4j
@Component
public class TokenUtils {


    @Autowired
    CacheService cacheService;

    // 私钥
    private static final String TOKEN_SECRET = "privateKey";

    /**
     * 创建token Algorithm.HMAC256
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
                .withIssuer("yq")
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
            if (token == null) {
                return false;
            }
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).withIssuer("yq").build();
            DecodedJWT jwt = verifier.verify(token);
            String accountId = jwt.getAudience().get(0);
            if (accountId == null) {
                log.info("token验证失败:token中不存在账户ID");
                return false;
            }
            Map map = cacheService.getValue(accountId);
            if (map == null) {
                return false;
            }
            if (!map.get("accountId").equals(accountId)) {
                return false;
            }
        } catch (Exception e) {
            e.toString();
        }
        return true;
    }

    /**
     * 解析出token中的accountId
     *
     * @param token
     * @return
     */
    public String getAccountIdByToken(String token) {
        String result;
        try {
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).withIssuer("yq").build();
            DecodedJWT jwt = verifier.verify(token);// 进行验证
            String accountId = jwt.getAudience().get(0);
            if (accountId != null) {
                result = accountId;
            } else {
                result = null;
            }
        } catch (Exception e) {
            result = null;
            log.error(e.getMessage(), new Throwable(e));
        }
        return result;
    }
}
