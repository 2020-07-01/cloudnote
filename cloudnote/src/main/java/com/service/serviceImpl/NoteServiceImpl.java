package com.service.serviceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.Util.ASEUtils;
import com.Util.UUIDUtils;
import com.baidu.BaiDuUtils;
import com.entity.Constant;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.entity.Condition;
import com.entity.note.Note;
import com.mapper.NoteMapper;
import com.service.NoteService;
import sun.misc.BASE64Encoder;

/**
 * @author :qiang
 * @date :2019/12/14 下午4:43
 * @description :
 * @other : 进行逻辑处理层
 */
@Slf4j
@Service
public class NoteServiceImpl implements NoteService {

    @Autowired
    NoteMapper noteMapper;

    @Autowired
    BaiDuUtils baiDuUtils;

    @Autowired
    ASEUtils aseUtils;

    /**
     * 存储笔记
     *
     * @param noteVo
     * @return
     */
    @Override
    public Map insertNote(Note noteVo) {

        Map<Object, String> result = new HashMap();
        String noteId = UUIDUtils.getUUID();
        noteVo.setNoteId(noteId);
        try {
            if (StringUtils.isBlank(noteVo.getNoteTitle())) {
                result.put(false, "标题不能为空!");
            } else if (StringUtils.isBlank(noteVo.getNoteContent())) {
                result.put(false, "内容不能为空!");
            } else if (noteVo.getNoteContent().trim().length() > 60000) {
                log.info("文本长度超过限制!" + "length:" + noteVo.getNoteContent().trim().length());
                result.put(false, "内容长度超过限制!");
            } else {
                Map<Boolean, String> map = check(noteVo);
                //true
                if (StringUtils.isNotEmpty(map.get(true))) {
                    //进行加密
                    String noteContent = aseUtils.encrypt(noteVo.getNoteContent().trim().getBytes("UTF-8"), noteVo.getAccountId().toString().getBytes("UTF-8"));
                    log.info("加密前的长度:"+noteVo.getNoteContent().trim().length());
                    log.info("加密后的长度:"+noteContent.length());
                    noteVo.setNoteContent(noteContent);

                    //设置type
                    if (StringUtils.isBlank(noteVo.getNoteType())) {
                        noteVo.setNoteType("未分类");
                    }
                    if (noteMapper.insertNote(noteVo) == 1) {
                        result.put(true, "SUCCESS");
                    } else {
                        result.put(false, "FAILURE");
                    }
                } else {
                    result.put(false, map.get(false));
                }
            }
        } catch (Exception e) {
            log.error("存储笔记异常:", new Throwable(e));
            result.put(false, "出现异常");
        }
        return result;
    }

    /**
     * 文本审核->笔记内容
     *
     * @param note
     * @return
     */
    private Map<Boolean, String> check(Note note) {
        Map<Boolean, String> result = new HashMap<>();
        JSONObject jsonObject = baiDuUtils.checkTextContent(note.getNoteContent());
        if (jsonObject.getString("conclusion").equals(Constant.CONCLUSION_2)) {
            JSONArray dataJSONArray = jsonObject.getJSONArray("data");
            String msg = dataJSONArray.getJSONObject(0).getString("msg");
            result.put(false, msg);
        } else {
            result.put(true, "文本审核通过!");
        }
        return result;
    }

    /**
     * 更新笔记
     *
     * @param noteVo
     * @return
     */
    @Override
    public Map updateNote(Note noteVo) {
        Map<Object, String> result = new HashMap();
        int row = 0;
        if (StringUtils.isNotEmpty(noteVo.getStar())) {
            row = noteMapper.updateNote(noteVo);//设置是否收藏
        } else if (StringUtils.isNotEmpty(noteVo.getIsRecycle())) {
            row = noteMapper.updateNote(noteVo);//设置是否回收
        } else if (StringUtils.isNotBlank(noteVo.getNoteTitle())) {
            row = noteMapper.updateNote(noteVo);
        } else if (StringUtils.isNotBlank(noteVo.getNoteContent()) && noteVo.getNoteContent().trim().length() < 20000) {
            Map<Boolean, String> map = check(noteVo);
            if (StringUtils.isNotEmpty(map.get(true))) {
                try {
                    //进行加密
                    String noteContent = aseUtils.encrypt(noteVo.getNoteContent().trim().getBytes("UTF-8"), noteVo.getAccountId().toString().getBytes("UTF-8"));
                    noteVo.setNoteContent(noteContent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                row = noteMapper.updateNote(noteVo);
            } else {
                result.put(false, map.get(false));
                return result;
            }
        } else {
            if (StringUtils.isBlank(noteVo.getNoteType())) {
                noteVo.setNoteType("未分类");
            }
            row = noteMapper.updateNote(noteVo);
        }
        if (row == 1) {
            result.put(true, "SUCCESS");
        } else {
            result.put(false, "FAILURE");
        }
        return result;
    }

    /**
     * 获取笔记列表
     *
     * @param condition
     * @return
     */
    @Override
    public List<Note> findNoteByCondition(Condition condition) {
        List<Note> noteList = noteMapper.findNoteByCondition(condition);
        noteList.forEach(p -> {
            try {
                //解密
                p.setNoteContent(new String(aseUtils.decrypt(Base64.decodeBase64(p.getNoteContent()), p.getAccountId().toString().getBytes("UTF-8"))));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return noteList;
    }

    @Override
    public List<String> selectNoteType(Note note) {
        List<String> noteTypes = noteMapper.selectNoteType(note);
        if (noteTypes != null) {
            return noteTypes;
        } else {
            return null;
        }
    }

    /**
     * 查询总数
     *
     * @param condition
     * @return
     */
    @Override
    public Integer selectCountByCondition(Condition condition) {

        return noteMapper.selectCountByCondition(condition);
    }

    @Override
    public Boolean deleteNoteByNoteId(String noteId) {
        noteMapper.deleteNote(noteId);
        return null;
    }


}
