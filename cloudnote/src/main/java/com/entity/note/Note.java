package com.entity.note;

import lombok.Data;

/**
 * @author :qiang
 * @date :2019/12/3 下午10:10
 * @description :笔记信息类
 * @other :
 */
@Data
public class Note {

    private String noteId;
    private Integer accountId;
    private String noteType;
    private String noteTitle;
    private String noteContent;
    private String createTime;
    private String updateTime;
    private String isRecycle;
    private String star;

}

