package com.controller;

import com.Util.Json;
import com.Util.Result;
import com.Util.TokenUtils;
import com.Util.UUIDUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.BaiDuUtils;
import com.cache.CacheService;
import com.entity.*;
import com.entity.file.CNFile;

import com.interceptor.UserLoginToken;
import com.oss.OSSUtil;

import com.service.serviceImpl.AccountServiceImpl;
import com.service.serviceImpl.FileServiceImpl;
import com.service.serviceImpl.ImageServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @program: UploadController
 * @description:
 * @create: 2020-02-08 15:39
 **/
@Slf4j
@RequestMapping(value = "/source")
@RestController
public class UploadController {

    @Autowired
    private TokenUtils tokenUtil;

    @Autowired
    OSSUtil ossUtil;

    @Autowired
    ImageServiceImpl imageService;

    @Autowired
    CacheService cacheService;

    @Autowired
    FileServiceImpl fileService;

    @Autowired
    BaiDuUtils baiDuUtils;

    @Autowired
    AccountServiceImpl accountService;

    /**
     * 上传图片
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @UserLoginToken
    @RequestMapping(value = "/upload_image.json")
    public void uploadImage(@RequestParam(value = "file", required = false) MultipartFile uploadImage,
                            HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = request.getHeader("token");
        String accountId = tokenUtil.getAccountIdByToken(token);
        Result result = null;
        try {
            //图片审核
            Map<String, String> map = check(uploadImage.getBytes());
            //true
            if (StringUtils.isNotEmpty(map.get("true"))) {
                //如果图片审核通过
                Map serviceData = imageService.uploadImage(uploadImage, accountId);
                if (serviceData.get("true") != null) {
                    result = new Result(true, "上传成功!");
                } else {
                    Map<String, String> data = new HashMap<>();
                    data.put("cache", serviceData.get("fail").toString());
                    result = new Result(false, serviceData.get("message").toString(), data);
                }
            } else {
                //图片审核不通过
                //存储到数据库
                String message = map.get("false");
                Account account = new Account();
                account.setAccountId(accountId);
                String illegalData = map.get("illegalData");
                account.setIllegalData(illegalData);
                accountService.updateAccount(account);
                result = new Result("2", "审核不通过" + message.replaceAll("<br>", ""));
            }
        } catch (Exception e) {
            result = new Result(false, "异常错误!");
            log.error(e.getMessage(), new Throwable(e));
        }
        Json.toJson(result, response);
    }


    /**
     * 图片审核
     *
     * @param bytes
     * @return
     */
    private Map<String, String> check(byte[] bytes) {
        Map<String, String> result = new HashMap<>();
        org.json.JSONObject jsonObject = baiDuUtils.checkImage(bytes);
        //判断是否审核失败
        if (!jsonObject.isNull("error_code")) {
            result.put("false", "图片审核失败!");
        } else {
            //审核成功
            if (!jsonObject.isNull("conclusion")) {
                if (jsonObject.getString("conclusion").equals(Constant.CONCLUSION_2)) {
                    //如果审核不合规
                    StringBuffer stringBuffer = new StringBuffer();
                    StringBuffer stringBuffer1 = new StringBuffer();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    JSONArray dataJSONArray = jsonObject.getJSONArray("data");//data数组
                    //遍历数组
                    for (int i = 0; i < dataJSONArray.length(); i++) {
                        org.json.JSONObject jsonObjectI = dataJSONArray.getJSONObject(i);
                        if (!jsonObjectI.isNull("msg")) {
                            stringBuffer.append("【" + jsonObjectI.getString("msg") + "】" + "<br>");
                            stringBuffer1.append("【" + jsonObjectI.getString("msg") + "】" + dateFormat.format(new Date()) + "<br>");
                        }
                    }
                    String message = stringBuffer.toString();
                    String message1 = stringBuffer1.toString();
                    if (StringUtils.isEmpty(message)) {
                        message = message.substring(0, message.length() - 4);
                        message1 = message1.substring(0, message1.length() - 4);
                    }
                    result.put("false", message);
                    result.put("illegalData", message1);
                } else if (jsonObject.getString("conclusion").equals(Constant.CONCLUSION_4)) {
                    result.put("false", "审核失败!");
                } else {
                    result.put("true", "审核通过!");
                }
            } else {
                result.put("false", "图片审核失败!");
            }
        }
        return result;
    }


