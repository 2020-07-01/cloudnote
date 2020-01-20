package com.service.serviceImpl;

import com.entity.Condition;
import com.entity.Note;
import com.mapper.NoteMapper;
import com.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author :qiang
 * @date :2019/12/14 下午4:43
 * @description :
 * @other : 进行逻辑处理层
 */

@Service
public class NoteServiceImpl implements NoteService {

    @Autowired
    NoteMapper noteMapper;

    @Override
    public void insertNote(Note note) {
        noteMapper.insertNote(note);

    }

    @Override
    public List<Note> selectNoteByUserId(Integer userId) {
        List<Note> listNote = noteMapper.selectNoteListByUserId(userId);
        return listNote;
    }


    @Override
    public boolean deleteNotesById(List<Integer> userIdList) {
        try {
            noteMapper.deleteNotesById(userIdList);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Note selectNoteByNoteId(Integer noteId) {
        return noteMapper.selectNoteByNoteId(noteId);
    }


    @Override
    public int selectNoteByCondition(Condition condition) {
        return noteMapper.selectNoteByCondition(condition);
    }

    @Override
    public void updateNote(Note note) {
        noteMapper.updateNote(note);
    }

    public boolean updateRecycle(Integer noteId) {
        int row;
        try {
            row = noteMapper.updateRecycle(noteId);
        } catch (Exception e) {
            return false;
        }
        return (row == 1) ? true : false;
    }


}
