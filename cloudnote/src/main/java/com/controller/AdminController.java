package com.controller;

import com.Util.Json;
import com.Util.Result;
import com.interceptor.TokenUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.entity.*;
import com.entity.account.Account;
import com.entity.admin.AdminCount;
import com.entity.admin.AdminData;
import com.entity.admin.GeneralData;
import com.entity.file.CNFile;
import com.entity.note.Note;
import com.interceptor.UserLoginToken;
import com.mailService.MailServiceImpl;
import com.oss.OSSUtil;
import com.service.serviceImpl.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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

    @Autowired
    OSSUtil ossUtil;

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
        condition.setStartNumber(getStartNumber(Integer.parseInt(page), Integer.parseInt(pageSize)));
        condition.setPageSize(Integer.parseInt(pageSize));
        List<Account> accountList = accountService.getAccountByCondition(condition);

        List<AdminData> adminDataList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(accountList)) {
            accountList.forEach(p -> {
                AdminData adminData = new AdminData(p);
                Condition conditionCount = new Condition();
                conditionCount.setAccountId(p.getAccountId());
                //获取count
                AdminCount adminCount = adminService.findCount(conditionCount);
                adminData.setNote(adminCount.getNoteCount());
                adminData.setFile(adminCount.getFileCount());
                adminData.setImage(adminCount.getImageCount());
                adminDataList.add(adminData);
            });
        }
        //存储空间大小
        if (CollectionUtils.isNotEmpty(adminDataList)) {
            adminDataList.forEach(p -> {
                p.setImage(getImageSize(p.getAccountId()));
                p.setFile(getFileSize(p.getAccountId()));
            });
        }

        Map<String, Object> responseMap = new HashMap();
        responseMap.put("code", "0");
        responseMap.put("msg", "success");
        responseMap.put("count", accountList.size());
        responseMap.put("data", adminDataList);
        return responseMap;
    }

    /**
     * 获取图片的空间大小
     * 数据库中存储的是字节
     *
     * @return
     */
    private String getImageSize(String accountId) {
        //获取图片空间大小
        Condition conditionImage = new Condition();
        conditionImage.setAccountId(accountId);
        List<String> imageSizeList = imageService.selectSize(conditionImage);
        Long size = 0L;
        if (CollectionUtils.isNotEmpty(imageSizeList)) {
            for (String item : imageSizeList) {
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
     * 获取文件的空间大小
     * 数据库中存储的是字节
     *
     * @return
     */
    private String getFileSize(String accountId) {
        //获取文件图片大小
        Condition conditionFile = new Condition();
        conditionFile.setAccountId(accountId);
        List<String> fileSizeList = fileService.selectSize(conditionFile);
        Long size = 0L;

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


    private Integer getStartNumber(Integer page, Integer pageSize) {
        return page == 1 ? 0 : ((page - 1) * pageSize);
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
        account.setAccountId(accountId);
        account.setIsLocked(isLock);
        if (accountService.updateAccount(account)) {
            result = new Result(true, "SUCCESS");
        } else {
            result = new Result(false, "设置失败，请重新操作!");
        }
        Json.toJson(result, response);
    }


    /**
     * 获取系统概况数据
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

        //活跃用户:
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


    /**
     * 管理员发送邮件服务
     *
     * @param jsonString
     * @param request
     * @param response
     */
    @UserLoginToken
    @RequestMapping(value = "/send_mails.json")
    public void sendMails(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {
        Result result;
        JSONObject jsonObject = JSON.parseObject(jsonString);
        String emails = jsonObject.getString("emails");
        String[] emailsArray = emails.split(",");
        List<String> emailsList = Arrays.asList(emailsArray);

        String emailTheme = jsonObject.getString("emailTheme");
        if (StringUtils.isEmpty(emailTheme)) {
            emailTheme = "【云笔记】";
        }
        String emailContent = jsonObject.getString("emailContent");
        if (StringUtils.isEmpty(emailContent)) {
            result = new Result(false, "邮件正文不能为空!");
        } else if (StringUtils.isEmpty(emails)) {
            result = new Result(false, "请选择用户!");
        } else {
            if (mailService.sendEmailsByAdmin(emailsList, emailTheme, emailContent)) {
                result = new Result(true, "SUCCESS");
            } else {
                result = new Result(false, "FAILURE");
            }
        }
        Json.toJson(result, response);
    }
}
