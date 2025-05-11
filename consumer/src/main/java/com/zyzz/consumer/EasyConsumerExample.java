package com.zyzz.consumer;

import com.zyzz.model.User;
import com.zyzz.proxy.ServiceProxyFactory;
import com.zyzz.service.UserService;

public class EasyConsumerExample {
    public static void main(String[] args) {
        User user = new User();
        user.setName("张三");
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);

        User userNew = userService.getUser(user);
        if(userNew!=null) System.out.println(userNew.getName());
        else System.out.println("user==null");
        // 动态代理


    }
}
