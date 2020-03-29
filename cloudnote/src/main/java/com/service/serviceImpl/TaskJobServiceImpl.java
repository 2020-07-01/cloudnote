package com.service.serviceImpl;

import com.entity.Condition;
import com.entity.TaskData;
import com.mapper.TaskJobMapper;
import com.service.TaskJobService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.ws.ServiceMode;
import java.util.List;

@Service
public class TaskJobServiceImpl implements TaskJobService {

    @Autowired
    TaskJobMapper taskJobMapper;

    @Override
    public List<TaskData> selectTaskDataByCondition(Condition condition) {
        List<TaskData> list = taskJobMapper.select(condition);
        return list;
    }
}
