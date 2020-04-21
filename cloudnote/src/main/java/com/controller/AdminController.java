package com.controller;

import com.Util.Json;
import com.Util.Result;
import com.Util.TokenUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.entity.*;
import com.entity.admin.AdminData;
import com.entity.admin.GeneralData;
import com.entity.file.CNFile;
import com.entity.note.Note;
import com.interceptor.UserLoginToken;
import com.mailService.MailServiceImpl;
import com.service.serviceImpl.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

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

    @Autowired
    NoteServiceImpl noteService;

    @Autowired
    ImageServiceImpl imageService;

    @Autowired
    FileServiceImpl fileService;

    /**
     * 获取用户列表
     *
     * @param page
     * @param pageSize
     * @param request
     * @param response
     * @return
     */
    @UserLoginToken
    @RequestMapping("/account_list.json")
    public Object getAccountList(@RequestParam(value = "page", defaultValue = "1") String page,
                                 @RequestParam(value = "pageSize", defaultValue = "10") String pageSize,
                                 HttpServletRequest request, HttpServletResponse response) {
        Condition condition = new Condition();
        condition.setStartNumber(getStartNumber(Integer.parseInt(page),Integer.parseInt(pageSize)));
        condition.setPageSize(Integer.parseInt(pageSize));

        List<Account> accountList = accountService.getAccountByCondition(condition);
        List<AdminData> adminDataList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(accountList)) {
            accountList.forEach(p -> {
                adminDataList.add(new AdminData(p));
            });
        }
        Map<String, Object> responseMap = new HashMap();
        responseMap.put("code", "0");
        responseMap.put("msg", "success");
        responseMap.put("count", accountList.size());
        responseMap.put("data", adminDataList);
        return responseMap;
    }

    private Integer getStartNumber(Integer page,Integer pageSize){
        return  page == 1 ? 0 : ((page - 1 )* pageSize) ;
    }

    /**
     * 账户锁定/解锁操作
     *
     * @param jsonString
     * @param request
     * @param response
     */
    @UserLoginToken
    @RequestMapping(value = "/lock_unlock.json")
    public void updateAccount(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {
        Result result;
        String isLock = request.getHeader("isLock");
        JSONObject jsonObject = JSON.parseObject(jsonString);
        String accountId = jsonObject.getString("accountId");
        Account account = new Account();
        account.setAccountId(Integer.parseInt(accountId));
        account.setIsLocked(isLock);
        if (accountService.updateAccount(account)) {
            result = new Result(true, "SUCCESS");
        } else {
            result = new Result(false, "设置失败，请重新操作!");
        }
        Json.toJson(result, response);
    }


    /**
     * 获取头部概况数据
     *
     * @param request
     * @param response
     */
    @UserLoginToken
    @RequestMapping(value = "/get_head_data.json")
    public void getHeadData(HttpServletRequest request, HttpServletResponse response) {

        List<Account> accountList = accountService.getAccountByCondition(new Condition());
        List<Note> noteList = noteService.findNoteByCondition(new Condition());
        List<Image> imageList = imageService.findImageByCondition(new Condition());
        List<CNFile> cnFileList = fileService.findFileList(new Condition());

        GeneralData generalData = new GeneralData();
        generalData.setAccountCount(accountList.size());
        generalData.setFileCount(cnFileList.size());
        generalData.setImageCount(imageList.size());
        generalData.setNoteCount(noteList.size());

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String currentDay = format.format(new Date());

        //活月用户:
        Integer aliveCount = accountService.findAliveAccountByCondintion(new Condition());
        List<Note> currentDayNoteList = noteService.findNoteByCondition(new Condition(currentDay));
        List<Image> currentDayImageList = imageService.findImageByCondition(new Condition(currentDay));
        List<CNFile> currentDayCnFileList = fileService.findFileList(new Condition(currentDay));

        generalData.setCurrentDayFileCount(currentDayCnFileList.size());
        generalData.setCurrentDayImageCount(currentDayImageList.size());
        generalData.setCurrentDayNoteCount(currentDayNoteList.size());
        generalData.setAliveAccountCount(aliveCount);
        Result result = new Result(true, "SUCCESS", generalData);
        Json.toJson(result, response);
    }

}
