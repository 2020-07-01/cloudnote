package com.interceptorService;

import com.entity.User;
import com.fasterxml.jackson.databind.util.TypeKey;

/**
 * @author :qiang
 * @date :2019/12/9 下午8:15
 * @description : 处理token的接口方法
 * @other :
 */
public interface TokenService {

    //根据请求token查询token的信息
    TokenEntity queryByToken(String token);

    //根据用户id创建token
    TokenEntity createToken(User user);

    //设置Token过期
    void expireToken(Integer userId);


}
