package com.service;

import java.util.List;
import java.util.Map;

import com.entity.Condition;
import com.entity.Note;

/**
 * @author :qiang
 * @date :2019/12/14 下午4:41
 * @description : 笔记信息
 * @other :
 */
public interface NoteService {

    // 保存信息
    Map insertNote(Note note);

    Map updateNote(Note note) throws Exception;

    List<Note> selectNoteByCondition(Condition condition);
}

