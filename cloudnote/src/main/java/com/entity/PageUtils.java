package com.entity;

import java.io.Serializable;
import java.util.List;

/**
 * @author :qiang
 * @date :2019/12/14 上午11:31
 * @description : 数据工具类
 * @other :
 */
public class PageUtils implements Serializable {
    //总记录数
    private int totalCount;

    //每页记录数
    private int pageSize;

    //总页数
    private int totalPage;

    //当前页数
    private int currPage;

    //列表数据
    List<?> list;

    public PageUtils(int totalCount, int pageSize, int totalPage, int currPage, List<?> list) {
        this.totalCount = totalCount;
        this.pageSize = pageSize;
        this.totalPage = totalPage;
        this.currPage = currPage;
        this.list = list;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getCurrPage() {
        return currPage;
    }

    public void setCurrPage(int currPage) {
        this.currPage = currPage;
    }

    public List<?> getList() {
        return list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }
}
