package com.service;

import com.entity.Task;

public interface TaskService {

    //增加任务
    void insert(Task task);
    //删除任务
    void delete(Task task);

}
