package com.service;

import com.entity.Condition;
import com.entity.User;

import java.util.Map;

/**
 * @author :qiang
 * @date :2019/12/7 下午7:27
 * @description :
 * @other :
 */
public interface AdminUserService {

    Map insert(User user);

    User findUserById(Integer id);

    Map findUser(Condition condition);


}
