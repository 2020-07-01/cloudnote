package com.entity;

/**
 * @Description TODO
 * @Author yq
 * @Date 2020/4/5 20:54
 */
public class ImageData {

    public ImageData(Image image) {
        this.imageId = image.getImageId();
        this.imageUrl = image.getImageUrl();
        this.wholeName = image.getWholeName();
        this.showName = image.getWholeName().length() > 17 ? image.getWholeName().substring(0, 15)+"..." : image.getWholeName();
        this.imagePath = image.getImagePath();
    }

    // id
    private Integer imageId;
    // url
    private String imageUrl;
    //完整的图片名
    private String wholeName;
    //展示图片名
    private String showName;
    //路径
    private String imagePath;

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getWholeName() {
        return wholeName;
    }

    public void setWholeName(String wholeName) {
        this.wholeName = wholeName;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
