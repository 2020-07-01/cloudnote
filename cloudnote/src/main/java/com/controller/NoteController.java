package com.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.entity.Note;
import com.entity.ResponseStatuCode;
import com.entity.ResultUtil;
import com.service.NoteService;
import com.service.serviceImpl.NoteServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author :qiang
 * @date :2019/12/7 下午7:22
 * @description :
 * @other :
 */
@RestController
@RequestMapping(value = "/note")
public class NoteController {

    @Autowired
    NoteServiceImpl noteService;

    /**
     * 后台返回的文本格式
     * {
     * "msg":{
     * "msg": "success",
     * "code": "0",
     * "data": [
     * {
     * "title": "for循环输出",
     * "problemId": 1139
     * },
     * {
     * "title": "测试2",
     * "problemId": 1138
     * },
     * {
     * "title": "测试1",
     * "problemId": 1137
     * },
     * {
     * "title": "for循环-Plus",
     * "problemId": 1140
     * },
     * {
     * "title": "第一个C++程序",
     * "problemId": 1141
     * }
     * ]
     * } "success",
     * "code": "0",
     * "data": [
     * {
     * "title": "for循环输出",
     * "problemId": 1139
     * },
     * {
     * "title": "测试2",
     * "problemId": 1138
     * },
     * {
     * "title": "测试1",
     * "problemId": 1137
     * },
     * {
     * "title": "for循环-Plus",
     * "problemId": 1140
     * },
     * {
     * "title": "第一个C++程序",
     * "problemId": 1141
     * }
     * ]
     * }
     */


    /**
     * @param pageno
     * @param pagesize
     * @return 返回json数据
     */
    @RequestMapping(value = "/note_list")
    public String indexList(@RequestParam(value = "page") String pageno, @RequestParam(value = "limit") String pagesize) {

        //此处封装返还体
        Note note = new Note();
        note.setNoteId(1);
        note.setNoteTitle("2");
        note.setTypeId(3);
        note.setCreateTime("4");

        List<Note> list = new ArrayList<>();
        list.add(note);
        Map<String, Object> hashMap = new HashMap();
        hashMap.put("code", "0");
        hashMap.put("msg", "");
        hashMap.put("count", 1);
        hashMap.put("data", list);

        return JSON.toJSONString(hashMap);
    }


    @RequestMapping(value = "/save_note")
    public String insertNote(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {

        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        Note note = new Note();
        return "";
    }


}
