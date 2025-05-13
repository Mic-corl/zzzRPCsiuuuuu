package com.zyzz.proxy;

import java.lang.reflect.Proxy;

public class ServiceProxyFactory {

    /**
     * 创建一个指定接口的代理对象
     * 这是给消费者提供代理对象的工厂
     * @param serviceClass 接口的 Class 对象，例如 UserService.class
     * @param <T>          泛型，表示你要创建的对象类型
     * @return 返回一个动态代理对象，类型为 T
     */
    public static <T> T getProxy(Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy()
        );
    }

}


