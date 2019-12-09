package com.service;

import com.entity.User;

/**
 * @author :qiang
 * @date :2019/12/7 下午7:27
 * @description :
 * @other :
 */
public interface AdminUserService {

    public int insert(User user);

    User findUserById(Integer id);

}
