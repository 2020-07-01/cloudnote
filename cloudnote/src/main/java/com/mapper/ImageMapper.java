package com.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.entity.Condition;
import com.entity.Image;

@Repository
public interface ImageMapper {

    void insertImage(Image image);

    List<Image> selectImageList(Integer accountId);

    void updateImage(Image image);

    String selectImageUrl(Integer imageId);

    Image selectImageByCondition(Condition condition);

    void updateIsRecycle(Condition condition);

    List<Image> selectImageByKey(Condition condition);
}
