package com.service.serviceImpl;

import com.entity.account.Account;
import com.entity.Condition;
import com.entity.Constant;
import com.mapper.AccountMapper;
import com.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author :qiang
 * @date :2019/12/7 下午7:27
 * @description :
 * @other :
 */
@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountMapper accountMapper;


    /**
     * 邮箱注册
     *
     * @param account
     * @return
     */
    public Map insert(Account account) {
        Map<Boolean, String> result = new HashMap();
        try {
            Condition conditionEmail = new Condition();
            conditionEmail.setEmail(account.getEmail());
            Condition conditionAccountName = new Condition();
            conditionAccountName.setAccountName(account.getAccountName());
            List<Account> accountList = accountMapper.findAccountByCondition(conditionEmail);
            if (CollectionUtils.isNotEmpty(accountList)) {
                result.put(false, Constant.email_message_3);
            } else if (CollectionUtils.isNotEmpty(accountMapper.findAccountByCondition(conditionAccountName))) {
                result.put(false, Constant.register_message_3);
            } else {
                accountMapper.insertAccount(account);
                result.put(true, Constant.register_message_1);
            }
        } catch (Exception e) {
            log.error("注册异常:", new Throwable(e));
            result.put(false, Constant.register_message_2);
        }
        return result;
    }


    @Override
    public String findPasswordByAccountId(String accountId) {
        String passWord = accountMapper.findPasswordByAccountId(accountId);
        return passWord;
    }

    /**
     * 更新账户信息
     *
     * @param account
     * @return
     */
    @Override
    public boolean updateAccount(Account account) {
        try {
            accountMapper.updateAccount(account);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 设置账户状态信息
     * 当登录成功时更新login_count  last_login_time两个字段信息
     *
     * @param account
     * @return
     */
    @Override
    public boolean updateLoginStatus(Account account) {
        try {
            Integer row = accountMapper.updateLoginStatus(account);
            if (row == 1) {
                return true;
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return false;
    }

    /**
     * 获取单个用户
     *
     * @param condition
     * @return
     */
    @Override
    public Account getOneAccount(Condition condition) {

        Account account = accountMapper.findAccountData(condition);
        return account;
    }

    /**
     * 获取账号信息
     *
     * @param condition
     * @return
     */
    @Override
    public List<Account> getAccountByCondition(Condition condition) {
        List<Account> accountList = accountMapper.findAccountByCondition(condition);
        return accountList;
    }

    /**
     * 获取活跃用户
     *
     * @param condition
     * @return
     */
    @Override
    public Integer findAliveAccountByCondintion(Condition condition) {
        Integer aliveCount = accountMapper.findAliveAccountByCondition(condition);
        return aliveCount;
    }


    /**
     * 对邮箱进行验证
     *
     * @param email
     * @return
     */
    public Map findUerByEmail(String email) {
        Map<String, String> result = new HashMap();
        try {
            Condition condition = new Condition();
            condition.setEmail(email);
            List<Account> accountList = accountMapper.findAccountByCondition(condition);
            if (CollectionUtils.isNotEmpty(accountList)) {
                result.put("email_3", Constant.email_message_3);
            } else {
                result.put("email_5", Constant.email_message_5);
            }
        } catch (Exception e) {
            result.put("email_2", Constant.email_message_2);
        }
        return result;
    }


}
