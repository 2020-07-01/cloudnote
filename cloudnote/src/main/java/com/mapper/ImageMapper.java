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

    void updateImage(Image image);

    String selectImageUrl(Integer imageId);

    void updateIsRecycle(Condition condition);

    //查询文件
    Image selectImage(Image image);

    //获取图片列表
    List<Image> selectImageByCondition(Condition condition);
}
