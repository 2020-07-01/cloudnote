package com.controller;

import com.Util.ASEUtils;
import com.Util.Json;
import com.Util.Result;
import com.Util.TokenUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.entity.*;
import com.entity.note.Note;
import com.interceptor.UserLoginToken;
import com.service.serviceImpl.NoteServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
@Slf4j
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
    public Object indexList(@RequestParam(value = "page", defaultValue = "1") String page,
                            @RequestParam(value = "pageSize", defaultValue = "10") String pageSize,
                            @RequestParam(value = "type", defaultValue = "") String type,
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
        condition.setStartNumber(getStartNumber(Integer.parseInt(page), Integer.parseInt(pageSize)));
        condition.setPageSize(Integer.parseInt(pageSize));
        condition.setKey(key);

        List<Note> noteList = noteService.findNoteByCondition(condition);
        Integer count = noteService.selectCountByCondition(condition);
        Map<String, Object> hashMap = new HashMap();
        hashMap.put("code", "0");
        hashMap.put("msg", "success");
        hashMap.put("count", count);
        hashMap.put("data", noteList);
        return hashMap;
    }

    //获取分页数据
    private Integer getStartNumber(Integer page, Integer pageSize) {
        return page == 1 ? 0 : ((page - 1) * pageSize);
    }

    /**
     * 保存笔记
     *
     * @param noteVo
     * @param request
     * @param response
     * @return
     */
    @UserLoginToken
    @RequestMapping(value = "/save_note.json")
    public void insertNote(@RequestBody Note noteVo, HttpServletRequest request, HttpServletResponse response) {

        Result result = null;
        String token = request.getHeader("token");
        Integer accountId = tokenUtil.getAccountIdByToken(token);
        noteVo.setAccountId(accountId);

        Map<Boolean, String> map = noteService.insertNote(noteVo);//存储笔记
        if (StringUtils.isNotEmpty(map.get(true))) {
            result = new Result(true, map.get(true));
        } else {
            result = new Result(false, map.get(false));
        }
        Json.toJson(result, response);
    }

    /**
     * 更新笔记
     *
     * @param noteVo
     * @param request
     * @param response
     * @return
     */
    @UserLoginToken
    @RequestMapping(value = "/update_note.json")
    public void updateNote(@RequestBody Note noteVo, HttpServletRequest request, HttpServletResponse response) {
        Result result = null;
        String token = request.getHeader("token");
        Integer accountId = tokenUtil.getAccountIdByToken(token);
        noteVo.setAccountId(accountId);
        Map<Boolean, String> map = noteService.updateNote(noteVo);
        if (StringUtils.isNotEmpty(map.get(true))) {
            result = new Result(true, map.get(true));
        } else {
            result = new Result(false, map.get(false));
        }
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
    @UserLoginToken
    @RequestMapping(value = "/delete_note.json")
    public void deleteNote(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {
        Result result = null;
        String token = request.getHeader("token");

        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        String noteId = jsonObject.getString("noteId");
        Note note = new Note();
        note.setNoteId(noteId);
        note.setIsRecycle(Constant.YES);
        Map<Boolean, String> map = noteService.updateNote(note);
        if (StringUtils.isNotEmpty(map.get(true))) {
            result = new Result(true, "SUCCESS");
        } else {
            result = new Result(false, map.get(false));
        }
        Json.toJson(result, response);

    }

    /**
     * 初始化标签类型 -> 检索为删除的
     *
     * @param request
     * @param response
     */
    @UserLoginToken
    @RequestMapping(value = "/init_noteType.json")
    public void initNoteType(@RequestParam(value = "star", defaultValue = "") String star,
                             @RequestParam(value = "recycle", defaultValue = "") String recycle,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        String token = request.getHeader("token");
        Integer accountId = tokenUtil.getAccountIdByToken(token);
        Note note = new Note();
        note.setAccountId(accountId);
        //是否删除
        note.setIsRecycle(recycle);
        note.setStar(star);

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
     * 收藏/取消收藏
     *
     * @param noteId
     * @param star
     * @param request
     * @param response
     */
    @UserLoginToken
    @RequestMapping(value = "/set_star.json")
    public void setStar(String noteId, String star, HttpServletRequest request, HttpServletResponse response) {
        Result result = null;
        Note note = new Note();
        if (StringUtils.isNotEmpty(noteId)) {
            note.setNoteId(noteId);
        }
        if (StringUtils.isNotEmpty(star)) {
            note.setStar(star);
        }
        try {
            Map<Boolean, String> map = noteService.updateNote(note);
            if (StringUtils.isNotEmpty(map.get(true))) {
                result = new Result(true, "SUCCESS!");
            } else {
                result = new Result(false, "FAILURE!");
            }
        } catch (Exception e) {
            result = new Result(false, "FAILURE!");
            log.info("set_star.json" + "FAILURE", new Throwable(e));
        }
        Json.toJson(result, response);
    }

    /**
     * 恢复笔记
     *
     * @param response
     * @return
     */
    @UserLoginToken
    @RequestMapping(value = "/revert_note.json")
    public void revertNote(String noteId, HttpServletRequest request, HttpServletResponse response) {
        Result result;
        Note note = new Note();
        note.setNoteId(noteId);
        note.setIsRecycle(Constant.NO);
        Map<Boolean, String> map = noteService.updateNote(note);
        if (StringUtils.isNotEmpty(map.get(true))) {
            result = new Result(true, "SUCCESS");
        } else {
            result = new Result(false, map.get(false));
        }
        Json.toJson(result, response);
    }


    /**
     * 彻底删除笔记
     *
     * @param response
     * @return
     */
    @UserLoginToken
    @RequestMapping(value = "/completely_remove_note.json")
    public void completelyRemoveNote(String noteId, HttpServletRequest request, HttpServletResponse response) {
        Result result = null;
        noteService.deleteNoteByNoteId(noteId);
        result = new Result(true, "SUCCESS");
        Json.toJson(result, response);
    }

}
