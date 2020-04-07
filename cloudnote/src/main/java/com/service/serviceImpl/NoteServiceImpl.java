package com.service.serviceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.Util.ASEUtils;
import com.entity.Constant;
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
            //内容进行加密
            note.setNoteContent(ASEUtils.encrypt(note.getNoteContent().getBytes(), note.getAccountId().toString().getBytes()).toString());
            int row = noteMapper.insertNote(note);
            if (row == 1) {
                result.put("true", Constant.SUCCESS);
            } else {
                result.put("false", Constant.FAILURE);
            }
        } catch (Exception e) {
            e.getMessage();
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Map updateNote(Note note) throws Exception {
        Map result = new HashMap();
        //内容加密
        note.setNoteContent(ASEUtils.decrypt(note.getNoteContent().getBytes(), note.getAccountId().toString().getBytes()).toString());
        try {
            int row = noteMapper.updateNote(note);
            if (row == 1) {
                result.put("true", "SUCCESS");
            } else {
                result.put("false", "FAILURE");
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return result;
    }

    @Override
    public List<Note> selectNoteByCondition(Condition condition) {
        List<Note> notes = noteMapper.selectNoteByCondition(condition);
        return notes;
    }

}
