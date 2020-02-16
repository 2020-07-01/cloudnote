package com.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.entity.Condition;
import com.entity.Image;

public interface ImageService {

    // 首次上传图片
    Map uploadImage(MultipartFile file, Integer userId);

    // 删除图片
    Map deleteImage(Integer imageId);

    // 获取image数据
    List<Image> selectImage(Integer userId);

    // 设置url过期时间
    Map updateExpireDate(String expireDate);

    String selecImageUrl(Integer imageId);

    // 二次上传图片
    Map uploadImage(Image image);

    // 根据key搜索图片
    List<Image> selectImageByKey(Condition condition);

    //
    Map updateImage(Image image);
}