    /**
     * 重命名图片
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/rename_image.json")
    @UserLoginToken
    public void renameImage(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("token");
        String accountId = tokenUtil.getAccountIdByToken(token);
        JSONObject jsonObject = JSON.parseObject(jsonString);
        String cacheKey = jsonObject.getString("cacheKey");

        Map cacheMap = cacheService.getValue(accountId);
        Map imageCache = (Map) cacheMap.get(cacheKey);
        byte[] bytes = (byte[]) imageCache.get(Constant.CACHE_BYTE);

        String newWholeName = imageCache.get(Constant.CACHE_NEW_NAME).toString();
        String imageName = newWholeName.substring(0, newWholeName.lastIndexOf("."));// 文件名
        String imageType = newWholeName.substring(newWholeName.lastIndexOf(".") + 1);// 文件类型
        String imagePath = getImagePath(newWholeName, accountId.toString(), imageType);
        String imageSize = imageCache.get(Constant.CACHE_SIZE).toString();
        //存储到oss
        Map<String, String> objectInfo = new HashMap<>();
        objectInfo.put(Constant.CACHE_SIZE, imageSize);
        objectInfo.put(Constant.CACHE_NEW_NAME, newWholeName);
        objectInfo.put(Constant.CACHE_ACCOUNTID, accountId.toString());

        ossUtil.putObject(bytes, objectInfo);
        //存储到mysql
        Image image = new Image();
        image.setImageId(UUIDUtils.getUUID());
        image.setImageName(imageName);
        image.setWholeName(newWholeName);
        image.setImageType(imageType);
        image.setAccountId(accountId);
        image.setImagePath(imagePath);
        image.setImageSize(imageSize);

        String imageUrl = ossUtil.getUrl(imagePath);
        if (StringUtils.isNotEmpty(imageUrl)) {
            image.setImageUrl(imageUrl.substring(0, imageUrl.lastIndexOf("?")));
        } else {
            image.setImageUrl("");
        }
        imageService.insertImage(image);
        //删除缓存
        cacheMap.remove(cacheKey);
        Json.toJson(new Result(true, "SUCCESS!"), response);
    }

    /**
     * 不重命名图片
     */
    @UserLoginToken
    @RequestMapping(value = "/no_rename_image.json")
    public void noRename(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("token");
        String accountId = tokenUtil.getAccountIdByToken(token);
        JSONObject jsonObject = JSON.parseObject(jsonString);
        String cacheKey = jsonObject.getString("cacheKey");
        //清空缓存
        Map cacheMap = cacheService.getValue(accountId.toString());
        cacheMap.remove(cacheKey);
        Json.toJson(new Result(true, "SUCCESS"), response);
    }

    /**
     * 设置图片的路径 55/images/png/2020-01-01/1.png
     *
     * @param sourceFileName
     * @param accountId
     * @return
     */
    private String getImagePath(String sourceFileName, String accountId, String type) {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString();
        return accountId + "/" + "image" + "/" + date + "/" + type + "/" + sourceFileName;
    }

    /**
     * 获取images数据
     *
     * @param request
     * @param response
     */
    @UserLoginToken
    @RequestMapping(value = "/image_list.json")
    public Object getImageList(@RequestParam(value = "page") String page,
                               @RequestParam(value = "limit") String pagesize,
                               @RequestParam(value = "token") String token,
                               @RequestParam(value = "key", defaultValue = "") String key,
                               @RequestParam(value = "isRecycle", defaultValue = "NO") String isRecycle,
                               HttpServletRequest request, HttpServletResponse response) {

        String accountId = tokenUtil.getAccountIdByToken(token);
        Condition condition = new Condition();
        condition.setAccountId(accountId);
        condition.setIsRecycle(isRecycle);
        condition.setKey(key);

        List<Image> images = imageService.findImageByCondition(condition);

        Map<String, Object> hashMap = new HashMap();
        hashMap.put("code", "0");
        hashMap.put("msg", "success");
        hashMap.put("data", images);
        return hashMap;
    }

    /**
     * 删除图片 将is_recycle 字段设置为YES，代表在回收站 NO 为默认值
     *
     * @param imageId
     * @param request
     * @param response
     */
    @UserLoginToken
    @RequestMapping(value = "/delete_image.json")
    public void deleteImage(String imageId, HttpServletRequest request, HttpServletResponse response) {

        Image image = new Image();
        image.setImageId(imageId);
        image.setIsRecycle(Constant.YES);
        imageService.updateImage(image);
        Result result = new Result(true, "删除成功");
        Json.toJson(result, response);
    }

