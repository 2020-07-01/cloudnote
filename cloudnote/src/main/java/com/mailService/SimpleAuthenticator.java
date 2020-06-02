package com.mailService;


import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * @Description 密码验证
 * @Author yq
 * @Date 2020/5/29 15:40
 */
public class SimpleAuthenticator extends Authenticator {

    private String username;
    private String password;


    public SimpleAuthenticator(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        // TODO Auto-generated method stub
        return new PasswordAuthentication(username, password);
    }
}
