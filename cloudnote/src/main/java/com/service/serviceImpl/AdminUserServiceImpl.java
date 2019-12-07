package com.service.serviceImpl;

import com.entity.User;
import com.mapper.AdminUserMapper;
import com.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author :qiang
 * @date :2019/12/7 下午7:27
 * @description :
 * @other :
 */
@Service
public class AdminUserServiceImpl implements AdminUserService {

    @Autowired
    private AdminUserMapper adminUserMapper;


    @Override
    public int insert(User user) {
        int row = adminUserMapper.insert(user);

        return row;

    }
}
