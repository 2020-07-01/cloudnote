package com.entity.admin;

import lombok.Data;

/**
 * @Description 云平台概况实体类
 * @Author yq
 * @Date 2020/4/16 22:12
 */
@Data
public class GeneralData {

    //用户总数
    private Integer accountCount;

    //活跃用户数
    private Integer aliveAccountCount;

    //笔记总数
    private Integer noteCount;

    //今日新增笔记总数
    private Integer currentDayNoteCount;

    //图片总数
    private Integer imageCount;

    //今日新增图片总数
    private Integer currentDayImageCount;

    //文档总数
    private Integer fileCount;

    //今日新增文档总数
    private Integer currentDayFileCount;

}
