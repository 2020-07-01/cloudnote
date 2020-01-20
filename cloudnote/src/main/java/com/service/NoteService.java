package com.service;

import com.alibaba.fastjson.JSONObject;
import com.entity.Condition;
import com.entity.Note;

import java.util.List;

/**
 * @author :qiang
 * @date :2019/12/14 下午4:41
 * @description : 笔记信息
 * @other :
 */
public interface NoteService {

    //保存信息
    void insertNote(Note note);

    //根据userid查询信息
    List<Note> selectNoteByUserId(Integer userId);

    //批量删除笔记信息
    boolean deleteNotesById(List<Integer> userIdList);

    //获取单条数据
    Note selectNoteByNoteId(Integer noteId);

    int selectNoteByCondition(Condition condition);

    void updateNote(Note note);


}