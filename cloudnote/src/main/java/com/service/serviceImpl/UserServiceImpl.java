package com.service.serviceImpl;

import com.entity.Condition;
import com.entity.User;
import com.mapper.AdminUserMapper;
import com.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author :qiang
 * @date :2019/12/7 下午7:27
 * @description :
 * @other :
 */
@Service
public class UserServiceImpl implements AdminUserService {

    @Autowired
    private AdminUserMapper adminUserMapper;

    /**
     * 邮箱注册
     * @param user
     * @return
     */
    public Map insert(User user) {
        Map<String,String> result = new HashMap();
        try {
            if (adminUserMapper.findUserByUsername(user.getUserName()) == null){
                adminUserMapper.insertByEmail(user);
                result.put("true","注册成功!");
                return result;
            }else{
                result.put("false","用户已经存在!");
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("false","出现异常!");
        }
        return result;
    }


    public User findUserById(Integer id) {
        return null;
    }

    @Override
    public Map findUser(Condition condition) {
        Map<String,String> result = new HashMap();

        //进行进行分类
        if(condition.getName().length() == 11)
        {
            condition.setPassword(condition.getName());
        }else {
            condition.setUserName(condition.getName());
        }
        User user = adminUserMapper.findUser(condition);
        if(user != null){
            result.put("userId",String.valueOf(user.getUserId()));
            result.put("true","登录成功!");
        }else {
            result.put("false","登录失败!");
        }
        return result;
    }


    /**
     * 验证邮箱是否已经注册
     * @param email
     * @return
     */
    public Map findUerByEmail(String email) {
        Map<String,String> result = new HashMap();
        try {
            User user = adminUserMapper.findUserByEmail(email);
            if (user == null) {
                result.put("true", "");
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
