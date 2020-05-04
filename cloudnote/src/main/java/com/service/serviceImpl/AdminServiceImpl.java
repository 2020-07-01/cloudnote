package com.service.serviceImpl;

import com.entity.Condition;
import com.entity.admin.AdminCount;
import com.entity.admin.AdminData;
import com.mapper.AdminMapper;
import com.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.ws.ServiceMode;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @Description TODO
 * @Author yq
 * @Date 2020/4/13 22:02
 */
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    AdminMapper adminMapper;

    @Override
    public AdminCount findCount(Condition condition) {
        AdminCount adminCount = adminMapper.findCount(condition);
        return adminCount;
    }
}
