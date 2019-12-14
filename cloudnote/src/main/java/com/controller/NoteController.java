package com.controller;

import com.alibaba.fastjson.JSONObject;
import com.entity.Note;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
@Controller
@RequestMapping(value = "/note")
public class NoteController {


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


    @ResponseBody
    @RequestMapping(value = "note_list")
    public Map<String, Object> indexList(@RequestParam(value = "page") String pageno, @RequestParam(value = "limit") String pagesize) {


        Note note = new Note();
        note.setId("1");
        note.setNoteTitle("2");
        note.setType("3");
        note.setCreateTime("4");

        List<Note> list = new ArrayList<>();
        list.add(note);
        Map<String,Object> hashMap = new HashMap();
        hashMap.put("code", "0");
        hashMap.put("msg", "");
        hashMap.put("count", 1);
        hashMap.put("data", list);


        return hashMap;
    }

}
