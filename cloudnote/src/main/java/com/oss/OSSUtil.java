package com.oss;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.aliyun.oss.model.*;
import com.entity.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.Util.DateUtils;
import com.aliyun.oss.OSS;

/**
 * @program: OSSUtil
 * @description:
 * @create: 2020-02-03 22:25
 **/
@Component
@Slf4j
public class OSSUtil {

    public static HashMap<String, String> typeMap = new HashMap();

    static {
        typeMap.put("doc", "file");
        typeMap.put("docx", "file");
        typeMap.put("pdf", "file");
        typeMap.put("txt", "file");
        typeMap.put("xlsx", "file");
        typeMap.put("png", "image");
        typeMap.put("jpg", "image");
        typeMap.put("jpeg","image");
        typeMap.put("jfif","image");
    }

    @Autowired
    private OSS ossClient;

    /**
     * 上传单个图片/文件
     *
     * @param file
     * @param accountId
     * @return
     */
    public Map putObject(MultipartFile file, String accountId) {
        HashMap<String, Boolean> result = new HashMap<>();
        String wholeName = file.getOriginalFilename();
        String type = wholeName.substring(wholeName.lastIndexOf(".") + 1);// 获取文件的后缀
        //判断是否需要进行文件名压缩
        String fileName = wholeName.substring(0, wholeName.lastIndexOf("."));// 文件名
        if(fileName.length() > 90){
            fileName = fileName.substring(0,90);
            wholeName = fileName + "."+type;
        }

        String typeSwitch = typeMap.get(type);
        String path = "";
        switch (typeSwitch) {
            case "file":
                path = getFilePath(wholeName, accountId, type);
                break;
            case "image":
                path = getImagePath(wholeName, accountId, type);
                break;
        }
        PutObjectRequest putObjectRequest = null;
        try {
            putObjectRequest = new PutObjectRequest("001-bucket", path, new ByteArrayInputStream(file.getBytes()));
            PutObjectResult putResult = ossClient.putObject(putObjectRequest);
            result.put("true", true);
        } catch (IOException e) {
            result.put("false", false);
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 存储单个图片/文件
     *
     * @param bytes
     * @param objectInfo
     * @return
     */
    public Map putObject(byte[] bytes, Map<String, String> objectInfo) {
        HashMap<String, Boolean> result = new HashMap<>();

        String wholeName = objectInfo.get(Constant.CACHE_NEW_NAME);
        String accountId = objectInfo.get("accountId");
        String type = wholeName.substring(wholeName.lastIndexOf(".") + 1);// 获取文件的后缀
        String typeSwitch = typeMap.get(type);
        String path = "";
        switch (typeSwitch) {
            case "file":
                path = getFilePath(wholeName, accountId, type);
                break;
            case "image":
                path = getImagePath(wholeName, accountId, type);
                break;
        }
        PutObjectRequest putObjectRequest = null;

        putObjectRequest = new PutObjectRequest("001-bucket", path, new ByteArrayInputStream(bytes));
        PutObjectResult putResult = ossClient.putObject(putObjectRequest);
        result.put("true", true);
        return result;
    }


    /**
     * 存储的头像
     *
     * @param headImage
     * @param path
     * @return
     */
    public Map putObject(byte[] headImage, String path) {

        Map response = new HashMap();
        PutObjectRequest putObjectRequest = new PutObjectRequest("001-bucket", path, new ByteArrayInputStream(headImage));
        PutObjectResult putResult = ossClient.putObject(putObjectRequest);
        String url = this.getUrl(path);
        response.put(Constant.FILE_IMAGE_URL, url);
        //ossClient.shutdown();//关闭客户端
        return response;
    }


    /**
     * 获取图片/文件的url
     *
     * @param path
     * @return
     */
    public String getUrl(String path) {
        HashMap result = new HashMap();
        // 设置图片url的有效期为10年
        Date date = new Date(new Date().getTime() + 36000 * 1000 * 24 * 365 * 10);
        URL url = ossClient.generatePresignedUrl("001-bucket", path, date);
        String urlString = url.toString();
        return urlString;
    }


    /**
     * 删除文件
     *
     * @param path
     * @return
     */
    public boolean deleteProject(String path) {
        boolean flag;
        try {
            ossClient.deleteObject("001-bucket", path);
            flag = true;
        } catch (Exception e) {
            flag = false;
            log.error(e.getMessage(), new Throwable(e));
        }
        return flag;
    }


    /**
     * 获取图片路径 55/images/png/2020-01-01/1.png
     *
     * @param sourceFileName
     * @param accountId
     * @return
     */
    private String getImagePath(String sourceFileName, String accountId, String type) {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        return accountId + "/" + "image" + "/" + date + "/" + type + "/" + sourceFileName;
    }

    /**
     * 获取文件的路径
     *
     * @param sourceFileName
     * @param accountId
     * @param type
     * @return
     */
    private String getFilePath(String sourceFileName, String accountId, String type) {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        return accountId + "/" + "file" + "/" + date + "/" + type + "/" + sourceFileName;
    }

/*
    public OSSObject getObject() {
        OSSObject ossObject = ossClient.getObject("001-bucket", "63/file/2020-04-23/doc/论文.doc");
        return ossObject;
    }*/
}
