package com.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.entity.Condition;
import com.entity.Image;

@Repository
public interface ImageMapper {

    //存储图片
    void insertImage(Image image);

    List<Image> selectImageList(Integer accountId);

    //设置图片信息
    void updateImage(Image image);

    String selectImageUrl(Integer imageId);

    //查询文件
    Image selectImage(Image image);

    //获取图片列表
    List<Image> findImageByCondition(Condition condition);

    //删除图片
    Integer deleteImage(String imageId);

    //获取图片大小
    List<String> selectSize(Condition condition);
}
