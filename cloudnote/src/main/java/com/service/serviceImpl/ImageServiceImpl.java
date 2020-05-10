package com.service.serviceImpl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.Util.DateUtils;
import com.Util.UUIDUtils;
import com.cache.CacheService;
import com.entity.Constant;
import com.entity.note.Note;
import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.collections.ObservableMap;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
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

    @Autowired
    CacheService cacheService;

    /**
     * 上传单个图片
     *
     * @param file
     * @param accountId
     * @return
     */
    @Override
    public Map uploadImage(MultipartFile file, String accountId) throws IOException {
        HashMap result = new HashMap();
        String wholeName = file.getOriginalFilename();// 获取源文件名
        String imageType = wholeName.substring(wholeName.lastIndexOf(".") + 1);// 获取文件的类型
        String imageName = wholeName.substring(0, wholeName.lastIndexOf("."));// 文件名
        if(imageName.length() > 90){
            imageName = imageName.substring(0,90);
            wholeName = imageName + "."+imageType;
        }

        Long imageSize = file.getSize();// 获取文件的大小 字节
        String imagePath = getImagePath(wholeName, accountId, imageType);// 生成图片在oss上的目录
        // 验证是否存在重名
        Image imageRepeat = new Image();
        imageRepeat.setAccountId(accountId);
        imageRepeat.setImageName(imageName);
        Image imageNull = imageMapper.selectImage(imageRepeat);
        // 如果图片命名重复
        if (imageNull != null) {
            // 设置终止条件
            boolean p = true;
            int i = 1;
            String newImageName = "";
            while (p) {
                newImageName = imageName + "(" + i + ")";
                Image image = new Image();
                image.setAccountId(accountId);
                image.setAccountId(accountId);
                image.setImageName(newImageName);
                if (imageMapper.selectImage(image) != null) {
                    i++;
                } else {
                    p = false;
                }
            }
            // 图片的完成命名
            String newWholeName = newImageName + "." + imageType;

            result.put("false", "图片名称重复!");
            result.put("message", "<br><p style=\"text-align: center;font-size: 14px\">已经存在重名文件，是否重命名为:</p>" + "<br><br><p style=\"text-align: center;\">" + newImageName + "." + imageType + "</p>");

            // 将文件存储到缓存中
            Map cacheMap = cacheService.getValue(accountId);
            Map imageMap = new HashMap();

            imageMap.put(Constant.CACHE_BYTE, file.getBytes());//存储二进制文件
            imageMap.put(Constant.CACHE_NEW_NAME, newWholeName);//存储新的文件名
            imageMap.put(Constant.CACHE_SIZE, file.getSize());//存储文件大小

            cacheMap.put(newWholeName, imageMap);
            result.put("fail", newWholeName);
        } else {// 如果图片命名不重复
            // 存储到oss
            ossUtil.putObject(file, accountId);

            Image newImage = new Image();
            newImage.setImageId(UUIDUtils.getUUID());
            newImage.setAccountId(accountId);
            newImage.setImageSize(imageSize.toString());
            newImage.setImageType(imageType);
            newImage.setImagePath(imagePath);
            newImage.setImageName(imageName);
            newImage.setWholeName(wholeName);

            String imageUrl = ossUtil.getUrl(imagePath);
            if (StringUtils.isNotEmpty(imageUrl)) {
                newImage.setImageUrl(imageUrl.substring(0,imageUrl.lastIndexOf("?")));
            } else {
                newImage.setImageUrl("");
            }
            // 存储数据库
            imageMapper.insertImage(newImage);

            result.put("true", "上传成功!");
        }
        return result;
    }

    @Override
    public List<Image> selectImage(Integer accountId) {

        try {
            List<Image> images = imageMapper.selectImageList(accountId);
            return images;
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public Map insertImage(Image image) {
        HashMap result = new HashMap();
        imageMapper.insertImage(image);
        result.put("true", "上传成功!");
        return result;
    }

    @Override
    public List<Image> findImageByCondition(Condition condition) {
        List<Image> images = imageMapper.findImageByCondition(condition);
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
        Map result = new HashMap();
        imageMapper.updateImage(image);
        result.put("true", "SUCCESS");
        return result;
    }

    @Override
    public Map deleteImage(Image image) {
        Map<Boolean, String> map = new HashMap<>();
        try {
            int row = imageMapper.deleteImage(image.getImageId());
            boolean flag = ossUtil.deleteProject(image.getImagePath());
            if (flag && row == 1) {
                map.put(true, "SUCCESS");
            } else {
                map.put(false, "FAILURE");
            }
        }catch (Exception e){
            map.put(false, "FAILURE");
        }
        return map;
    }

    /**
     * 获取图片路径 55/images/png/2020-01-01/1.png
     *
     * @param sourceFileName
     * @param accountId
     * @return
     */
    private String getImagePath(String sourceFileName, String accountId, String type) {
        String date = DateUtils.CurrentDay();
        return accountId + "/" + "image" + "/" + date + "/" + type + "/" + sourceFileName;
    }


}
