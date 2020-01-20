package com.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.entity.Condition;
import com.entity.Note;
import com.entity.TypeUtils;
import com.interceptorService.TokenUtil;
import com.resultUtil.Json;
import com.resultUtil.Result;
import com.service.serviceImpl.NoteServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    TokenUtil tokenUtil;

    /**
     * 数据列表
     *
     * @param pageno
     * @param pagesize
     * @return 返回json数据
     */
    @RequestMapping(value = "/note_list")
    public Object indexList(@RequestParam(value = "page") String pageno, @RequestParam(value = "limit") String pagesize, @RequestParam(value = "token") String token) {

        //根据token解析userId
        Integer userId = tokenUtil.getUserIdByToken(token);
        List<Note> list = noteService.selectNoteByUserId(userId);

        Map<String, Object> hashMap = new HashMap();
        hashMap.put("code", "0");
        hashMap.put("msg", "1312");
        hashMap.put("count", list.size());
        hashMap.put("data", list);
        return hashMap;
    }

    /**
     * 保存笔记
     *
     * @param jsonString
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/save_note")
    public void insertNote(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {
        Result result = null;
        String token = request.getHeader("token");
        Integer userId = tokenUtil.getUserIdByToken(token);
        JSONObject jsonObject = JSONObject.parseObject(jsonString);

        Note note = new Note();
        note.setUserId(userId);
        note.setNoteTitle(jsonObject.getString("noteTitle"));
        note.setNoteContent(jsonObject.getString("noteContent"));
        note.setTypeId(Integer.parseInt(jsonObject.getString("noteType")));
        try {
            noteService.insertNote(note);
        } catch (Exception e) {
            result = new Result(false, "保存失败!");
            Json.toJson(result, response);
        }
        result = new Result(true, "保存成功!");
        Json.toJson(result, response);
    }

    /**
     * 查看笔记
     *
     * @param jsonString
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/check_note")
    public void checkNote(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {


    }


    /**
     * 更新笔记
     *
     * @param jsonString
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/update_note")
    public void updateNote(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {
        Result result = null;
        String token = request.getHeader("token");
        Integer userId = tokenUtil.getUserIdByToken(token);
        JSONObject jsonObject = JSONObject.parseObject(jsonString);

        Note note = new Note();
        note.setUserId(userId);
        note.setNoteTitle(jsonObject.getString("noteTitle"));
        note.setNoteContent(jsonObject.getString("noteContent"));
        note.setTypeId(TypeUtils.getSexEnumByCode(jsonObject.getString("noteType")));
        noteService.updateNote(note);
        result = new Result(true, "更新成功!");
        Json.toJson(result, response);
    }


    /**
     * 删除笔记
     *
     * @param jsonString
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/delete_note")
    public void deleteNote(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {
        Result result = null;
        String token = request.getHeader("token");
        try {
            tokenUtil.verifyToken(token);
        } catch (Exception e) {
            result = new Result(false, "用户验证失败，请重新登录!");
        }
        try {
            JSONArray jsonArray = JSONArray.parseArray(jsonString);
            List<Integer> listNoteId = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                String temp = jsonArray.getString(i);
                JSONObject jsonObject = JSONArray.parseObject(temp);
                Integer userId = Integer.parseInt(jsonObject.getString("noteId"));
                listNoteId.add(userId);
            }

            boolean p = noteService.deleteNotesById(listNoteId);
            if (p) {
                result = new Result(false, "删除成功!");
            } else {
                result = new Result(true, "删除失败!");
            }
        } catch (Exception e) {
            //此处应该打印日志
        }
        Json.toJson(result, response);
    }


    /**
     * 关键字检索
     *
     * @param jsonString
     * @param request
     * @param response
     */
    public void selectByKey(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {
        Result result = null;
        String token = request.getHeader("token");
        try {
            tokenUtil.verifyToken(token);
        } catch (Exception e) {
            result = new Result(false, "用户验证失败，请重新登录!");
        }
        try {
            JSONArray jsonArray = JSONArray.parseArray(jsonString);
            Condition condition = new Condition();

            noteService.selectNoteByCondition(condition);

        } catch (Exception e) {

        }
    }

}
