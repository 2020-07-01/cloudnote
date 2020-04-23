package com.entity;

import lombok.Data;

/**
 * @Description
 * @Author yq
 * @Date 2020/4/5 20:54
 */
@Data
public class ImageData {

    public ImageData(Image image) {
        this.imageId = image.getImageId();
        this.imageUrl = image.getImageUrl();
        this.wholeName = image.getWholeName();
        this.showName = image.getWholeName().length() > 17 ? image.getWholeName().substring(0, 15)+"..." : image.getWholeName();
        this.imagePath = image.getImagePath();
    }

    // id
    private String imageId;
    // url
    private String imageUrl;
    //完整的图片名
    private String wholeName;
    //展示图片名
    private String showName;
    //路径
    private String imagePath;


}