    /**
     * 彻底删除图片
     *
     * @param response
     * @return
     */
    @UserLoginToken
    @RequestMapping(value = "/completely_remove_image.json")
    public void completelyRemoveImage(String imageId, String imagePath, HttpServletRequest request, HttpServletResponse response) {
        Result result = null;
        Image image = new Image();
        image.setImagePath(imagePath);
        image.setImageId(imageId);
        Map<Boolean, String> map = imageService.deleteImage(image);
        if (StringUtils.isNotEmpty(map.get(true))) {
            result = new Result(true, "SUCCESS");
        } else {
            result = new Result(false, "FAILURE");
        }
        Json.toJson(result, response);
    }

    /**
     * 恢复图片
     *
     * @param imageId
     * @param request
     * @param response
     */
    @RequestMapping(value = "/revert_image.json")
    @UserLoginToken
    public void revertImage(String imageId, HttpServletRequest request, HttpServletResponse response) {
        Result result = null;
        Image image = new Image();
        image.setImageId(imageId);
        image.setIsRecycle(Constant.NO);
        Map<Boolean, String> map = imageService.updateImage(image);
        if (StringUtils.isNotEmpty(map.get("true"))) {
            result = new Result(true, "SUCCESS");
        } else {
            result = new Result(false, "FAILURE");
        }
        Json.toJson(result, response);
    }


    /************************************************文档资源*************************************************************/

    /**
     * 上传文档
     *
     * @param uploadFile
     * @param request
     * @param response
     * @throws IOException
     */
    @UserLoginToken
    @RequestMapping(value = "/upload_file.json")
    public void uploadFile(@RequestParam(value = "file", required = false) MultipartFile uploadFile,
                           HttpServletRequest request, HttpServletResponse response) throws IOException {

        String token = request.getHeader("token");
        String accountId = tokenUtil.getAccountIdByToken(token);
        Map<String, String> serviceData = fileService.uploadFile(uploadFile, accountId);
        if (serviceData.get("true") != null) {
            Result result = new Result(true, "上传成功!");
            Json.toJson(result, response);
        } else {
            Map<String, String> data = new HashMap<>();
            data.put("cache", serviceData.get("fail"));
            Json.toJson(new Result(false, serviceData.get("message"), data), response);
        }
    }

    /**
     * 重命名文档
     *
     * @param request
     * @param response
     */
    @UserLoginToken
    @RequestMapping(value = "/rename_file.json")
    public void renameFile(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("token");
        String accountId = tokenUtil.getAccountIdByToken(token);
        JSONObject jsonObject = JSON.parseObject(jsonString);
        String cacheKey = jsonObject.getString("cacheKey");

        Map cacheMap = cacheService.getValue(accountId.toString());
        Map fileCache = (Map) cacheMap.get(cacheKey);

        byte[] bytes = (byte[]) fileCache.get(Constant.CACHE_BYTE);
        Map<String, String> objectInfo = new HashMap<>();

        String newWholeName = fileCache.get(Constant.CACHE_NEW_NAME).toString();
        String fileName = newWholeName.substring(0, newWholeName.lastIndexOf("."));// 文件名
        String fileType = newWholeName.substring(newWholeName.lastIndexOf(".") + 1);// 文件类型
        String filePath = getFilePath(newWholeName, accountId.toString(), fileType);
        String fileSize = fileCache.get(Constant.CACHE_SIZE).toString();

        objectInfo.put(Constant.CACHE_SIZE, fileSize);
        objectInfo.put(Constant.CACHE_NEW_NAME, newWholeName);
        objectInfo.put(Constant.CACHE_ACCOUNTID, accountId.toString());
        ossUtil.putObject(bytes, objectInfo);

        CNFile file = new CNFile();
        file.setFileId(UUIDUtils.getUUID());
        file.setFileName(fileName);
        file.setWholeName(newWholeName);
        file.setFileType(fileType);
        file.setAccountId(accountId);
        file.setFilePath(filePath);
        file.setFileSize(fileSize);

        String fileUrl = ossUtil.getUrl(filePath);
        if (StringUtils.isNotEmpty(fileUrl)) {
            file.setFileUrl(fileUrl.substring(0, fileUrl.lastIndexOf("?")));
        } else {
            file.setFileUrl("");
        }
        fileService.insertFile(file);
        //删除缓存
        cacheMap.remove(cacheKey);
        Result result = new Result(true, "SUCCESS!");
        Json.toJson(result, response);
    }


