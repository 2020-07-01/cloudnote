package com.service.serviceImpl;

import com.entity.Condition;
import com.entity.Task;
import com.mapper.TaskMapper;
import com.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: TaskServiceImpl
 * @description:
 * @create: 2020-01-31 17:23
 **/
@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    TaskMapper taskMapper;

    @Override
    public void insert(Task task) {
        taskMapper.insert(task);
    }

    @Override
    public void delete(Task task) {

    }

    @Override
    public List<Task> select(Condition condition) {
        List<Task> tasks = taskMapper.select(condition);
        return tasks;
    }
}
