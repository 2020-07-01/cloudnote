package com.controller;

import com.Util.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.BaiDuUtils;
import com.cache.CacheService;
import com.entity.Account;
import com.entity.Condition;
import com.entity.Constant;
import com.interceptor.PassToken;
import com.interceptor.UserLoginToken;
import com.mailService.MailServiceImpl;
import com.oss.OSSUtil;
import com.service.serviceImpl.AccountServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @Autowired
    OSSUtil ossUtil;

    @Autowired
    BaiDuUtils baiDuUtils;


    @Autowired
    ASEUtils aseUtils;

    /**
     * 邮箱注册
     *
     * @param jsonParam
     * @return
     */
    @PassToken
    @RequestMapping(value = "register.json")
    public void emailRegister(@RequestBody String jsonParam, HttpServletRequest request, HttpServletResponse response) {
        JSONObject jsonObject = JSON.parseObject(jsonParam);
        String securityCode = jsonObject.getString("securityCode");
        Result result;
        try {
            Map<String, String> securityCodeCacheMap = cacheService.getValue(securityCode);
            if (!securityCodeCacheMap.get(securityCode).equals(securityCode)) {
                result = new Result(false, "验证码错误");
            } else {
                cacheService.deleteValue(securityCode);//删除缓存
                String email = jsonObject.getString("email");
                String accountName = jsonObject.getString("accountName");
                String accountPassword = jsonObject.getString("accountPassword");
                Account account = new Account();
                String accountId = UUIDUtils.getUUID();
                account.setAccountId(accountId);
                account.setAccountName(accountName);
                //对密码进行加密存储
                String password = aseUtils.encrypt(accountPassword.getBytes("UTF-8"), accountId.getBytes("UTF-8"));
                account.setAccountPassword(password);
                account.setEmail(email);
                account.setHeadImageUrl("http://t.cn/RCzsdCq");
                account.setIllegalData("");
                Map<Boolean, String> map = accountService.insert(account);
                if (StringUtils.isNotEmpty(map.get(true))) {
                    result = new Result(true, "SUCCESS");
                } else {
                    result = new Result(false, map.get(false));
                }
            }
        } catch (Exception e) {
            log.error("用户注册异常:", new Throwable(e));
            result = new Result(false, "FAILURE");
        }
        Json.toJson(result, response);
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
    @RequestMapping(value = "/login.json")
    public void login(@RequestBody String jsonParam, HttpServletRequest request, HttpServletResponse response) {
        JSONObject jsonObject;
        Result result = null;
        jsonObject = JSON.parseObject(jsonParam);
        String accountName = jsonObject.getString("accountName");
        try {
            Account account;
            Condition condition = new Condition();
            condition.setAccountName(accountName);
            account = accountService.getOneAccount(condition);
            if (account == null) {
                result = new Result(false, "用户名或密码错误!");
            } else if (account.getIsLocked().equals(Constant.LOCK_YES)) {
                result = new Result(false, "账户被锁，请联系管理员!");
            } else {
                if (StringUtils.isNotEmpty(account.getAccountPassword())) {
                    //解密
                    String asePassword = new String(aseUtils.decrypt(Base64.decodeBase64(account.getAccountPassword()), account.getAccountId().getBytes("UTF-8")));
                    String accountPassword = jsonObject.getString("accountPassword");
                    //如果验证成功
                    if (asePassword.equals(accountPassword)) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String lastLoginTime = dateFormat.format(new Date());
                        account.setLastLoginTime(lastLoginTime);
                        account.setLoginCount(1);
                        //更新状态
                        boolean flag = accountService.updateLoginStatus(account);
                        if (flag) {
                            //生成token并存储在缓存中
                            String token = tokenService.createToken(account.getAccountId());
                            HashMap cacheMap = new HashMap();
                            cacheMap.put("accountId", account.getAccountId());
                            cacheService.putValue(account.getAccountId(), cacheMap);
                            HashMap data = new HashMap();
                            data.put("token", token);
                            if (jsonObject.getString("accountName").equals("admin")) {
                                result = new Result("admin", "SUCCESS!", data);
                            } else {
                                result = new Result(true, "SUCCESS!", data);
                            }
                        }
                    } else {
                        result = new Result(false, "用户名或密码错误!");
                    }
                }
            }
        } catch (Exception e) {
            log.error("账户" + accountName + "登录异常:", new Throwable(e));
            result = new Result(false, "异常错误!");
        }
        Json.toJson(result, response);
    }


    /**
     * 注册->发送验证码
     *
     * @param jsonParam
     * @param request
     * @param response
     * @return
     */
    @PassToken
    @RequestMapping(value = "send_security_code1.json")
    public void securityCode(@RequestBody String jsonParam, HttpServletRequest request, HttpServletResponse response) {
        Result result;
        JSONObject jsonObject = JSON.parseObject(jsonParam);
        String emailAddress = jsonObject.getString("emailAddress");

        //验证邮箱格式
        Map<String, String> serviceData = accountService.findUerByEmail(emailAddress);
        if (serviceData.get("true") != null) {
            //生成验证码
            String securityCode = random(6);
            try {
                mailService.sendSecurityCode(emailAddress, securityCode);
                //将验证码存储在缓存中
                Map<String, String> securityCodeCacheMap = new HashMap<>();
                securityCodeCacheMap.put(securityCode, securityCode);
                cacheService.putValue(securityCode, securityCodeCacheMap);
                result = new Result(true, "验证码已发送");
                //Json.toJson(new Result(true, "验证码已发送"), response);
            } catch (Exception e) {
                log.error("验证码发送失败:" + e.getMessage(), new Throwable(e));
                result = new Result(false, "FAILURE!");
            }
        } else {
            log.info(emailAddress + "意见注册");
            result = new Result(false, serviceData.get("false"));
        }
        Json.toJson(result, response);
    }

    /**
     * 系统设置->修改密码
     *
     * @param jsonString
     * @param request
     * @param response
     */
    @UserLoginToken
    @RequestMapping(value = "/update_password.json")
    public void updatePassword(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {
        Result result;
        JSONObject jsonObject = JSON.parseObject(jsonString);
        String token = request.getHeader("token");
        String accountId = tokenService.getAccountIdByToken(token);
        String currentPassword = jsonObject.getString("currentPassword");
        String accountPassword = jsonObject.getString("accountPassword");
        String confirmPassword = jsonObject.getString("confirmPassword");
        try {
            //获取旧的密码
            String oldAccountPassword = accountService.findPasswordByAccountId(accountId);
            //解密
            String asePassword = new String(aseUtils.decrypt(Base64.decodeBase64(oldAccountPassword), accountId.getBytes("UTF-8")));
            if (!asePassword.equals(currentPassword)) {
                result = new Result(false, "密码错误!");
            } else if (!accountPassword.equals(confirmPassword)) {
                result = new Result(false, "密码不一致!");
            } else if (asePassword.equals(accountPassword)) { //判断密码是否重复
                result = new Result(false, "新密码不能与原来的密码一致!");
            } else {
                //更新密码
                Account account = new Account();
                account.setAccountId(accountId);
                //对新密码进行加密
                String password = aseUtils.encrypt(accountPassword.getBytes("UTF-8"), accountId.getBytes("UTF-8"));
                account.setAccountPassword(password);
                if (accountService.updateAccount(account)) {
                    result = new Result(true, "SUCCESS");
                } else {
                    result = new Result(false, "FAILURE");
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), new Throwable(e));
            result = new Result(false, "FAILURE");
        }
        Json.toJson(result, response);
    }

    /**
     * 忘记密码->发送验证码
     */
    @PassToken
    @RequestMapping(value = "/send_security_code.json")
    public void getSecurityCode(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {

        JSONObject jsonObject = JSON.parseObject(jsonString);
        Result result;
        try {
            String email = jsonObject.getString("email");
            Condition condition = new Condition();
            condition.setEmail(email);
            Account account = accountService.getOneAccount(condition);
            if (account == null) {
                result = new Result(false, "此邮箱不存在!");
            } else {
                //生成6位验证码
                String securityCode = random(6);
                mailService.sendSecurityCode(email, securityCode);
                HashMap data = new HashMap();
                data.put("securityCode", securityCode);
                data.put("email", email);
                data.put("accountId", account.getAccountId());
                result = new Result(true, "验证码已发送，请查收!", data);
            }
        } catch (Exception e) {
            result = new Result(false, "FAILURE");
            log.error(e.getMessage(), new Throwable(e));
        }
        Json.toJson(result, response);
    }

    /**
     * 忘记密码->重置新密码->成功后跳转至登录界面
     *
     * @param jsonString
     * @param request
     * @param response
     */
    @PassToken
    @RequestMapping(value = "/reset_password.json")
    public void toResetPassword(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {

        Result result;
        JSONObject jsonObject = JSON.parseObject(jsonString);
        try {
            String confirmPassword = jsonObject.getString("confirmPassword");
            String accountPassword = jsonObject.getString("accountPassword");
            String email = jsonObject.getString("email");
            String accountId = jsonObject.getString("accountId");
            if (!confirmPassword.equals(confirmPassword)) {
                result = new Result(false, "密码不一致!");
            } else {
                Account account = new Account();
                account.setEmail(email);
                account.setAccountPassword(accountPassword);
                //对新密码进行加密
                String password = aseUtils.encrypt(accountPassword.getBytes("UTF-8"), accountId.getBytes("UTF-8"));
                account.setAccountPassword(password);
                accountService.updateAccount(account);
                result = new Result(true, "SUCCESS!");
            }
        } catch (Exception e) {
            result = new Result(false, "FAILURE");
            log.error(e.getMessage(), new Throwable(e));
        }
        Json.toJson(result, response);
    }

    /**
     * 注销登录->删除缓存->跳转至登录界面
     *
     * @param request
     * @param response
     */
    @UserLoginToken
    @RequestMapping(value = "/logout.json")
    public void logout(HttpServletRequest request, HttpServletResponse response) {

        String token = request.getParameter("token");
        String accountId = tokenService.getAccountIdByToken(token);
        try {
            //删除缓存
            cacheService.deleteValue(accountId);
            response.sendRedirect("/login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 用户设置->修改用户信息以及头像
     *
     * @param jsonString
     * @param request
     * @param response
     */
    @UserLoginToken
    @RequestMapping(value = "/update_account.json")
    public void updateAccount(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("token");
        String accountId = tokenService.getAccountIdByToken(token);
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
        Json.toJson(new Result(true, "设置成功!"), response);
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
        Result result = null;
        String token = request.getHeader("token");
        String accountId = tokenService.getAccountIdByToken(token);
        Condition condition = new Condition();
        condition.setAccountId(accountId);
        List<Account> accountList = accountService.getAccountByCondition(condition);
        if (CollectionUtils.isNotEmpty(accountList)) {
            result = new Result(true, "SUCCESS", accountList.get(0));
        }
        Json.toJson(result, response);
    }

    /**
     * 上传头像->返回头像的url地址
     *
     * @param headImage
     * @param request
     * @param response
     * @throws IOException
     */
    @UserLoginToken
    @RequestMapping(value = "/upload_image.json")
    public void uploadHeadImage(@RequestParam(value = "file", required = false) MultipartFile headImage,
                                HttpServletRequest request, HttpServletResponse response) throws IOException {
        Result result = null;
        //图片审核
        org.json.JSONObject jsonObject = baiDuUtils.checkImage(headImage.getBytes());
        if (jsonObject == null) {
            result = new Result(false, "FAILURE");
        } else
            //如果不合规
            if (jsonObject.getString("conclusion").equals(Constant.CONCLUSION_2)) {
                JSONArray dataJSONArray = jsonObject.getJSONArray("data");
                String msg = dataJSONArray.getJSONObject(0).getString("msg");
                result = new Result(false, msg);
            } else {
                String token = request.getHeader("token");
                String accountId = tokenService.getAccountIdByToken(token);
                String headName = random(5);
                String imageType = headImage.getOriginalFilename().substring(headImage.getOriginalFilename().lastIndexOf(".") + 1);// 获取文件的类型
                String headPath = accountId + "/" + "headImage" + "/" + headName + "." + imageType;
                Map<String, String> ossMap = ossUtil.putObject(headImage.getBytes(), headPath);
                if (StringUtils.isNotEmpty(ossMap.get(Constant.FILE_IMAGE_URL))) {
                    String url = ossMap.get(Constant.FILE_IMAGE_URL).substring(0, ossMap.get(Constant.FILE_IMAGE_URL).lastIndexOf("?"));
                    result = new Result(true, "上传成功!", url);
                } else {
                    result = new Result(false, "上传失败!");
                }
            }
        Json.toJson(result, response);
    }


    /**
     * 获取随机数
     *
     * @param n
     * @return
     */
    private String random(int n) {
        Random random = new Random();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < n; i++) {
            stringBuffer.append(random.nextInt(10));
        }
        return stringBuffer.toString();
    }

    /**
     * 获取头部数据
     */
    @RequestMapping(value = "/getHeadData.json")
    @UserLoginToken
    public void getHeadData(HttpServletRequest request, HttpServletResponse response) {
        Result result = null;
        Map<String, String> data = new HashMap<>();
        String token = request.getHeader("token");
        String accountId = tokenService.getAccountIdByToken(token);
        Condition condition = new Condition();
        condition.setAccountId(accountId);
        List<Account> accountList = accountService.getAccountByCondition(condition);
        if (CollectionUtils.isNotEmpty(accountList)) {
            if (StringUtils.isNotEmpty(accountList.get(0).getHeadImageUrl())) {
                data.put("headImageUrl", accountList.get(0).getHeadImageUrl());
            }
            if (StringUtils.isNotEmpty(accountList.get(0).getAccountName())) {
                data.put("accountName", accountList.get(0).getAccountName());
            }
            result = new Result(true, "SUCCESS", data);
        } else {
            result = new Result(false, "FAILURE", data);
        }
        Json.toJson(result, response);
    }
}
