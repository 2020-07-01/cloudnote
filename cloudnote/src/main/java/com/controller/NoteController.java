package com.controller;

import com.Util.Json;
import com.Util.Result;
import com.Util.TokenUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.entity.Condition;
import com.entity.Note;
import com.entity.TextValue;
import com.service.serviceImpl.NoteServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    TokenUtils tokenUtil;

    /**
     * 数据列表
     *
     * @return 返回json数据
     */
    @RequestMapping(value = "/note_list")
    public Object indexList(@RequestParam(value = "token") String token,
                            @RequestParam(value = "type", defaultValue = "") String type,
                            @RequestParam(value = "isRecycle", defaultValue = "0") String isRecycle,
                            @RequestParam(value = "key", defaultValue = "") String key,
                            @RequestParam(value = "star",defaultValue = "") String star) {

        Integer accountId = tokenUtil.getAccountIdByToken(token);
        Condition condition = new Condition();
        condition.setAccountId(accountId);
        condition.setType(type);
        condition.setIsRecycle(isRecycle);
        condition.setStar(star);
        if (!key.equals("")) {
            condition.setKey(key);
            condition.setType("");
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
        Integer accountId = tokenUtil.getAccountIdByToken(token);
        JSONObject jsonObject = JSONObject.parseObject(jsonString);

        Note note = new Note();
        note.setAccountId(accountId);
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
            note.setNoteType("未分类");
        } else {
            note.setNoteType(jsonObject.getString("noteType"));
        }
        String noteId = jsonObject.getString("noteId");
        //如果此笔记存在则进行更新
        if (!noteId.equals("")) {
            note.setNoteId(Integer.parseInt(noteId));
            if (updateNote(note)) {
                Json.toJson(new Result(true, "更新成功!"), response);
                return;
            } else {
                Json.toJson(new Result(false, "更新失败!"), response);
                return;
            }
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
     * @return
     */
    private boolean updateNote(Note note) {
        try {
            noteService.updateNote(note);
            return true;
        } catch (Exception e) {

        }
        return false;
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

    /**
     * 初始化类型
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/init_noteType.json")
    public void initNoteType(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("token");
        int accountId = tokenUtil.getAccountIdByToken(token);
        Condition condition = new Condition();
        condition.setAccountId(accountId);
        condition.setIsRecycle("0");
        List<Note> notes = noteService.selectNoteByCondition(condition);
        HashMap data = new HashMap();
        ArrayList<TextValue> textValues = new ArrayList<>();
        HashMap repeatHashMap = new HashMap();
        for (Note note : notes) {
            if (repeatHashMap.get(note.getNoteType()) != null) {
                continue;
            }
            if (note.getNoteType().equals("未分类")) {
                continue;
            }
            TextValue textValue = new TextValue();
            textValue.setKey(note.getNoteType());
            textValue.setValue(note.getNoteType());
            repeatHashMap.put(note.getNoteType(), note.getNoteType());
            //此处进行去重
            textValues.add(textValue);
        }
        data.put("textValues", textValues);
        Json.toJson(new Result(true, "SUCCESS", data), response);
    }


    /**
     * 加星
     * @param jsonString
     * @param request
     * @param response
     */
    @RequestMapping(value = "/set_star.json")
    public void setStar(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {

        String token = request.getHeader("token");
        Integer accountId = tokenUtil.getAccountIdByToken(token);

        JSONObject jsonObject = JSON.parseObject(jsonString);
        String noteId = jsonObject.getString("noteId");

        Note note = new Note();
        note.setStar("1");
        note.setNoteId(Integer.parseInt(noteId));
        try {
            updateNote(note);
            Json.toJson(new Result(true, "收藏成功!"), response);
        } catch (Exception e) {
            Json.toJson(new Result(false, "收藏失败!"), response);
        }
    }

}
