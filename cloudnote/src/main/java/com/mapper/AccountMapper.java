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

    String findPasswordByAcoountId(Integer accountId);

    int updateAccount(Account account);

}
