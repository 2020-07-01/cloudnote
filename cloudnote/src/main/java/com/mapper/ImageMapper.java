package com.mapper;


import com.entity.Condition;
import com.entity.Image;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface ImageMapper {

    void insertImage(Image image);

    void deleteImage(Integer imageId);

    List<Image> selectImageList(Integer userId);

    void updateImage(Image image);

    String selectImageUrl(Integer imageId);

    Image selectImageByCondition(Condition condition);
}
