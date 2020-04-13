package com.service;

import com.entity.Condition;
import com.entity.admin.AdminData;


import java.util.List;

public interface AdminService {

    //获取账户列表
    List<AdminData> getAccountList(Condition collection);

    Integer getAccountCount(Condition condition);
}
