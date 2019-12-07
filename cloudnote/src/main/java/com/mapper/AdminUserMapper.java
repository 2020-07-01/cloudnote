package com.mapper;

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

    int insert(User user);

}
