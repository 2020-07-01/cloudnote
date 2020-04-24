package com.entity;

import lombok.Data;

/**
 * @program: Image
 * @description: 图片资源实体类
 * @create: 2020-02-09 14:06
 **/
@Data
public class Image {

    // id
    private String imageId;
    // accountId
    private String accountId;
    // 名称
    private String imageName;
    // 类型
    private String imageType;
    // 地址
    private String imagePath;
    // 大小
    private String imageSize;
    // 创建日期
    private String createTime;
    // url
    private String imageUrl;
    //完整的文件名
    private String wholeName;
    // 是否已经删除
    private String isRecycle;
    //更新时间
    private String updateTime;


}
