package com.oss;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.entity.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.Util.DateUtils;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.CopyObjectResult;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;

/**
 * @program: OSSUtil
 * @description:
 * @create: 2020-02-03 22:25
 **/
@Component
public class OSSUtil {

    public static HashMap<String, String> typeMap = new HashMap();

    static {
        typeMap.put("doc", "file");
        typeMap.put("pdf", "file");
        typeMap.put("txt", "file");
        typeMap.put("xlsx", "file");
        typeMap.put("png", "image");
        typeMap.put("jpg", "image");
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
        //ossClient.shutdown();//关闭客户端
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
        //ossClient.shutdown();//关闭客户端
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
     * @param key
     * @return
     */
    public Map deleteImage(String key) {
        HashMap result = new HashMap();
        ossClient.deleteObject("001-bucket", key);
        return result;
    }

    /**
     * 复制源文件到目标文件，成功后删除源文件
     *
     * @param wholeName
     * @param accountId
     * @return
     */
    public Map copy_delete_Image(String wholeName, String accountId) {
        HashMap result = new HashMap();
        // 目标
        String type = wholeName.substring(wholeName.lastIndexOf(".") + 1);// 获取文件的后缀名
        // 目标路径
        String destinationKey = getImagePath(wholeName, accountId, type);
        // 源路径
        String sourceKey = accountId + "/temp/" + wholeName;
        CopyObjectResult copyObjectResult = ossClient.copyObject("001-bucket", sourceKey, "001-bucket", destinationKey);

        this.deleteImage(sourceKey);
        return result;

    }

    /**
     * 获取图片路径 55/images/png/2020-01-01/1.png
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
     * 获取文件的路径
     *
     * @param sourceFileName
     * @param accountId
     * @param type
     * @return
     */
    private String getFilePath(String sourceFileName, String accountId, String type) {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString();
        return accountId + "/" + "file" + "/" + date + "/" + type + "/" + sourceFileName;
    }


}
