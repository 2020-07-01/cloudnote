package com.service.serviceImpl;

import com.alibaba.fastjson.JSONObject;
import com.entity.Note;
import com.mapper.NoteMapper;
import com.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public int insertNote(Note note) {

        int row = noteMapper.insertNote(note);
        return 0;
    }
}
