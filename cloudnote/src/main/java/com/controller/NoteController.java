package com.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Util.Json;
import com.Util.Result;
import com.alibaba.fastjson.JSONObject;
import com.entity.Condition;
import com.entity.Note;
import com.interceptorService.TokenUtil;
import com.service.serviceImpl.NoteServiceImpl;

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
     * @return 返回json数据
     */
    @RequestMapping(value = "/note_list")
    public Object indexList(@RequestParam(value = "token") String token,
        @RequestParam(value = "type", defaultValue = "") String type,
        @RequestParam(value = "isRecycle", defaultValue = "0") String isRecycle,
        @RequestParam(value = "key", defaultValue = "") String key) {

        // 根据token解析userId
        Integer userId = tokenUtil.getUserIdByToken(token);

        if (!key.equals("")) {

        }

        Condition condition = new Condition();
        condition.setUserId(userId);
        condition.setType(type);

        if (isRecycle != null) {
            condition.setIsRecycle(isRecycle);
        }

        List<Note> list = noteService.selectNoteByCondition(condition);

        Map<String, Object> hashMap = new HashMap();
        hashMap.put("code", "0");
        hashMap.put("msg", "1312");
        hashMap.put("count", list.size());
        hashMap.put("data", list);
        return hashMap;
    }

    private List<Note> search(Condition condition) {
        List<Note> list = noteService.selectNoteByCondition(condition);
        return list;
    }

    /**
     * 保存笔记
     *
     * @param jsonString
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/save_note.json")
    public void insertNote(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {
        Map result;
        String token = request.getHeader("token");
        Integer userId = tokenUtil.getUserIdByToken(token);
        JSONObject jsonObject = JSONObject.parseObject(jsonString);

        Note note = new Note();
        note.setUserId(userId);
        String noteTitle = jsonObject.getString("noteTitle");
        if (!noteTitle.equals("")) {
            note.setNoteTitle(jsonObject.getString("noteTitle"));
        } else {
            Json.toJson(new Result(false, "标题不能为空!"), response);
        }
        String noteContent = jsonObject.getString("noteContent");
        if (!noteContent.equals("")) {
            note.setNoteContent(jsonObject.getString("noteContent"));
        } else {
            Json.toJson(new Result(false, "内容不能为空!"), response);
        }
        if (jsonObject.getString("noteType").equals("")) {
            note.setNoteType("default");
        } else {
            note.setNoteType(jsonObject.getString("noteType"));
        }
        try {
            result = noteService.insertNote(note);
            if (result.get("true") != null) {
                Json.toJson(new Result(true, "保存成功!"), response);
            } else {
                Json.toJson(new Result(false, "保存失败!"), response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Json.toJson(new Result(false, "保存失败!"), response);
        }
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
        // note.setNoteType(TypeUtils.getSexEnumByCode(jsonObject.getString("noteType")));
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
    @RequestMapping(value = "/delete_note.json")
    public void deleteNote(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {
        Result result = null;
        String token = request.getHeader("token");
        try {
            tokenUtil.verifyToken(token);
        } catch (Exception e) {
            result = new Result(false, "用户验证失败，请重新登录!");
        }
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        Integer noteId = Integer.parseInt(jsonObject.getString("noteId"));

        try {
            Note note = new Note();
            note.setNoteId(noteId);
            note.setIsRecycle("1");
            Map map = noteService.updateNote(note);
            if (map.get("true") != null) {
                Json.toJson(new Result(true, "删除成功!"), response);
            } else {
                Json.toJson(new Result(false, "删除失败!"), response);
            }
        } catch (Exception e) {
            // 此处应该打印日志
        }
    }

}
