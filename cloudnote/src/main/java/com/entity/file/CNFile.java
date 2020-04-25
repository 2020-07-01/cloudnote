package com.entity.file;

import lombok.Data;

/**
 * @Description 文件实体类
 * @Author yq
 * @Date 2020/4/3 13:20
 */
@Data
public class CNFile {

    // id
    private String fileId;
    // accountId
    private String accountId;
    // 名称
    private String fileName;
    // 类型
    private String fileType;
    // 地址
    private String filePath;
    // 大小
    private String fileSize;
    // 创建日期
    private String createTime;
    // url
    private String fileUrl;
    //完整的文件名
    private String wholeName;
    // 是否已经删除
    private String isRecycle;
    //更新时间
    private String updateTime;


}
