package com.mapper;

import com.entity.Condition;
import com.entity.admin.AdminCount;
import com.entity.admin.AdminData;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminMapper {

    //获取总数
    AdminCount findCount(Condition condition);

}
