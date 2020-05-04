package com.service;

import com.entity.Condition;
import com.entity.admin.AdminCount;
import com.entity.admin.AdminData;
import org.springframework.context.annotation.ConditionContext;


import java.util.List;

public interface AdminService {

    //查询总数
    AdminCount findCount(Condition condition);


}
