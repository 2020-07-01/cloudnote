package com.service;

import com.entity.Account;
import com.entity.Condition;

import java.util.Map;

/**
 * @author :qiang
 * @date :2019/12/7 下午7:27
 * @description :
 * @other :
 */
public interface AccountService {

    Map insert(Account user);

    Account findUserById(Integer id);

    Map findAccountByCondition(Condition condition);

    String findPasswordByAccountId(Integer accountId);

    boolean updateAccount(Account account);


}
