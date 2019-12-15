package com.mapper;

import com.entity.Note;
import org.springframework.stereotype.Repository;

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
    List<Note> selectNoteList(Integer userId);

    //根据类别查询笔记
    List<Note> selectNoteByType(Integer typeId);


}
