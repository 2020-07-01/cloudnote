package com.controller;

import com.Util.Json;
import com.Util.Result;
import com.Util.SecurityCode;
import com.Util.TokenUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cache.CacheService;
import com.entity.Account;
import com.entity.Condition;
import com.entity.Constant;
import com.interceptor.PassToken;
import com.interceptor.UserLoginToken;
import com.mailService.MailServiceImpl;
import com.service.serviceImpl.AccountServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author :qiang
 * @date :2019/12/4 下午11:14
 * @description :
 * @other :
 */
@Slf4j
@RequestMapping(value = "/account")
@RestController
public class AccountController {

    @Autowired
    private AccountServiceImpl accountService;

    @Autowired
    private MailServiceImpl mailService;

    @Autowired
    TokenUtils tokenService;

    @Autowired
    CacheService cacheService;

    /**
     * 邮箱注册
     * 添加触发器存储accountId 信息
     *
     * @param jsonParam
     * @return
     */
    @RequestMapping(value = "register.json")
    public void emailRegister(@RequestBody String jsonParam, HttpServletRequest request, HttpServletResponse response) {

        String tempSecurityCode = request.getHeader("tempSecurityCode");
        try {
            JSONObject jsonObject = JSON.parseObject(jsonParam);
            String confirmSecurityCode = jsonObject.getString("securityCode");
            if (!tempSecurityCode.equals(confirmSecurityCode)) {
                Json.toJson(new Result(false, "验证码错误"), response);
                return;
            }

            String email = jsonObject.getString("email");
            String accountName = jsonObject.getString("accountName");
            String accountPassword = jsonObject.getString("accountPassword");
            String confirmPassword = jsonObject.getString("confirmPassword");

            if (!confirmPassword.equals(accountPassword)) {
                Json.toJson(new Result(false, "两次输入密码不一致"), response);
            }

            Account account = new Account();
            account.setAccountName(accountName);
            account.setAccountPassword(accountPassword);
            account.setEmail(email);

            Map result = accountService.insert(account);

            if (result.get("true") != null) {
                Json.toJson(new Result(true, (String) result.get("true")), response);
            } else {
                Json.toJson(new Result(false, (String) result.get("false")), response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Json.toJson(new Result(false, "出现异常"), response);
        }
    }

    /**
     * 用户名登录
     * 登陆成功后在缓存中创建私有Map
     *
     * @param jsonParam
     * @param request
     * @param response
     * @return
     */
    @PassToken
    @RequestMapping(value = "login.json")
    public void Login(@RequestBody String jsonParam, HttpServletRequest request, HttpServletResponse response) {
        JSONObject jsonObject;
        Result result = null;
        String accountId = "";
        try {
            jsonObject = JSON.parseObject(jsonParam);
            Account account = new Account(jsonObject.getString("accountName"), jsonObject.getString("accountPassword"));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String lastLoginTime = dateFormat.format(new Date());
            account.setLastLoginTime(lastLoginTime);
            account.setIsOnline("ONLINE");
            account.setLoginCount(1);
            boolean flag = accountService.updateLoginStatus(account);
            if (!flag) {
                //判断是否被锁定
                Condition condition = new Condition();
                condition.setAccountName(jsonObject.getString("accountName"));
                condition.setAccountPassword(jsonObject.getString("accountPassword"));
                List<Account> accountList = accountService.getAccountByCondition(condition);
                if (CollectionUtils.isNotEmpty(accountList)) {
                    if (accountList.get(0).getIsLocked().equals(Constant.LOCK_YES)) {
                        result = new Result(false, "账户被锁，请联系管理员!");
                        log.info("账户：" + accountId + "被锁定!");
                    }
                } else {
                    result = new Result(false, "用户名或密码错误!");
                    log.info("账户：" + accountId + "登录失败!");
                }
            } else {
                //获取accountId生成token
                Condition condition = new Condition();
                condition.setAccountName(jsonObject.getString("accountName"));
                condition.setAccountPassword(jsonObject.getString("accountPassword"));
                accountId = accountService.findAccountId(condition);
                //生成token并存储在缓存中
                String token = tokenService.createToken(accountId);
                HashMap cacheMap = new HashMap();
                cacheMap.put("accountId", accountId);
                cacheService.putValue(accountId, cacheMap);
                HashMap data = new HashMap();
                data.put("token", token);
                if (jsonObject.getString("accountName").equals("admin")) {
                    result = new Result("000", "登录成功!", data);
                } else {
                    result = new Result(true, "登录成功!", data);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("账户：" + accountId + "登录异常!");
        }

        Json.toJson(result, response);
        log.info("账户：" + accountId + "登录成功!");
    }


    /**
     * 注册->发送验证码
     *
     * @param jsonParam
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "send_security_code1.json")
    public void securityCode(@RequestBody String jsonParam, HttpServletRequest request, HttpServletResponse response) {
        JSONObject jsonObject;
        jsonObject = JSON.parseObject(jsonParam);
        String emailAddress = jsonObject.getString("emailAddress");
        if (emailAddress == null || emailAddress.equals("")) {
            Json.toJson(new Result(false, "邮箱不能为空!"), response);
            return;
        }
        //验证邮箱格式
        Map result = accountService.findUerByEmail(emailAddress);
        if (result.get("true") != null) {
            String sender = "2422321558@qq.com";
            //生成验证码
            String securityCode = SecurityCode.getSecurityCode();
            try {
                mailService.sendSecurityCode(sender, emailAddress, "验证码", securityCode);
                HashMap data = new HashMap();
                data.put("securityCode", securityCode);
                Json.toJson(new Result(true, "验证码已发送", data), response);
            } catch (Exception e) {
                Json.toJson(new Result(false, "发送失败!"), response);
            }
        } else {
            Json.toJson(new Result(false, result.get("false").toString()), response);
        }
    }


    /**
     * 查询密码
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "showPassword.json")
    public void findPassword(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("token");
        Integer accountId = tokenService.getAccountIdByToken(token);
        String passpord = accountService.findPasswordByAccountId(accountId);
        Json.toJson(new Result(true, passpord), response);
    }

    /**
     * 修改密码
     *
     * @param jsonString
     * @param request
     * @param response
     */
    @UserLoginToken
    @RequestMapping(value = "/update_password.json")
    public void updatePassword(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {
        JSONObject jsonObject = JSON.parseObject(jsonString);
        String token = request.getHeader("token");

        Integer accountId = tokenService.getAccountIdByToken(token);

        String currentPassword = jsonObject.getString("currentPassword");
        String accountPassword = jsonObject.getString("accountPassword");
        String confirmPassword = jsonObject.getString("confirmPassword");

        //获取旧的密码
        String oldAccountPassword = accountService.findPasswordByAccountId(accountId);
        if (!oldAccountPassword.equals(currentPassword)) {
            Json.toJson(new Result(false, "密码错误!"), response);
            return;
        }
        //判断密码是否一致
        if (!accountPassword.equals(confirmPassword)) {
            Json.toJson(new Result(false, "密码不一致!"), response);
            return;
        }
        //判断密码是否重复
        if (oldAccountPassword.equals(accountPassword)) {
            Json.toJson(new Result(false, "新密码不能与原来的密码一致!"), response);
            return;
        }
        Account account = new Account();
        account.setAccountId(accountId);
        account.setAccountPassword(accountPassword);
        accountService.updateAccount(account);
        Json.toJson(new Result(true, "修改成功!"), response);
    }

    /**
     * 忘记密码->发送验证码
     */
    @RequestMapping(value = "/send_security_code.json")
    public void getSecurityCode(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {

        JSONObject jsonObject = JSON.parseObject(jsonString);
        String email = jsonObject.getString("email");

        if (email == null || email.equals("")) {
            Json.toJson(new Result(false, "邮箱不能为空!"), response);
            return;
        }
        Map result = accountService.findUerByEmail(email);
        if (result.get("true") != null) {
            Json.toJson(new Result(false, "此邮箱不存在!"), response);
        } else {
            try {
                String sender = "2422321558@qq.com";
                //生成验证码
                String securityCode = SecurityCode.getSecurityCode();
                mailService.sendSecurityCode(sender, email, "验证码", securityCode);
                HashMap data = new HashMap();
                data.put("securityCode", securityCode);
                data.put("email", email);
                Json.toJson(new Result(true, "验证码已发送，请查收!", data), response);
            } catch (Exception e) {
                Json.toJson(new Result(false, "发送失败!"), response);
            }
        }
    }


    /**
     * 忘记密码->找回密码时验证已发送的验证码
     *
     * @param jsonString
     * @param request
     * @param response
     */
    @RequestMapping(value = "/check_security_code.json")
    public void checkSecurityCode(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {

        JSONObject jsonObject = JSON.parseObject(jsonString);
        String firstSecurityCode = jsonObject.getString("firstSecurityCode");
        String secondSecurityCode = jsonObject.getString("secondSecurityCode");

        if (firstSecurityCode.equals(secondSecurityCode)) {
            Json.toJson(new Result(true, "验证通过!"), response);
        } else {
            Json.toJson(new Result(false, "验证失败!"), response);
        }
    }


    /**
     * 忘记密码->重置新密码->成功后跳转至登录界面
     *
     * @param jsonString
     * @param request
     * @param response
     */
    @RequestMapping(value = "/reset_password.json")
    public void toResetPassword(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {

        JSONObject jsonObject = JSON.parseObject(jsonString);

        String confirmPassword = jsonObject.getString("confirmPassword");
        String accountPassword = jsonObject.getString("accountPassword");
        String email = jsonObject.getString("email2");

        if (!confirmPassword.equals(confirmPassword)) {
            Json.toJson(new Result(false, "密码不一致!"), response);
        } else {
            Account account = new Account();
            account.setEmail(email);
            account.setAccountPassword(accountPassword);
            accountService.updateAccount(account);
            Json.toJson(new Result(true, "重置密码成功!"), response);
        }
    }

    /**
     * 退出登录->修改改login status->删除缓存->跳转至登录界面
     *
     * @param request
     * @param response
     */
    @UserLoginToken
    @RequestMapping(value = "/logout.json")
    public void logout(HttpServletRequest request, HttpServletResponse response) {

        String token = request.getParameter("token");
        Integer accountId = tokenService.getAccountIdByToken(token);
        //删除缓存
        cacheService.deleteValue(String.valueOf(accountId));
        Account account = new Account();
        account.setIsOnline("OFFLINE");
        account.setAccountId(accountId);
        accountService.updateLoginStatus(account);

        try {
            response.sendRedirect("/login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 账号设置
     *
     * @param jsonString
     * @param request
     * @param response
     */
    @RequestMapping(value = "/update_account.json")
    public void updateAccount(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {

        String token = request.getHeader("token");
        Integer accountId = tokenService.getAccountIdByToken(token);

        JSONObject jsonObject = JSON.parseObject(jsonString);

        Account account = new Account();
        account.setAccountId(accountId);
        if (StringUtils.isNotEmpty(jsonObject.getString("sex"))) {
            account.setSex(jsonObject.getString("sex"));
        }
        if (StringUtils.isNotEmpty(jsonObject.getString("birthday"))) {
            account.setBirthday(jsonObject.getString("birthday"));
        }
        if (StringUtils.isNotEmpty(jsonObject.getString("area"))) {
            account.setArea(jsonObject.getString("area"));
        }
        if (StringUtils.isNotEmpty(jsonObject.getString("headImageUrl"))) {
            account.setHeadImageUrl(jsonObject.getString("headImageUrl"));
        }
        if (StringUtils.isNotEmpty(jsonObject.getString("remark"))) {
            account.setRemark(jsonObject.getString("remark"));
        }
        if (StringUtils.isNotEmpty(jsonObject.getString("phone"))) {
            account.setPhone(jsonObject.getString("phone"));
        }

        accountService.updateAccount(account);

        Json.toJson(new Result(true, "注销成功!"), response);

    }

    /**
     * 获取用户信息
     *
     * @param request
     * @param response
     */
    @UserLoginToken
    @RequestMapping(value = "/get_account.json")
    public void getAccountData(HttpServletRequest request, HttpServletResponse response) {

        String token = request.getHeader("token");
        Integer accountId = tokenService.getAccountIdByToken(token);
        Condition condition = new Condition();
        condition.setAccountId(accountId);
        List<Account> accountList = accountService.getAccountByCondition(condition);
        Map data = new HashMap();
        if (CollectionUtils.isNotEmpty(accountList)) {
            data.put("data", accountList.get(0));
        }
        Json.toJson(new Result(true, "SUCCESS", data), response);
    }
}
