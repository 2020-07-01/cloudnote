package com.mapper;

import com.entity.Condition;
import com.entity.User;
import org.springframework.stereotype.Repository;

/**
 * @author :qiang
 * @date :2019/12/7 下午7:27
 * @description :
 * @other :
 */
@Repository
public interface AdminUserMapper {

    int insertByEmail(User user);

    int insertByPhone(User user);

    User findUser(Condition condition);

    User findUserByUsername(String userName);

    User findUserByEmail(String email);


}
