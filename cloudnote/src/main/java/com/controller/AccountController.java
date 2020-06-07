package com.controller;

import com.Util.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.BaiDuUtils;
import com.cache.CacheService;
import com.entity.account.Account;
import com.entity.Condition;
import com.entity.Constant;
import com.entity.account.AccountData;
import com.interceptor.PassToken;
import com.interceptor.TokenUtils;
import com.interceptor.UserLoginToken;
import com.mailService.MailServiceImpl;
import com.oss.OSSUtil;
import com.service.serviceImpl.AccountServiceImpl;
import com.service.serviceImpl.FileServiceImpl;
import com.service.serviceImpl.ImageServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.C;
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
    AESUtils aesUtils;

    @Autowired
    ImageServiceImpl imageService;

    @Autowired
    FileServiceImpl fileService;

    @Autowired
    TokenUtils tokenUtils;

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
        Result result;
        try {
            if (checkSecurity(jsonParam)) {//验证码正确
                //核对密码是否正确，前端会对密码进行判空处理
                String accountPassword = jsonObject.getString("accountPassword");
                String confirmPassword = jsonObject.getString("confirmPassword");
                if (!accountPassword.equals(confirmPassword)) {
                    //两次密码输入不一致
                    result = new Result(false, Constant.password_message_1);
                } else {
                    //两次密码一致
                    String email = jsonObject.getString("email");
                    String accountName = jsonObject.getString("accountName");
                    Account account = new Account();
                    String accountId = UUIDUtils.getUUID();
                    account.setAccountId(accountId);
                    account.setAccountName(accountName);
                    //对密码进行加密存储
                    String password = aesUtils.encrypt(accountPassword.getBytes("UTF-8"), accountId.getBytes("UTF-8"));
                    account.setAccountPassword(password);
                    account.setEmail(email);
                    account.setHeadImageUrl("https://001-bucket.oss-cn-shanghai.aliyuncs.com/common/title.gif");//设置默认头像
                    account.setIllegalData("");
                    Map<Boolean, String> map = accountService.insert(account);
                    if (StringUtils.isNotEmpty(map.get(true))) {
                        result = new Result(true, Constant.register_message_1);
                    } else {
                        result = new Result(false, map.get(false));
                    }
                }
            } else {
                //验证码错误
                result = new Result(false, Constant.security_code_message_1);
            }
        } catch (Exception e) {
            log.error("用户注册异常:", new Throwable(e));
            result = new Result(false, Constant.register_message_2);
        }
        Json.toJson(result, response);
    }


    /**
     * 用户(邮箱)注册->发送验证码
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
        //验证邮箱
        Map<String, String> map = accountService.findUerByEmail(emailAddress);
        if (StringUtils.isNotEmpty(map.get("email_5"))) {//邮箱未注册
            //生成验证码
            String securityCode = random(6);
            //获取验证码的ASCII值
            String securityCodeASCII = stringToAscii(securityCode);
            try {
                String content = "【<span style=\"font-weight: bold\">云笔记</span>】您正在进行云笔记注册，验证码<span style=\"font-size: 20px\">" + securityCode + "</span>，请在10分钟内按页面提示提交验证码，如非本人操作请忽略。<br><span style=\"color: #645f66;\">&nbsp;提示：请勿将验证码泄露于他人</span>";
                mailService.sendSecurityCode(Constant.email_address_1, emailAddress, Constant.subject_1, content);
                //将验证码传送到前端
                result = new Result(true, Constant.email_message_1, securityCodeASCII);
            } catch (Exception e) {
                log.error("验证码发送失败:" + e.getMessage(), new Throwable(e));
                result = new Result(false, Constant.email_message_2);
            }
        } else if (StringUtils.isNotEmpty(map.get("email_3"))) {
            result = new Result(false, Constant.email_message_3);
        } else {
            result = new Result(false, Constant.email_message_2);
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
            account.setAccountName(accountName);
            if (account == null) {
                result = new Result(false, Constant.login_message_1);
            } else if (account.getIsLocked().equals(Constant.LOCK_YES)) {
                result = new Result(false, Constant.login_message_2);
            } else {
                if (StringUtils.isNotEmpty(account.getAccountPassword())) {
                    //解密
                    String aesPassword = new String(aesUtils.decrypt(Base64.decodeBase64(account.getAccountPassword()), account.getAccountId().getBytes("UTF-8")));
                    String accountPassword = jsonObject.getString("accountPassword");
                    //如果验证成功
                    if (aesPassword.equals(accountPassword)) {
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
                            //当token过期时会自动删除，如果没有自动删除，旧的cacheMap会自动覆盖
                            cacheService.putValue(account.getAccountId(), cacheMap);
                            HashMap data = new HashMap();
                            data.put("token", token);
                            if (jsonObject.getString("accountName").equals("admin")) {
                                result = new Result("admin", Constant.login_message_3, data);
                            } else {
                                result = new Result(true, Constant.login_message_3, data);
                            }
                        } else {
                            result = new Result(false, Constant.login_message_1);
                        }
                    } else {
                        result = new Result(false, Constant.login_message_1);
                    }
                }
            }
        } catch (Exception e) {
            log.error("账户" + accountName + "登录异常:", new Throwable(e));
            result = new Result(false, Constant.login_message_4);
        }
        Json.toJson(result, response);
    }


    /**
     * 更换邮箱->旧邮箱发送验证码
     *
     * @param jsonParam
     * @param request
     * @param response
     * @return
     */
    @PassToken
    @RequestMapping(value = "send_security_code_3.json")
    public void sendSecurityCode3(@RequestBody String jsonParam, HttpServletRequest request, HttpServletResponse response) {
        Result result;
        JSONObject jsonObject = JSON.parseObject(jsonParam);
        String emailAddress = jsonObject.getString("emailAddress");
        //生成验证码
        String securityCode = random(6);
        //获取验证码的ASCII值
        String securityCodeASCII = stringToAscii(securityCode);
        try {
            String content = "【<span style=\"font-weight: bold\">云笔记</span>】您正在进行邮箱换绑，验证码<span style=\"font-size: 20px\">" + securityCode + "</span>，请在1分钟内按页面提示提交验证码，如非本人操作请忽略。<br><span style=\"color: #645f66;\">&nbsp;提示：请勿将验证码泄露于他人</span>";
            mailService.sendSecurityCode(Constant.email_address_1, emailAddress, Constant.subject_1, content);
            //将验证码传送到前端
            result = new Result(true, Constant.email_message_1, securityCodeASCII);
        } catch (Exception e) {
            log.error("验证码发送失败:" + e.getMessage(), new Throwable(e));
            result = new Result(false, Constant.email_message_2);
        }
        Json.toJson(result, response);
    }

    /**
     * 更换邮箱->新邮箱发送验证码
     *
     * @param jsonParam
     * @param request
     * @param response
     */
    @UserLoginToken
    @RequestMapping(value = "/send_security_code_4.json")
    public void sendSecurityCode4(@RequestBody String jsonParam, HttpServletRequest request, HttpServletResponse response) {
        Result result;
        JSONObject jsonObject = JSON.parseObject(jsonParam);
        String emailAddress = jsonObject.getString("emailAddress");
        try {
            //生成验证码
            String securityCode = random(6);
            //获取验证码的ASCII值
            String securityCodeASCII = stringToAscii(securityCode);
            //对邮箱进行判断
            Map<String, String> map = accountService.findUerByEmail(emailAddress);
            if (map.get("email_5") != null) {
                String content = "【<span style=\"font-weight: bold\">云笔记</span>】您正在进行邮箱换绑，验证码<span style=\"font-size: 20px\">" + securityCode + "</span>，请在1分钟内按页面提示提交验证码，如非本人操作请忽略。<br><span style=\"color: #645f66;\">&nbsp;提示：请勿将验证码泄露于他人</span>";
                mailService.sendSecurityCode(Constant.email_address_1, emailAddress, Constant.subject_1, content);
                //将验证码传送到前端
                result = new Result(true, Constant.email_message_1, securityCodeASCII);

            } else if (StringUtils.isNotEmpty(map.get("email_3"))) {
                result = new Result(false, map.get("email_3"));
            } else {
                result = new Result(false, map.get("email_2"));
            }
        } catch (Exception e) {
            log.error("验证码发送失败:" + e.getMessage(), new Throwable(e));
            result = new Result(false, Constant.email_message_2);
        }
        Json.toJson(result, response);
    }

    /**
     * 核对验证码是否一致
     *
     * @param jsonParam
     * @param request
     * @param response
     */
    @UserLoginToken
    @RequestMapping(value = "/check_security_code.json")
    public void checkSecurityCode(@RequestBody String jsonParam, HttpServletRequest request, HttpServletResponse response) {

        Result result;
        JSONObject jsonObject = JSON.parseObject(jsonParam);
        String firstSecurityCodeAscii = jsonObject.getString("firstSecurityCode");
        if (StringUtils.isNotEmpty(firstSecurityCodeAscii)) {
            String firstSecurityCode = ascillToString(firstSecurityCodeAscii);
            String secondSecurityCode = jsonObject.getString("secondSecurityCode");
            if (firstSecurityCode.equals(secondSecurityCode)) {
                result = new Result(true, Constant.security_code_message_2);
            } else {
                result = new Result(false, Constant.security_code_message_1);
            }
        } else {
            result = new Result(false, Constant.security_code_message_1);
        }
        Json.toJson(result, response);
    }

    /**
     * 更换邮箱
     *
     * @param jsonParam
     * @param request
     * @param response
     */
    @UserLoginToken
    @RequestMapping(value = "/update_email.json")
    public void updateEmail(@RequestBody String jsonParam, HttpServletRequest request, HttpServletResponse response) {
        Result result;
        JSONObject jsonObject = JSON.parseObject(jsonParam);
        String firstSecurityCodeAscii = jsonObject.getString("firstSecurityCode");
        if (StringUtils.isNotEmpty(firstSecurityCodeAscii)) {
            String firstSecurityCode = ascillToString(firstSecurityCodeAscii);
            String secondSecurityCode = jsonObject.getString("secondSecurityCode");
            if (firstSecurityCode.equals(secondSecurityCode)) {
                //更新邮箱地址
                Account account = new Account();
                String email = jsonObject.getString("email");
                account.setEmail(email);
                String token = request.getHeader("token");
                String accountId = tokenService.getAccountIdByToken(token);
                account.setAccountId(accountId);
                if (accountService.updateAccount(account)) {
                    result = new Result(true, Constant.update_email_message_1);
                } else {
                    result = new Result(true, Constant.update_email_message_2);
                }
            } else {
                result = new Result(false, Constant.security_code_message_1);
            }
        } else {
            result = new Result(false, Constant.security_code_message_1);
        }
        Json.toJson(result, response);
    }

    //获取字符串的ASCII值，逗号进行分隔
    private String stringToAscii(String string) {
        StringBuffer stringBuffer = new StringBuffer();
        char[] chars = string.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            stringBuffer.append(Integer.valueOf(chars[i])).append(",");
        }
        return stringBuffer.toString();
    }

    //获取ASCII对应的字符串
    private String ascillToString(String stringASCII) {
        String[] chars = stringASCII.split(",");
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            stringBuffer.append((char) Integer.parseInt(chars[i]));
        }
        return stringBuffer.toString();
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
            String asePassword = new String(aesUtils.decrypt(Base64.decodeBase64(oldAccountPassword), accountId.getBytes("UTF-8")));
            if (!asePassword.equals(currentPassword)) {
                result = new Result(false, Constant.password_message_2);
            } else if (!accountPassword.equals(confirmPassword)) {
                result = new Result(false, Constant.password_message_1);
            } else if (asePassword.equals(accountPassword)) { //判断密码是否重复
                result = new Result(false, Constant.password_message_3);
            } else {
                //更新密码
                Account account = new Account();
                account.setAccountId(accountId);
                //对新密码进行加密
                String password = aesUtils.encrypt(accountPassword.getBytes("UTF-8"), accountId.getBytes("UTF-8"));
                account.setAccountPassword(password);
                if (accountService.updateAccount(account)) {
                    result = new Result(true, Constant.password_message_6);
                } else {
                    result = new Result(false, Constant.password_message_5);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), new Throwable(e));
            result = new Result(false, Constant.password_message_5);
        }
        Json.toJson(result, response);
    }

    /**
     * 忘记密码->发送验证码
     */
    @PassToken
    @RequestMapping(value = "/send_security_code.json")
    public void sendSecurityCode(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {

        JSONObject jsonObject = JSON.parseObject(jsonString);
        Result result;
        try {

            String email = jsonObject.getString("email");
            Condition condition = new Condition();
            condition.setEmail(email);
            Account account = accountService.getOneAccount(condition);
            if (account == null) {
                result = new Result(false, Constant.email_message_5);
            } else {
                //生成6位验证码
                String securityCode = random(6);
                String content = "【<span style=\"font-weight: bold\">云笔记</span>】您正在找回密码，验证码<span style=\"font-size: 20px\">" + securityCode + "</span>，请在1分钟内按页面提示提交验证码，如非本人操作请忽略。<br><span style=\"color: #645f66;\">&nbsp;提示：请勿将验证码泄露于他人</span>";
                mailService.sendSecurityCode(Constant.email_address_1, email, Constant.subject_1, content);
                String securityCodeAscii = stringToAscii(securityCode);
                HashMap data = new HashMap();
                data.put("securityCode", securityCodeAscii);
                data.put("accountId", account.getAccountId());
                result = new Result(true, Constant.email_message_1, data);
            }
        } catch (Exception e) {
            result = new Result(false, Constant.email_message_2);
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
                result = new Result(false, Constant.password_message_1);
            } else {
                Account account = new Account();
                account.setEmail(email);
                account.setAccountPassword(accountPassword);
                //对新密码进行加密
                String password = aesUtils.encrypt(accountPassword.getBytes("UTF-8"), accountId.getBytes("UTF-8"));
                account.setAccountPassword(password);
                account.setAccountId(accountId);
                accountService.updateAccount(account);
                result = new Result(true, Constant.password_message_4);
            }
        } catch (Exception e) {
            result = new Result(false, Constant.password_message_5);
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
     * 用户设置-》获取用户信息
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
            String size = getAllSize(accountList.get(0).getAccountId());
            AccountData accountData = new AccountData(accountList.get(0));
            accountData.setSize(size);
            result = new Result(true, "SUCCESS", accountData);
        }
        Json.toJson(result, response);
    }

    /**
     * 获取总的空间大小
     * 数据库中存储的是字节
     *
     * @return
     */
    private String getAllSize(String accountId) {
        //获取图片空间大小
        Condition conditionImage = new Condition();
        conditionImage.setAccountId(accountId);
        List<String> imageSizeList = imageService.selectSize(conditionImage);
        //获取文件图片大小
        Condition conditionFile = new Condition();
        conditionImage.setAccountId(accountId);
        List<String> fileSizeList = fileService.selectSize(conditionFile);

        Long size = 0L;
        if (CollectionUtils.isNotEmpty(imageSizeList)) {
            for (String item : imageSizeList) {
                Long itemSize = Long.valueOf(item);
                size = size + itemSize;
            }
        }
        if (CollectionUtils.isNotEmpty(fileSizeList)) {
            for (String item : fileSizeList) {
                Long itemSize = Long.valueOf(item);
                size = size + itemSize;
            }
        }

        StringBuffer stringBuffer = new StringBuffer();

        //转换为GB
        if (size / 1024 / 1024 > 1024) {
            stringBuffer.append(size / 1024 / 1024 / 1024);//转换为GB 整数位
            stringBuffer.append(".");
            Long decimals = size % 1024 % 1024 % 1024; //GB 小数位
            stringBuffer.append(decimals);
            stringBuffer.append("GB");
        } else {
            //转换为MB
            stringBuffer.append(size / 1024 / 1024);//转换为MB 整数位
            stringBuffer.append(".");
            Long decimals = size % 1024 % 1024; //MB 小数位
            stringBuffer.append(decimals);
            stringBuffer.append("MB");
        }
        return stringBuffer.toString();
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


    /**
     * 邮箱登录-》发送验证码
     *
     * @param jsonParam
     * @param request
     * @param response
     * @return
     */
    @PassToken
    @RequestMapping(value = "send_security_code_5.json")
    public void sendSecurityCode5(@RequestBody String jsonParam, HttpServletRequest request, HttpServletResponse response) {
        Result result;
        JSONObject jsonObject = JSON.parseObject(jsonParam);
        String emailAddress = jsonObject.getString("email");
        //生成验证码
        String securityCode = random(6);
        //获取验证码的ASCII值
        String securityCodeASCII = stringToAscii(securityCode);
        try {
            //对邮箱进行判断
            Map<String, String> map = accountService.findUerByEmail(emailAddress);
            //如果邮箱不存在
            if (StringUtils.isNotEmpty(map.get("email_3"))) {
                String content = "【<span style=\"font-weight: bold\">云笔记</span>】您正在进行邮箱登录，验证码<span style=\"font-size: 20px\">" + securityCode + "</span>，请在1分钟内按页面提示提交验证码，如非本人操作请忽略。<br><span style=\"color: #645f66;\">&nbsp;提示：请勿将验证码泄露于他人</span>";
                mailService.sendSecurityCode(Constant.email_address_1, emailAddress, Constant.subject_1, content);
                //将验证码传送到前端
                result = new Result(true, Constant.email_message_1, securityCodeASCII);
            } else if (StringUtils.isNotEmpty(map.get("email_5"))) { //如果邮箱不存在
                result = new Result(false, Constant.email_message_5);
            } else {
                //如果出现异常
                result = new Result(false, Constant.email_message_2);
            }
        } catch (Exception e) {
            log.error("验证码发送失败:" + e.getMessage(), new Throwable(e));
            result = new Result(false, Constant.email_message_2);
        }
        Json.toJson(result, response);
    }


    /**
     * 邮箱登录-》核对验证码
     * 邮箱登录不需要进行密码验证
     * 登陆成功后在缓存中创建私有Map
     *
     * @param jsonParam
     * @param request
     * @param response
     * @return
     */
    @PassToken
    @RequestMapping(value = "/login_1.json")
    public void emailLogin(@RequestBody String jsonParam, HttpServletRequest request, HttpServletResponse response) {
        JSONObject jsonObject;
        Result result = null;
        jsonObject = JSON.parseObject(jsonParam);
        String email = jsonObject.getString("email");
        try {
            //核对验证码
            if (checkSecurity(jsonParam)) {//验证码核对通过
                Condition condition = new Condition();
                condition.setEmail(email);
                Account account = accountService.getOneAccount(condition);
                if (account == null) {
                    result = new Result(false, Constant.login_message_5);
                } else if (account.getIsLocked().equals(Constant.LOCK_YES)) {
                    result = new Result(false, Constant.login_message_2);
                } else {
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
                        //当token过期时会自动删除，如果没有自动删除，旧的cacheMap会自动覆盖
                        cacheService.putValue(account.getAccountId(), cacheMap);
                        HashMap data = new HashMap();
                        data.put("token", token);
                        //此处通过邮箱来判断登录者身份
                        if (email.equals(Constant.ADMIN_EMAIL)) {
                            result = new Result("admin", Constant.login_message_3, data);
                        } else {
                            result = new Result(true, Constant.login_message_3, data);
                        }
                    } else {
                        result = new Result(false, Constant.login_message_1);
                    }
                }
            } else {
                result = new Result(false, Constant.security_code_message_1);
            }
        } catch (Exception e) {
            e.getMessage();
            result = new Result(false, Constant.login_message_4);
        }
        Json.toJson(result, response);
    }

    /**
     * 邮箱登录/用户注册->核对验证码
     *
     * @param jsonParam
     * @return
     */
    private boolean checkSecurity(String jsonParam) {
        JSONObject jsonObject = JSON.parseObject(jsonParam);
        String firstSecurityCodeAscii = jsonObject.getString("firstSecurityCode");
        if (StringUtils.isNotEmpty(firstSecurityCodeAscii)) {
            String firstSecurityCode = ascillToString(firstSecurityCodeAscii);
            String secondSecurityCode = jsonObject.getString("secondSecurityCode");
            if (firstSecurityCode.equals(secondSecurityCode)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 验证Token是否正确
     *
     * @param request
     * @param response
     */
    @PassToken
    @RequestMapping(value = "/check_token.json")
    public void toRecycleBin(HttpServletRequest request, HttpServletResponse response) {
        Result result;
        String token = request.getHeader("token");
        if (tokenUtils.verifyToken(token)) {
            result = new Result(true, Constant.SUCCESS);
        } else {
            result = new Result(false, Constant.FAILURE);
        }
        Json.toJson(result, response);
    }

    /**
     * 意见反馈
     *
     * @param jsonParam
     * @param request
     * @param response
     */
    @PassToken
    @RequestMapping(value = "/feedback.json")
    public void feedback(@RequestBody String jsonParam, HttpServletRequest request, HttpServletResponse response) {
        JSONObject jsonObject;
        Result result = null;
        try {
            jsonObject = JSON.parseObject(jsonParam);
            String token = request.getHeader("token");
            String accountId = tokenService.getAccountIdByToken(token);
            Condition condition = new Condition();
            condition.setAccountId(accountId);
            Account account = accountService.getOneAccount(condition);
            Set<String> key = jsonObject.keySet();
            //如果key的数量为1，并且其他信息为空时直接返回
            if (key.size() == 1 && StringUtils.isEmpty(jsonObject.getString("feedback_9"))) {
                result = new Result(true, Constant.feedback_1);
            } else {
                String content = "<span style=\"font-size: 16px\">" + "用户" + "<span style=\"font-size: 16px;font-weight: bold\">" + account.getAccountName()  + "</span>" +"(" +  account.getEmail()+ ")" + "意见反馈信息：" + "</span>";
                int i = 1;
                for (String p : key) {
                    content = content + "<br>" + "问题" + i++ + "：";
                    switch (p) {
                        case "feedback_1":
                            content = content + "笔记丢失";
                            break;
                        case "feedback_2":
                            content = content + "笔记无法收藏";
                            break;
                        case "feedback_3":
                            content = content + "无法添加笔记";
                            break;
                        case "feedback_4":
                            content = content + "文件丢失";
                            break;
                        case "feedback_5":
                            content = content + "无法上传文件";
                            break;
                        case "feedback_6":
                            content = content + "无法下载文件";
                            break;
                        case "feedback_7":
                            content = content + "无法创建日程";
                            break;
                        case "feedback_8":
                            content = content + "日程提醒失败";
                            break;
                        case "feedback_9":
                            if (StringUtils.isNotBlank(jsonObject.getString("feedback_9"))) {
                                content = content + jsonObject.getString("feedback_9");
                            }
                            break;
                        default:
                            continue;
                    }
                }
                String receiver = Constant.email_address_1;
                String subject = "【云笔记】：用户意见反馈";
                content = content.substring(0, content.length() - 4);
                if (mailService.sendFeedback(receiver, subject, content)) {
                    result = new Result(true, Constant.SUCCESS);
                } else {
                    result = new Result(true, Constant.FAILURE);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), new Throwable(e));
            result = new Result(true, Constant.FAILURE);
        }

        Json.toJson(result, response);
    }
}
