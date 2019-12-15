package com.service;

import com.alibaba.fastjson.JSONObject;
import com.entity.Note;

/**
 * @author :qiang
 * @date :2019/12/14 下午4:41
 * @description : 笔记信息
 * @other :
 */
public interface NoteService {

    //保存信息
    public int insertNote(Note note);
}
