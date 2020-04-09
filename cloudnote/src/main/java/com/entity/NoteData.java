package com.entity;

/**
 * @Description 返回实体
 * @Author yq
 * @Date 2020/4/9 19:45
 */
public class NoteData {

    private Integer noteId;
    private Integer accountId;
    private String noteType;
    private String noteTitle;
    private String noteContent;
    private String createTime;
    private String updateTime;
    private String isRecycle;
    private String star;

    public NoteData(Note note){
        this.accountId = note.getAccountId();
        this.noteId  = note.getNoteId();
        this.noteTitle = note.getNoteTitle();
        this.star = note.getStar();
        this.noteType = note.getNoteType();
    }
    public Integer getNoteId() {
        return noteId;
    }

    public void setNoteId(Integer noteId) {
        this.noteId = noteId;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getNoteType() {
        return noteType;
    }

    public void setNoteType(String noteType) {
        this.noteType = noteType;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getIsRecycle() {
        return isRecycle;
    }

    public void setIsRecycle(String isRecycle) {
        this.isRecycle = isRecycle;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }
}
