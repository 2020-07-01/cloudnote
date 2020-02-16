package com.service.serviceImpl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.entity.Condition;
import com.entity.Image;
import com.mapper.ImageMapper;
import com.oss.OSSUtil;
import com.service.ImageService;

/**
 * @program: ResourceServiceImpl
 * @description:
 * @create: 2020-02-09 14:11
 **/
@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    ImageMapper imageMapper;

    @Autowired
    OSSUtil ossUtil;

    /**
     * 首次上传图片
     * 
     * @param file
     * @param userId
     * @return
     */
    @Override
    public Map uploadImage(MultipartFile file, Integer userId) {
        HashMap result = new HashMap();
        String wholeName = file.getOriginalFilename();// 获取源文件名
        String imageName = wholeName.substring(0, wholeName.lastIndexOf("."));// 文件名
        String imageType = wholeName.substring(wholeName.lastIndexOf(".") + 1);// 获取文件的类型
        Long imageSize = file.getSize();// 获取文件的大小 字节
        String imagePath = getImagePath(wholeName, userId.toString(), imageType);// 生成图片在oss上的目录
        // 验证是否存在重名
        Condition condition = new Condition();
        condition.setUserId(userId);
        condition.setImageName(imageName);
        condition.setIsRecycle("0");
        Image image = imageMapper.selectImageByCondition(condition);
        // 如果图片命名重复
        if (image != null) {
            // 设置终止条件
            boolean p = true;
            int i = 1;
            String newImageName = "";
            while (p) {
                newImageName = imageName + "(" + i + ")";
                Condition condition1 = new Condition();
                condition1.setUserId(userId);
                condition1.setImageName(newImageName);
                condition1.setIsRecycle("0");
                if (imageMapper.selectImageByCondition(condition1) != null) {
                    i++;
                } else {
                    p = false;
                }
            }
            // 图片的完成命名
            String wholeName1 = newImageName + "." + imageType;
            // 将图片存储到oss零时目录
            ossUtil.putObjectTemp(file, userId.toString(), wholeName1);
            result.put("false", "图片名称重复!");
            result.put("message", "已经存在重名文件，是否重命名为：" + newImageName + "." + imageType);

        } else {// 如果图片命名不重复
            Image newImage = new Image();
            newImage.setUserId(userId);
            newImage.setImageSize(imageSize);
            newImage.setImageType(imageType);
            newImage.setImagePath(imagePath);
            newImage.setImageName(imageName);
            newImage.setWholeName(wholeName);
            // 存储数据库
            imageMapper.insertImage(newImage);
            // 存储到oss
            ossUtil.putObject(file, userId.toString());
            result.put("true", "上传成功!");
        }
        return result;
    }

    @Override
    public Map deleteImage(Integer imageId) {
        HashMap result = new HashMap();
        try {
            Condition condition = new Condition();
            condition.setImageId(imageId);
            condition.setIsRecycle("1");
            imageMapper.updateIsRecycle(condition);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // result.put("true","删除成功")
        return result;
    }

    @Override
    public List<Image> selectImage(Integer userId) {

        try {
            List<Image> images = imageMapper.selectImageList(userId);
            return images;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map updateExpireDate(String expireDate) {
        return null;
    }

    @Override
    public String selecImageUrl(Integer imageId) {
        try {
            String imageUrl = imageMapper.selectImageUrl(imageId);
            return imageUrl;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 存储图片
     * 
     * @param image
     * @return
     */
    @Override
    public Map uploadImage(Image image) {
        HashMap result = new HashMap();
        imageMapper.insertImage(image);
        result.put("true", "上传成功!");
        return result;
    }

    @Override
    public List<Image> selectImageByKey(Condition condition) {
        List<Image> images = imageMapper.selectImageByKey(condition);
        return images;
    }

    /**
     * 更新图片的信息
     * 
     * @param image
     * @return
     */
    @Override
    public Map updateImage(Image image) {
        imageMapper.updateImage(image);
        return null;
    }

    /**
     * 获取图片路径 55/images/png/2020-01-01/1.png
     *
     * @param sourceFileName
     * @param userId
     * @return
     */
    private String getImagePath(String sourceFileName, String userId, String type) {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString();
        return userId + "/" + "image" + "/" + date + "/" + type + "/" + sourceFileName;
    }
}
