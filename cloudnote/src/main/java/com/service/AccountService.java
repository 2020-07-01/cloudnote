package com.service;

import com.entity.Account;
import com.entity.Condition;
import org.checkerframework.checker.units.qual.A;

import java.util.List;
import java.util.Map;

/**
 * @author :qiang
 * @date :2019/12/7 下午7:27
 * @description :
 * @other :
 */
public interface AccountService {


    Map insert(Account user);

    //获取旧密码
    String findPasswordByAccountId(String accountId);

    //更新账户信息
    boolean updateAccount(Account account);

    //设置状态
    boolean updateLoginStatus(Account account);

    //查询账号信息
    Account getOneAccount(Condition condition);

    //获取账号信息
    List<Account> getAccountByCondition(Condition condition);

    //获取活跃用户
    Integer findAliveAccountByCondintion(Condition condition);
}

