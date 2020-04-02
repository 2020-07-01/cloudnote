package com.service.serviceImpl;

import com.entity.Account;
import com.entity.Condition;
import com.entity.PIM;
import com.mapper.AccountMapper;
import com.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author :qiang
 * @date :2019/12/7 下午7:27
 * @description :
 * @other :
 */
@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private PIMServiceImpl pimService;

    /**
     * 邮箱注册
     *
     * @param account
     * @return
     */
    public Map insert(Account account) {
        Map<String, String> result = new HashMap();
        try {
            Condition condition = new Condition();
            condition.setEmail(account.getEmail());
            Condition condition1 = new Condition();
            condition1.setAccountName(account.getAccountName());
            if (accountMapper.findAccountByCondition(condition) != null) {
                result.put("false", "此邮箱已注册!");
            } else if (accountMapper.findAccountByCondition(condition1) != null) {
                result.put("false", "用户已经存在!");
            } else {
                accountMapper.insertAccount(account);
                //存储pim

                result.put("true", "注册成功!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("false", "出现异常!");
        }
        return result;
    }


    @Override
    public Map findAccountByCondition(Condition condition) {
        Map<String, String> result = new HashMap();
        //手机号登录
        if (condition.getName().length() == 11) {
            condition.setPhone(condition.getName());
        }
        //名称登录
        else {
            condition.setAccountName(condition.getName());
        }
        Account account = accountMapper.findAccountByCondition(condition);
        if (account != null) {
            result.put("userId", String.valueOf(account.getAccountId()));
            result.put("true", "登录成功!");
            result.put("accountId", String.valueOf(account.getAccountId()));
        } else {
            result.put("false", "登录失败!");
        }
        return result;
    }

    @Override
    public String findPasswordByAccountId(Integer accountId) {
        String passWord = accountMapper.findPasswordByAccountId(accountId);
        return passWord;
    }

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

    @Override
    public String findAccountId(Condition condition) {
        Integer accountId = accountMapper.findAccountId(condition);
        return accountId.toString();
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
            Account user = accountMapper.findAccountByCondition(condition);
            if (user == null) {
                result.put("true", "此邮箱不存在");
                return result;
            } else {
                result.put("false", "此邮箱已注册!");
                return result;
            }
        } catch (Exception e) {
            result.put("false", "网络出现异常!");
            return result;
        }
    }

}
