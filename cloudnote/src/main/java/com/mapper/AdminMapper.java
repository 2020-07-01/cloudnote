package com.mapper;

import com.entity.Condition;
import com.entity.admin.AdminData;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminMapper {

    //获取账户列表
    List<AdminData> getAccountList(Condition collection);

    //获取账户总数
    Integer getAccountCount(Condition condition);

}
