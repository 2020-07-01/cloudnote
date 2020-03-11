package com.service.serviceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.entity.Condition;
import com.entity.Note;
import com.mapper.NoteMapper;
import com.service.NoteService;

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
    public Map insertNote(Note note) {
        HashMap result = new HashMap();
        try {
            noteMapper.insertNote(note);
            result.put("true", "SUCCESS");
        } catch (Exception e) {
            result.put("false", "FAILURE");
        }
        return result;
    }

    @Override
    public Map updateNote(Note note) {
        Map result = new HashMap();
        try {
            noteMapper.updateNote(note);
            result.put("true", "SUCCESS");
        } catch (Exception e) {
            result.put("false", "FALIURE");
        }
        return result;
    }

    @Override
    public List<Note> selectNoteByCondition(Condition condition) {
        List<Note> notes = noteMapper.selectNoteByCondition(condition);
        return notes;
    }

}
