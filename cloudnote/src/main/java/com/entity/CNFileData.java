package com.entity;

/**
 * @Description file列表返回实体
 * @Author yq
 * @Date 2020/4/5 10:44
 */
public class CNFileData {

    public CNFileData(CNFile cnFile) {
        this.fileId = cnFile.getFileId();
        this.fileUrl = cnFile.getFileUrl();
        this.wholeName = cnFile.getWholeName();
        this.showName = cnFile.getWholeName().length() > 17 ? cnFile.getWholeName().substring(0, 15)+"..." : cnFile.getWholeName();
        this.filePath = cnFile.getFilePath();
    }

    // id
    private Integer fileId;
    // url
    private String fileUrl;
    //完整的文件名
    private String wholeName;
    //展示文件名
    private String showName;
    //路径
    private String filePath;

    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
