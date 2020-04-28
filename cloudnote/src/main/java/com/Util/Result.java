package com.Util;

/**
 * @author :qiang
 * @date :2019/12/14 下午7:22
 * @description : 响应结果封装体
 * @other :
 */
public final class Result {

    public static final String STATUS_SUCCESS = "1";
    public static final String STATUS_FAILED = "0";

    private String code;//状态码
    private String message;//消息
    private Object data;//数据对象

    public Result() {
    }

    public Result(Boolean status, String message) {
        this.code = (status) ? STATUS_SUCCESS : STATUS_FAILED;
        this.message = message;
    }

    public Result(boolean status, String message, Object data) {
        this.code = (status) ? STATUS_SUCCESS : STATUS_FAILED;
        this.message = message;
        this.data = data;
    }

    public Result(String code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Result(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
