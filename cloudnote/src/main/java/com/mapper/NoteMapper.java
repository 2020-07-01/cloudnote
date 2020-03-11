package com.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.entity.Condition;
import com.entity.Note;

/**
 * @author :qiang
 * @date :2019/12/14 下午4:45
 * @description :
 * @other :
 */
@Repository
public interface NoteMapper {

    // 保存笔记信息
    int insertNote(Note note);

    //
    List<Note> selectNoteByCondition(Condition condition);

    // 更新笔记
    int updateNote(Note note);

}
