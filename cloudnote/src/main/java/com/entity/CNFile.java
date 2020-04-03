package com.entity;

/**
 * @Description 文件实体类
 * @Author yq
 * @Date 2020/4/3 13:20
 */
public class CNFile {

    // id
    private Integer imageId;
    // accountId
    private Integer accountId;
    // 名称
    private String fileName;
    // 类型
    private String fileType;
    // 地址
    private String filePath;
    // 大小
    private Long fileSize;
    // 创建日期
    private String createTime;
    // url
    private String fileUrl;
    //完整的文件名
    private String wholeName;
    // 是否已经删除
    private String isRecycle;

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
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

    public String getIsRecycle() {
        return isRecycle;
    }

    public void setIsRecycle(String isRecycle) {
        this.isRecycle = isRecycle;
    }
}
