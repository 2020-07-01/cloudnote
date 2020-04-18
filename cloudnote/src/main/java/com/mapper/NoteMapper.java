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

    // 笔记列表
    List<Note> findNoteByCondition(Condition condition);

    // 更新笔记
    int updateNote(Note note);

    //笔记标签
    List<String> selectNoteType(Note note);

    //获取笔记总数
    Integer selectCountByCondition(Condition condition);
}
