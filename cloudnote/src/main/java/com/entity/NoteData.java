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
    private String showTitle;
    private String noteContent;
    private String star;

    public NoteData(Note note) {
        this.accountId = note.getAccountId();
        this.noteId = note.getNoteId();
        this.noteTitle = note.getNoteTitle();
        this.star = note.getStar();
        this.noteType = note.getNoteType();
        this.showTitle = note.getNoteTitle().length() > 18 ? note.getNoteTitle().substring(0, 16) + "..." : note.getNoteTitle();
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

    public String getShowTitle() {
        return showTitle;
    }

    public void setShowTitle(String showTitle) {
        this.showTitle = showTitle;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }
}
