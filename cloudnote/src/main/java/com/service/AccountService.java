package com.service;

import com.entity.Account;
import com.entity.Condition;
import org.checkerframework.checker.units.qual.A;

import java.util.Map;

/**
 * @author :qiang
 * @date :2019/12/7 下午7:27
 * @description :
 * @other :
 */
public interface AccountService {

    Map insert(Account user);

    Map findAccountByCondition(Condition condition);

    String findPasswordByAccountId(Integer accountId);

    boolean updateAccount(Account account);
    //设置状态
    boolean updateLoginStatus(Account account);
    //查询accountId生成token
    String findAccountId(Condition condition);


}

