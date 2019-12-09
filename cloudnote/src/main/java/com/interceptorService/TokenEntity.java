package com.interceptorService;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author :qiang
 * @date :2019/12/9 下午8:09
 * @other : Token实体类
 */
public class TokenEntity implements Serializable {

    private Integer userId;
    private String token;
    private LocalDateTime expireTime;
    private LocalDateTime updateTime;

    public TokenEntity() {
    }

    public TokenEntity(Integer userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    public TokenEntity(Integer userId, String token, LocalDateTime expireTime, LocalDateTime updateTime) {
        this.userId = userId;
        this.token = token;
        this.expireTime = expireTime;
        this.updateTime = updateTime;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String taoken) {
        this.token = token;
    }

    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
