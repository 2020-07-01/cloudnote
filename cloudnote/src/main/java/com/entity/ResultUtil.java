package com.entity;

/**
 * @author :qiang
 * @date :2019/12/14 下午7:22
 * @description : 响应结果
 * @other :
 */
public class ResultUtil<T> {

    private String code;

    private String msg;

    private T data;

    public ResultUtil(ResponseStatuCode responseStatuCode) {
        this.code = responseStatuCode.getCode();
        this.msg = responseStatuCode.getMsg();
    }

    public ResultUtil(ResponseStatuCode responseStatuCode, T data) {
        this.code = responseStatuCode.getCode();
        this.msg = responseStatuCode.getMsg();
        this.data = data;
    }

}
