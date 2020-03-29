package com.service;

import com.entity.Condition;
import com.entity.TaskData;

import java.util.List;

public interface TaskJobService {
    List<TaskData> selectTaskDataByCondition(Condition condition);
}
