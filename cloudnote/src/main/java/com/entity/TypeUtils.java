package com.entity;

public enum TypeUtils {

    DEFAULT("默认", 0),
    STUDY("学习", 1),
    WORKD("工作", 2),
    LIFE("生活", 3),
    TRAVEL("旅游", 4),
    INFORMALESSAY("随笔", 5);

    private String name;
    private Integer id;

    TypeUtils(String name, Integer id) {
        this.name = name;
        this.id = id;
    }

    public static Integer getSexEnumByCode(String name) {
        for (TypeUtils typeUtils : TypeUtils.values()) {
            if (typeUtils.name.equals(name)) {
                return typeUtils.id;
            }
        }
        return null;
    }

}
