package com.service.serviceImpl;

import com.entity.Account;
import com.entity.Condition;
import com.mapper.AccountMapper;
import com.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.checkerframework.checker.units.qual.C;
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
                result.put(false, "此邮箱已注册!");
            } else if (CollectionUtils.isNotEmpty(accountMapper.findAccountByCondition(conditionAccountName))) {
                result.put(false, "用户名已存在!");
            } else {
                accountMapper.insertAccount(account);
                result.put(true, "SUCCESS!");
            }
        } catch (Exception e) {
            log.error("注册异常:", new Throwable(e));
            result.put(false, "出现异常!");
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
     * 当登录成功时更新is_online login_count  last_login_time三个字段信息
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

    //获取用户id
    @Override
    public String findAccountId(Condition condition) {
        String accountId = accountMapper.findAccountId(condition);
        return accountId;
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
     * 验证邮箱是否已经注册
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
                result.put("false", "此邮箱已注册!");
                return result;
            } else {
                result.put("true", "此邮箱不存在");
                return result;
            }
        } catch (Exception e) {
            result.put("false", "网络出现异常!");
            return result;
        }
    }

}
