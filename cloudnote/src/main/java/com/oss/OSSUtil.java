package com.oss;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
        String type = wholeName.substring(wholeName.lastIndexOf(".") + 1);// 获取文件的后缀名
        String imagePath = getImagePath(wholeName, accountId, type);
        PutObjectRequest putObjectRequest = null;
        try {
            putObjectRequest = new PutObjectRequest("001-bucket", imagePath, new ByteArrayInputStream(file.getBytes()));
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
     * 存储临时文件
     *
     * @param file
     * @param accountId
     * @param newImageName
     */
    public void putObjectTemp(MultipartFile file, String accountId, String newImageName) {

        // 获取文件名
        String wholeName = file.getOriginalFilename();
        // 获取文件类型
        String type = wholeName.substring(wholeName.lastIndexOf(".") + 1);// 获取文件的后缀名
        // 获取文件的新路径
        String imagePath = accountId + "/temp/" + newImageName;
        PutObjectRequest putObjectRequest = null;
        try {
            putObjectRequest = new PutObjectRequest("001-bucket", imagePath, new ByteArrayInputStream(file.getBytes()));
            ossClient.putObject(putObjectRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取图片/文件的url
     *
     * @param imagePath
     * @return
     */
    public Map getUrl(String imagePath) {
        HashMap result = new HashMap();
        // 设置图片url的有效期为10年
        Date date = new Date(new Date().getTime() + 36000 * 1000 * 24 * 365 * 10);
        URL url = ossClient.generatePresignedUrl("001-bucket", imagePath, date);
        String urlString = url.toString();
        result.put("url", urlString);
        String expireDate = DateUtils.parse(date);
        result.put("expireDate", expireDate);
        return result;
    }

    /**
     * 流式下载图片
     *
     * @param wholeName
     * @return
     */
    public Map downImage(String wholeName) {
        HashMap result = new HashMap();
        GetObjectRequest getObjectRequest = new GetObjectRequest("001-bucket", wholeName);
        File file = new File("D:\\毕业设计");
        ossClient.getObject(getObjectRequest, file);
        return result;
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
        return accountId + "/" + "file" + "/" + date + "/" + type + "/" + sourceFileName;
    }




}
