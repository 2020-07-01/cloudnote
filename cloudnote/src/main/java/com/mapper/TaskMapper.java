package com.mapper;

import com.entity.Condition;
import com.entity.Task;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Repository
public interface TaskMapper {

    //增加任务
    void insert(Task task);

    //删除任务
    void deleteTaskByTaskId(Integer taskId);

    //查询任务
    List<Task> select(Condition condition);

}