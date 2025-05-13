package com.zyzz.provider;

import com.zyzz.User;
import com.zyzz.service.UserService;

public class UserServiceImpl implements UserService {
    /**
     * 获取用户信息实现
     * @param user
     * @return
     */
    @Override
    public User getUser(User user) {
        System.out.println("用户名:"+user.getName());
        return user;
    }
}
