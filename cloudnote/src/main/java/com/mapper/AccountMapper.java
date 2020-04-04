package com.mapper;

import com.entity.Account;
import com.entity.Condition;
import org.springframework.stereotype.Repository;

/**
 * @author :qiang
 * @date :2019/12/7 下午7:27
 * @description :
 * @other :
 */
@Repository
public interface AccountMapper {

    int insertAccount(Account account);

    Account findAccountByCondition(Condition condition);
    //获取旧密码
    String findPasswordByAccountId(Integer accountId);
    //更新账户信息
    int updateAccount(Account account);
    //当登录成功时更新is_online login_count  last_login_time三个字段信息
    int updateLoginStatus(Account account);
    //根据用户名和密码获取用户的id
    Integer findAccountId(Condition condition);




}
