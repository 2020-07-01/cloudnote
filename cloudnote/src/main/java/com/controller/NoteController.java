package com.controller;

import com.Util.ASEUtils;
import com.Util.Json;
import com.Util.Result;
import com.Util.TokenUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.entity.*;
import com.interceptor.UserLoginToken;
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
    @UserLoginToken
    @RequestMapping(value = "/note_list.json")
    public Object indexList(@RequestParam(value = "type", defaultValue = "") String type,
                            @RequestParam(value = "isRecycle", defaultValue = "NO") String isRecycle,
                            @RequestParam(value = "key", defaultValue = "") String key,
                            @RequestParam(value = "star", defaultValue = "") String star, HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("token");
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
        List<NoteData> noteDataList = noteService.selectNoteByCondition(condition);
        Map<String, Object> hashMap = new HashMap();
        hashMap.put("code", "0");
        hashMap.put("msg", "success");
        hashMap.put("count", noteDataList.size());
        hashMap.put("data", noteDataList);
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
    @UserLoginToken
    @RequestMapping(value = "/save_note.json")
    public void insertNote(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {
        Result result = null;
        String token = request.getHeader("token");
        Integer accountId = tokenUtil.getAccountIdByToken(token);
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        Note note = new Note();
        note.setAccountId(accountId);
        String noteId = jsonObject.getString("noteId");

        //如果此笔记存在则进行更新
        if (noteId != null) {
            note.setNoteId(Integer.parseInt(noteId));
            if (jsonObject.getString("noteTitle") != null) {
                if (!jsonObject.getString("noteTitle").trim().equals("")) {
                    note.setNoteTitle(jsonObject.getString("noteTitle").trim());
                    if (updateNote(note)) {
                        result = new Result(true, "标题更新成功!");
                    } else {
                        result = new Result(false, "标题更新失败!");
                    }
                    Json.toJson(result, response);
                    return;
                } else {
                    result = new Result(false, "标题不能为空!");
                    Json.toJson(result, response);
                    return;
                }
            }
            if (jsonObject.getString("noteContent") != null) {
                if (!jsonObject.getString("noteContent").trim().equals("")) {
                    if (jsonObject.getString("noteContent").trim().length() < 20000) {
                        try {
                            byte[] con = ASEUtils.encrypt(jsonObject.getString("noteContent").trim().getBytes("UTF-8"), accountId.toString().getBytes("UTF-8"));
                            note.setNoteContent(con);
                            if (updateNote(note)) {
                                Json.toJson(new Result(true, "内容更新成功!"), response);
                                return;
                            } else {
                                Json.toJson(new Result(false, "内容更新失败!"), response);
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Json.toJson(new Result(false, "内容的大小超过限制!最多可存储20000字"), response);
                        return;
                    }
                } else {
                    Json.toJson(new Result(false, "内容不能为空!"), response);
                    return;
                }
            }

            //类型->字数30字 前端控制
            if (jsonObject.getString("noteType") != null) {
                if (!jsonObject.getString("noteType").trim().equals("")) {
                    note.setNoteType(jsonObject.getString("noteType").trim());
                } else {
                    note.setNoteType("未分类");
                }
                if (updateNote(note)) {
                    Json.toJson(new Result(true, "类型更新成功!"), response);
                    return;
                } else {
                    Json.toJson(new Result(false, "类型更新失败!"), response);
                    return;
                }
            }
        } else {
            //标题->字数前端控制100字
            if (!jsonObject.getString("noteTitle").trim().equals("")) {
                note.setNoteTitle(jsonObject.getString("noteTitle").trim());
            } else {
                Json.toJson(new Result(false, "标题不能为空!"), response);
                return;
            }
            //内容->字数20000字
            if (!jsonObject.getString("noteContent").trim().equals("")) {
                if (jsonObject.getString("noteContent").trim().length() > 20000) {
                    Json.toJson(new Result(false, "内容的大小超过限制!最多可存储20000字"), response);
                    return;
                } else {
                    try {
                        byte[] con = ASEUtils.encrypt(jsonObject.getString("noteContent").trim().getBytes("UTF-8"), accountId.toString().getBytes("UTF-8"));
                        note.setNoteContent(con);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Json.toJson(new Result(false, "内容不能为空!"), response);
                return;
            }
            //类型->字数30字 前端控制
            if (jsonObject.getString("noteType").trim().equals("")) {
                note.setNoteType("未分类");
            } else {
                note.setNoteType(jsonObject.getString("noteType").trim());
            }
            try {
                Map resultdata = noteService.insertNote(note);
                if (resultdata.get("true") != null) {
                    Json.toJson(new Result(true, "保存成功!"), response);
                    return;
                } else {
                    Json.toJson(new Result(false, "保存失败!"), response);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Json.toJson(new Result(false, "保存失败!"), response);
    }

    /**
     * 更新笔记
     *
     * @return
     */
    private boolean updateNote(Note note) {
        try {
            if (noteService.updateNote(note)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
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
    @UserLoginToken
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
            note.setIsRecycle(Constant.RECYCLE_YES);
            if (noteService.updateNote(note)) {
                Json.toJson(new Result(true, "删除成功!"), response);
            } else {
                Json.toJson(new Result(false, "删除失败!"), response);
            }
        } catch (Exception e) {
            // 此处应该打印日志
        }
    }

    /**
     * 初始化标签类型 -> 检索为删除的
     *
     * @param request
     * @param response
     */
    @UserLoginToken
    @RequestMapping(value = "/init_noteType.json")
    public void initNoteType(HttpServletRequest request, HttpServletResponse response) {

        String token = request.getHeader("token");
        Integer accountId = tokenUtil.getAccountIdByToken(token);
        Note note = new Note();
        note.setAccountId(accountId);
        note.setIsRecycle(Constant.RECYCLE_NO);
        List<String> noteTypes = noteService.selectNoteType(note);
        HashMap data = new HashMap();
        ArrayList<TextValue> textValues = new ArrayList<>();
        for (String item : noteTypes) {
            TextValue textValue = new TextValue(item, item);
            textValues.add(textValue);
        }
        data.put("textValues", textValues);
        Json.toJson(new Result(true, "SUCCESS", data), response);
    }


    /**
     * 加星
     *
     * @param jsonString
     * @param request
     * @param response
     */
    @UserLoginToken
    @RequestMapping(value = "/set_star.json")
    public void setStar(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {

        String token = request.getHeader("token");
        Integer accountId = tokenUtil.getAccountIdByToken(token);

        JSONObject jsonObject = JSON.parseObject(jsonString);
        String noteId = jsonObject.getString("noteId");

        Note note = new Note();
        note.setStar(Constant.RECYCLE_YES);
        note.setNoteId(Integer.parseInt(noteId));
        try {
            updateNote(note);
            Json.toJson(new Result(true, "收藏成功!"), response);
        } catch (Exception e) {
            Json.toJson(new Result(false, "收藏失败!"), response);
        }
    }

}
