package com.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.entity.note.Note;
import org.springframework.web.multipart.MultipartFile;

import com.entity.Condition;
import com.entity.Image;

public interface ImageService {

    // 上传图片
    Map uploadImage(MultipartFile file, String accountId) throws IOException;

    // 获取image数据
    List<Image> selectImage(Integer userId);

    String selecImageUrl(Integer imageId);

    // 二次上传图片
    Map insertImage(Image image);

    // 根据key搜索图片
    List<Image> findImageByCondition(Condition condition);

    //更新图片
    Map updateImage(Image image);

    //删除图片
    Map deleteImage(Image image);
}
