package com.service;


import com.entity.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ImageService {

    //首次上传图片
    Map uploadImage(MultipartFile file,Integer userId);

    //删除图片
    Map deleteImage(Integer imageId);

    //获取image数据
    List<Image> selectImage(Integer userId);

    //设置url过期时间
    Map updateExpireDate(String expireDate);

    String selecImageUrl(Integer imageId);

    //二次上传图片
    Map uploadImage(Image image);
}
