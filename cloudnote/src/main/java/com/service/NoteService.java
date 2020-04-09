package com.service;

import java.util.List;
import java.util.Map;

import com.entity.Condition;
import com.entity.Note;
import com.entity.NoteData;

/**
 * @author :qiang
 * @date :2019/12/14 下午4:41
 * @description : 笔记信息
 * @other :
 */
public interface NoteService {

    // 保存信息
    Map insertNote(Note note);
    // 更新笔记
    Map updateNote(Note note) throws Exception;
    // 笔记列表
    List<NoteData> selectNoteByCondition(Condition condition);
    // 笔记标签
    List<String> selectNoteType(Note note);
}

