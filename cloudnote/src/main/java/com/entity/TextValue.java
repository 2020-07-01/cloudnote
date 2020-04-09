package com.entity;

/**
 * 笔记标签返回实体封装
 */
public class TextValue {

    private String key;
    private String value;

    public TextValue(){

    }

    public TextValue(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