    /**
     * 不重命名文档
     *
     * @param jsonString
     * @param request
     * @param response
     */
    @UserLoginToken
    @RequestMapping(value = "/no_rename_file.json")
    public void noRenameFile(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("token");
        String accountId = tokenUtil.getAccountIdByToken(token);
        JSONObject jsonObject = JSON.parseObject(jsonString);
        String cacheKey = jsonObject.getString("cacheKey");

        //获取缓存中存储的新的文件名
        Map cacheMap = cacheService.getValue(accountId.toString());
        cacheMap.remove(cacheKey);
        Json.toJson(new Result(true, "SUCCESS"), response);
    }


    /**
     * 获取文文档路径
     *
     * @param sourceFileName
     * @param accountId
     * @return
     */
    private String getFilePath(String sourceFileName, String accountId, String type) {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        return accountId + "/" + "file" + "/" + date + "/" + type + "/" + sourceFileName;
    }

    /**
     * 获取文档列表/关键字查询
     *
     * @param page
     * @param pagesize
     * @param token
     * @param request
     * @param response
     * @return
     */
    @UserLoginToken
    @RequestMapping(value = "/file_list.json")
    public Object getFileList(@RequestParam(value = "page") String page,
                              @RequestParam(value = "limit") String pagesize,
                              @RequestParam(value = "token") String token,
                              @RequestParam(value = "key", defaultValue = "") String key,
                              @RequestParam(value = "recycle", defaultValue = "NO") String recycle,
                              HttpServletRequest request,
                              HttpServletResponse response) {

        String accountId = tokenUtil.getAccountIdByToken(token);
        Condition condition = new Condition();
        condition.setAccountId(accountId);
        condition.setIsRecycle(recycle);
        condition.setKey(key);

        List<CNFile> files = fileService.findFileList(condition);

        Map<String, Object> hashMap = new HashMap();
        hashMap.put("code", "0");
        hashMap.put("msg", "success");
        hashMap.put("data", files);
        return hashMap;
    }


    /**
     * 删除文件
     *
     * @param fileId
     * @param request
     * @param response
     */
    @UserLoginToken
    @RequestMapping(value = "/delete_file.json")
    public void deleteFile(String fileId, HttpServletRequest request, HttpServletResponse response) {

        CNFile cnFile = new CNFile();
        cnFile.setFileId(fileId);
        cnFile.setIsRecycle(Constant.YES);
        fileService.updateFile(cnFile);
        Result result = new Result(true, "删除成功");
        Json.toJson(result, response);
    }

    /**
     * 更新文件命名
     *
     * @param jsonString
     * @param request
     * @param response
     */
    @RequestMapping(value = "/update_file.json")
    public void update(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {

        JSONObject jsonObject = JSON.parseObject(jsonString);
        String fileId = jsonObject.getString("fileId");
        String fileName = jsonObject.getString("fileName");

        CNFile file = new CNFile();
        file.setFileId(fileId);
        file.setFileName(fileName);
        fileService.updateFile(file);
        Json.toJson(new Result(true, "更新成功!"), response);
    }


    /**
     * 彻底删除文件
     *
     * @param response
     * @return
     */
    @UserLoginToken
    @RequestMapping(value = "/completely_remove_file.json")
    public void completelyRemoveFile(String fileId, String filePath, HttpServletRequest request, HttpServletResponse response) {
        Result result = null;
        CNFile file = new CNFile();
        file.setFileId(fileId);
        file.setFilePath(filePath);
        Map<Boolean, String> map = fileService.deleteFile(file);
        if (StringUtils.isNotEmpty(map.get(true))) {
            result = new Result(true, "SUCCESS");
        } else {
            result = new Result(false, "FAILURE");
        }
        Json.toJson(result, response);
    }

    /**
     * 恢复图片
     *
     * @param fileId
     * @param request
     * @param response
     */
    @RequestMapping(value = "/revert_file.json")
    @UserLoginToken
    public void revertFile(String fileId, HttpServletRequest request, HttpServletResponse response) {
        Result result;
        CNFile file = new CNFile();
        file.setFileId(fileId);
        file.setIsRecycle(Constant.NO);
        Map<Boolean, String> map = fileService.updateFile(file);
        if (StringUtils.isNotEmpty(map.get(true))) {
            result = new Result(true, "SUCCESS");
        } else {
            result = new Result(false, "FAILURE");
        }
        Json.toJson(result, response);
    }
}
