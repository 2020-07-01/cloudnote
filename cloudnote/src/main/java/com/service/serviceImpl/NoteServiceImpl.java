package com.service.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.Util.ASEUtils;
import com.entity.Constant;
import com.entity.NoteData;
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
    public List<NoteData> selectNoteByCondition(Condition condition) {
        List<Note> notes = noteMapper.selectNoteByCondition(condition);
        List<NoteData> noteDatas = new ArrayList<>();
        notes.forEach(p->{
            NoteData noteData = new NoteData(p);
            try {
                noteData.setNoteContent(new String(ASEUtils.decrypt(p.getNoteContent(),p.getAccountId().toString().getBytes("UTF-8"))));
                noteDatas.add(noteData);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        return noteDatas;
    }

    @Override
    public List<String> selectNoteType(Note note) {
        List<String> noteTypes = noteMapper.selectNoteType(note);
        if(noteTypes != null){
            return noteTypes;
        }else {
            return null;
        }
    }

}
