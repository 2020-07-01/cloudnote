package com.service;

import com.entity.Condition;
import com.entity.Task;

import java.util.List;

public interface TaskService {

    //增加任务
    void insert(Task task);
    //删除任务
    void delete(Task task);
    //查询任务
    List<Task> select(Condition condition);

}
