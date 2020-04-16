package com.controller;

import com.Util.Json;
import com.Util.Result;
import com.Util.TokenUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.entity.Account;
import com.entity.Condition;
import com.entity.Constant;
import com.entity.NoteData;
import com.entity.admin.AdminData;
import com.interceptor.UserLoginToken;
import com.mailService.MailServiceImpl;
import com.service.AdminService;
import com.service.serviceImpl.AccountServiceImpl;
import com.service.serviceImpl.AdminServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author yq
 * @Date 2020/4/13 21:58
 */
@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    @Autowired
    AdminServiceImpl adminService;

    @Autowired
    private AccountServiceImpl accountService;

    @Autowired
    private MailServiceImpl mailService;

    @Autowired
    TokenUtils tokenService;



    /**
     * 获取用户列表
     * @param page
     * @param limit
     * @param request
     * @param response
     * @return
     */
    @UserLoginToken
    @RequestMapping("/account_list.json")
    public Object getAccountList(@RequestParam(value = "page", defaultValue = "1") String page,
                                 @RequestParam(value = "limit", defaultValue = "10") String limit,
                                 HttpServletRequest request, HttpServletResponse response) {
        Condition condition = new Condition();
        condition.setPage(Integer.parseInt(page));
        condition.setLimit(Integer.parseInt(limit));
        List<AdminData> adminDataList = adminService.getAccountList(condition);
        Integer count = adminService.getAccountCount(condition);

        Map<String, Object> responseMap = new HashMap();
        responseMap.put("code", "0");
        responseMap.put("msg", "success");
        responseMap.put("count", count);
        responseMap.put("data", adminDataList);
        return responseMap;
    }

    /**
     * 账户锁定/解锁操作
     * @param jsonString
     * @param request
     * @param response
     */
    @UserLoginToken
    @RequestMapping(value = "/lock_unlock.json")
    public void updateAccount(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response){

        String isLock = request.getHeader("isLock");
        JSONObject jsonObject = JSON.parseObject(jsonString);
        String accountId = jsonObject.getString("accountId");
        Account account = new Account();
        account.setAccountId(Integer.parseInt(accountId));
        account.setIsLocked(isLock);

        accountService.updateAccount(account);

        Json.toJson(new Result(true, "SUCCESS"), response);
    }

}
