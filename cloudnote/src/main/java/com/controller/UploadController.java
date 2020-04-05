package com.controller;

import com.Util.Json;
import com.Util.Result;
import com.Util.TokenUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cache.CacheService;
import com.entity.*;
import com.oss.OSSUtil;
import com.service.ImageService;
import com.service.serviceImpl.FileServiceImpl;
import com.service.serviceImpl.ImageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.DoubleAccumulator;

/**
 * @program: UploadController
 * @description:
 * @create: 2020-02-08 15:39
 **/
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


    /**
     * 上传图片
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/upload_image.json")
    public void uploadImage(@RequestParam(value = "file", required = false) MultipartFile uploadImage,
                            HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = request.getHeader("token");
        Integer accountId = tokenUtil.getAccountIdByToken(token);
        Map serviceData = imageService.uploadImage(uploadImage, accountId);
        if (serviceData.get("true") != null) {
            HashMap<String, String> data = new HashMap<>();
            data.put("message", "上传成功!");
            Result result = new Result(true, "上传成功!", data);
            Json.toJson(result, response);
        } else {
            Result result = new Result(false, serviceData.get("message").toString());
            Json.toJson(result, response);
        }
    }

    /**
     * 重命名文件
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/rename_image.json")
    public void renameImage(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("token");
        Integer accountId = tokenUtil.getAccountIdByToken(token);

        Map cacheMap = cacheService.getValue(accountId.toString());
        byte[] bytes = (byte[]) cacheMap.get(Constant.CACHE_BYTE);
        Map<String, String> objectInfo = new HashMap<>();
        objectInfo.put(Constant.CACHE_SIZE, cacheMap.get(Constant.CACHE_SIZE).toString());
        objectInfo.put(Constant.CACHE_NEW_NAME, cacheMap.get(Constant.CACHE_NEW_NAME).toString());
        objectInfo.put(Constant.CACHE_ACCOUNTID, accountId.toString());
        ossUtil.putObject(bytes, objectInfo);

        String newWholeName = cacheMap.get(Constant.CACHE_NEW_NAME).toString();
        String imageName = newWholeName.substring(0, newWholeName.lastIndexOf("."));// 文件名
        String imageType = newWholeName.substring(newWholeName.lastIndexOf(".") + 1);// 文件类型
        String imagePath = getImagePath(newWholeName, accountId.toString(), imageType);
        String imageSize = cacheMap.get(Constant.CACHE_SIZE).toString();

        Image image = new Image();
        image.setImageName(imageName);
        image.setWholeName(newWholeName);
        image.setImageType(imageType);
        image.setAccountId(accountId);
        image.setImagePath(imagePath);
        image.setImageSize(imageSize);
        imageService.insertImage(image);

        //删除缓存

        Json.toJson(new Result(true, "存储成功!"), response);
    }

    /**
     * 不重命名文件
     */
    @RequestMapping(value = "/no_rename_image.json")
    public void noReanme(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("token");
        Integer accountId = tokenUtil.getAccountIdByToken(token);
        //清空缓存
        Map cacheMap = cacheService.getValue(accountId.toString());
        cacheMap.remove(Constant.CACHE_SIZE);
        cacheMap.remove(Constant.CACHE_NEW_NAME);
        cacheMap.remove(Constant.CACHE_BYTE);
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
    @RequestMapping(value = "/image_list.json")
    public Object getImageList(@RequestParam(value = "page") String pageno,
                               @RequestParam(value = "limit") String pagesize, @RequestParam(value = "token") String token, @RequestParam(value = "key") String key,
                               HttpServletRequest request, HttpServletResponse response) {

        Integer accountId = tokenUtil.getAccountIdByToken(token);
        Condition condition = new Condition();
        condition.setAccountId(accountId);
        condition.setIsRecycle(Constant.RECYCLE_NO);
        condition.setKey(key);

        List<Image> images = imageService.getImageByCondition(condition);

        List<ImageData> imageDataList = new ArrayList<>();
        images.forEach(p -> {
            imageDataList.add(new ImageData(p));
        });

        Map<String, Object> hashMap = new HashMap();
        hashMap.put("code", "0");
        hashMap.put("msg", "success");
        hashMap.put("data", imageDataList);
        return hashMap;
    }

    /**
     * 获取图片的url
     *
     * @param jsonString
     * @param request
     * @param response
     */
    @RequestMapping(value = "/get_image_url.json")
    public void getImageUrl(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("token");
        JSONObject jsonObject = JSON.parseObject(jsonString);

        String filePath = jsonObject.getString("imagePath");
        Integer imageId = Integer.parseInt(jsonObject.getString("imageId"));

        Map ossData = ossUtil.getUrl(filePath);
        Image image = new Image();
        image.setImageId(imageId);
        String imageUrl = ossData.get("url").toString();
        image.setImageUrl(imageUrl.substring(0, imageUrl.lastIndexOf("?")));
        imageService.updateImage(image);
        Json.toJson(new Result(true, ossData.get("url").toString()), response);
    }

    /**
     * 删除图片 将is_recycle 字段设置为1，代表在回收站
     *
     * @param jsonString
     * @param request
     * @param response
     */
    @RequestMapping(value = "/delete_image.json")
    public void deleteImage(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("token");
        Integer userId = tokenUtil.getAccountIdByToken(token);
        JSONObject jsonObject = JSON.parseObject(jsonString);
        Integer imageId = Integer.parseInt(jsonObject.getString("imageId"));
        Image image = new Image();
        image.setImageId(imageId);
        imageService.updateImage(image);

        Result result = new Result(true, "删除成功");
        Json.toJson(result, response);
    }

    /************************************************文件资源*************************************************************/

    /**
     * 上传文件
     *
     * @param uploadFile
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/upload_file.json")
    public void uploadFile(@RequestParam(value = "file", required = false) MultipartFile uploadFile,
                           HttpServletRequest request, HttpServletResponse response) throws IOException {

        String token = request.getHeader("token");
        Integer accountId = tokenUtil.getAccountIdByToken(token);
        Map serviceData = fileService.uploadFile(uploadFile, accountId);
        if (serviceData.get("true") != null) {
            HashMap<String, String> data = new HashMap<>();
            data.put("message", "上传成功!");
            Result result = new Result(true, "上传成功!", data);
            Json.toJson(result, response);
        } else {
            Result result = new Result(false, serviceData.get("message").toString());
            Json.toJson(result, response);
        }
    }

    /**
     * 重命名文件
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/rename_file.json")
    public void renameFile(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("token");
        Integer accountId = tokenUtil.getAccountIdByToken(token);

        Map cacheMap = cacheService.getValue(accountId.toString());
        byte[] bytes = (byte[]) cacheMap.get(Constant.CACHE_BYTE);
        Map<String, String> objectInfo = new HashMap<>();
        objectInfo.put(Constant.CACHE_SIZE, cacheMap.get(Constant.CACHE_SIZE).toString());
        objectInfo.put(Constant.CACHE_NEW_NAME, cacheMap.get(Constant.CACHE_NEW_NAME).toString());
        objectInfo.put(Constant.CACHE_ACCOUNTID, accountId.toString());
        ossUtil.putObject(bytes, objectInfo);

        String newWholeName = cacheMap.get(Constant.CACHE_NEW_NAME).toString();
        String fileName = newWholeName.substring(0, newWholeName.lastIndexOf("."));// 文件名
        String fileType = newWholeName.substring(newWholeName.lastIndexOf(".") + 1);// 文件类型
        String filePath = getFilePath(newWholeName, accountId.toString(), fileType);
        double fileSize = (double) (cacheMap.get(Constant.CACHE_SIZE));

        CNFile file = new CNFile();
        file.setFileName(fileName);
        file.setWholeName(newWholeName);
        file.setFileType(fileType);
        file.setAccountId(accountId);
        file.setFilePath(filePath);
        file.setFileSize(fileSize);
        fileService.insertFile(file);

        //删除缓存
        Result result = new Result(true, "存储成功!");
        Json.toJson(result, response);
    }


    @RequestMapping(value = "/no_rename_file.json")
    public void noRenameFile(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("token");
        Integer accountId = tokenUtil.getAccountIdByToken(token);

        //获取缓存中存储的新的文件名
        Map cacheMap = cacheService.getValue(accountId.toString());
        cacheMap.remove(Constant.CACHE_SIZE);
        cacheMap.remove(Constant.CACHE_NEW_NAME);
        cacheMap.remove(Constant.CACHE_BYTE);
        Json.toJson(new Result(true, "SUCCESS"), response);
    }


    /**
     * 获取文件路径
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
     * 获取文件列表/关键字查询
     *
     * @param pageno
     * @param pagesize
     * @param token
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/file_list.json")
    public Object getFileList(@RequestParam(value = "page") String pageno,
                              @RequestParam(value = "limit") String pagesize, @RequestParam(value = "token") String token, @RequestParam(value = "key") String key, HttpServletRequest request, HttpServletResponse response) {

        Integer accountId = tokenUtil.getAccountIdByToken(token);
        Condition condition = new Condition();
        condition.setAccountId(accountId);
        condition.setIsRecycle(Constant.RECYCLE_NO);
        condition.setKey(key);

        List<CNFile> files = fileService.getFileList(condition);

        List<CNFileData> cnFileDataList = new ArrayList<>();
        files.forEach(p -> {
            cnFileDataList.add(new CNFileData(p));
        });

        Map<String, Object> hashMap = new HashMap();
        hashMap.put("code", "0");
        hashMap.put("msg", "success");
        hashMap.put("data", cnFileDataList);
        return hashMap;

    }

    /**
     * 获取并更新文件的url地址
     *
     * @param jsonString
     * @param request
     * @param response
     */
    @RequestMapping(value = "/get_file_url.json")
    public void getFileUrl(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("token");
        JSONObject jsonObject = JSON.parseObject(jsonString);

        String filePath = jsonObject.getString("filePath");
        Integer fileId = Integer.parseInt(jsonObject.getString("fileId"));

        Map ossData = ossUtil.getUrl(filePath);
        CNFile file = new CNFile();
        file.setFileId(fileId);
        String fileUrl = ossData.get("url").toString();
        file.setFileUrl(fileUrl.substring(0, fileUrl.lastIndexOf("?")));
        fileService.updateFile(file);
        Result result = new Result(true, ossData.get("url").toString());
        Json.toJson(result, response);
    }

    /**
     * 删除文件
     *
     * @param jsonString
     * @param request
     * @param response
     */
    @RequestMapping(value = "/delete_file.json")
    public void deleteFile(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("token");
        JSONObject jsonObject = JSON.parseObject(jsonString);
        Integer fileId = Integer.parseInt(jsonObject.getString("fileId"));
        CNFile cnFile = new CNFile();
        cnFile.setFileId(fileId);
        cnFile.setIsRecycle(Constant.RECYCLE_YES);
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
        String token = request.getHeader("token");
        JSONObject jsonObject = JSON.parseObject(jsonString);
        Integer fileId = Integer.parseInt(jsonObject.getString("fileId"));
        String fileName = jsonObject.getString("fileName");

        CNFile file = new CNFile();
        file.setFileId(fileId);
        file.setFileName(fileName);
        fileService.updateFile(file);
        Json.toJson(new Result(true, "更新成功!"), response);

    }
}
