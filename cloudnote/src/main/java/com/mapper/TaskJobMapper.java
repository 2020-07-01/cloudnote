package com.mapper;

import com.entity.Condition;
import com.entity.TaskData;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskJobMapper {

    //查询任务
    List<TaskData> select(Condition condition);

}
