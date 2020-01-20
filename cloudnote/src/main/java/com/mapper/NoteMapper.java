package com.mapper;

import com.entity.Condition;
import com.entity.Note;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

/**
 * @author :qiang
 * @date :2019/12/14 下午4:45
 * @description :
 * @other :
 */
@Repository
public interface NoteMapper {

    //保存笔记信息
    int insertNote(Note note);

    //查询所有笔记
    List<Note> selectNoteListByUserId(Integer userId);

    //根据类别查询笔记
    List<Note> selectNoteByType(Integer typeId);

    //删除笔记信息
    int deleteNoteById(Integer userId);

    //批量删除笔记信息
    int deleteNotesById(List<Integer> userIdList);

    //获取单条数据
    Note selectNoteByNoteId(Integer noteId);

    //设置笔记删除状态
    int updateRecycle(Integer noteId);

    int selectNoteByCondition(Condition condition);

    int updateNote(Note note);


}
