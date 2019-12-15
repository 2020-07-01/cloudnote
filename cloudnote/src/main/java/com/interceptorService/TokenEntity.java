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
    private String userName;
    private String userPassword;


    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
}
