package com.oss;

import com.Util.DateUtils;
import com.aliyun.oss.OSS;

import com.aliyun.oss.model.CopyObjectResult;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
     * 上传单个图片
     *
     * @param file
     * @param userId
     * @return
     */
    public Map putObject(MultipartFile file, String userId) {
        HashMap<String, Boolean> result = new HashMap<>();

        //获取文件名
        String fileName = file.getOriginalFilename();
        //获取文件类型
        String type = fileName.substring(fileName.lastIndexOf(".") + 1);//获取文件的后缀名
        //获取文件的新路径
        String imagePath = getImagePath(fileName, userId, type);
        PutObjectRequest putObjectRequest = null;
        try {
            putObjectRequest = new PutObjectRequest("001-bucket", imagePath, new ByteArrayInputStream(file.getBytes()));
            result.put("true", true);
        } catch (IOException e) {
            result.put("false", false);
            e.printStackTrace();
        }

        // 如果需要上传时设置存储类型与访问权限，请参考以下示例代码。
        // ObjectMetadata metadata = new ObjectMetadata();
        // metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
        // metadata.setObjectAcl(CannedAccessControlList.Private);
        // putObjectRequest.setMetadata(metadata);
        //上传图片
        PutObjectResult putResult = ossClient.putObject(putObjectRequest);
        //ossClient.shutdown();//关闭客户端
        return result;
    }


    /**
     * 存储临时文件
     *
     * @param file
     * @param userId
     * @param newImageName
     */
    public void putObjectTemp(MultipartFile file, String userId, String newImageName) {

        //获取文件名
        String wholeName = file.getOriginalFilename();
        //获取文件类型
        String type = wholeName.substring(wholeName.lastIndexOf(".") + 1);//获取文件的后缀名
        //获取文件的新路径
        String imagePath = userId + "/temp/" + newImageName;
        PutObjectRequest putObjectRequest = null;
        try {
            putObjectRequest = new PutObjectRequest("001-bucket", imagePath, new ByteArrayInputStream(file.getBytes()));
            ossClient.putObject(putObjectRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取图片的url
     *
     * @param imagePath
     * @return
     */
    public Map getUrl(String imagePath) {
        HashMap result = new HashMap();
        //设置图片url的有效期为10年
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
    public Map getImage(String wholeName) {
        HashMap result = new HashMap();

        OSSObject ossObject = ossClient.getObject("001-bucket", "ImageRepeatName/" + wholeName);
        ossObject.getObjectContent();


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
     * @param userId
     * @return
     */
    public Map copy_delete_Image(String wholeName, String userId) {
        HashMap result = new HashMap();
        //目标
        String type = wholeName.substring(wholeName.lastIndexOf(".") + 1);//获取文件的后缀名
        //目标路径
        String destinationKey = getImagePath(wholeName, userId, type);

        //源路径
        String sourceKey = userId + "/temp/" + wholeName;
        CopyObjectResult copyObjectResult = ossClient.copyObject("001-bucket", sourceKey, "001-bucket", destinationKey);

        this.deleteImage(sourceKey);
        return result;

    }


    /**
     * 获取图片路径
     * 55/images/png/2020-01-01/1.png
     *
     * @param sourceFileName
     * @param userId
     * @return
     */
    private String getImagePath(String sourceFileName, String userId, String type) {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString();
        return userId + "/" + "image" + "/" + date + "/" + type + "/" + sourceFileName;
    }



    /*    *//**
     * 上传单个文件
     *
     * @param file
     * @param userId
     * @return
     *//*
    public Map uploadFile(MultipartFile file, String userId) {
        HashMap<String, String> result = new HashMap<>();
        if (file.getSize() > 1024 * 1024 * 20) {
            result.put("false", "文件大小超过限制");
            return result;
        }
        //获取文件名
        String fileName = file.getOriginalFilename();
        //获取文件类型
        String type = fileName.substring(fileName.lastIndexOf(".") + 1);//获取文件的后缀名
        //获取文件的新路径
        String filePath = getFilePath(fileName, userId, type);
        PutObjectRequest putObjectRequest = null;
        try {
            putObjectRequest = new PutObjectRequest("001-bucket", filePath, new ByteArrayInputStream(file.getBytes()));
            result.put("true", "上传成功");
        } catch (IOException e) {
            result.put("false", "出现异常");
            e.printStackTrace();
        }

        // 如果需要上传时设置存储类型与访问权限，请参考以下示例代码。
        // ObjectMetadata metadata = new ObjectMetadata();
        // metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
        // metadata.setObjectAcl(CannedAccessControlList.Private);
        // putObjectRequest.setMetadata(metadata);
        //上传图片
        ossClient.putObject(putObjectRequest);
        // ossClient.shutdown();//关闭客户端
        return result;
    }*/


    /* *//**
     * 获取文件的路径
     *
     * @param sourceFileName
     * @param userId
     * @param type
     * @return
     *//*
    private String getFilePath(String sourceFileName, String userId, String type) {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString();
        return userId + "/" + "file" + "/" + date + "/" + type + "/" + sourceFileName;

    }*/

}
