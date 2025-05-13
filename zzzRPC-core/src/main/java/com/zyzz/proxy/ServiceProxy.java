package com.zyzz.proxy;


import cn.hutool.core.lang.UUID;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.zyzz.RpcRequest;
import com.zyzz.RpcResponse;
import com.zyzz.serializer.JDKSerializer;
import com.zyzz.serializer.Serializer;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 服务代理（JDK 动态代理）
 */
public class ServiceProxy implements InvocationHandler {


    /**
     * 调用代理
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        // 排除 Object 类中的基础方法
        if (method.getDeclaringClass() == Object.class) {
            if ("toString".equals(method.getName())) {
                return "Dynamic Proxy for " + method.getDeclaringClass();
            } else if ("hashCode".equals(method.getName())) {
                return System.identityHashCode(proxy);
            } else if ("equals".equals(method.getName())) {
                return proxy == args[0];
            }
        }
        Serializer serializer = new JDKSerializer();

        RpcRequest rpcRequest =RpcRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .interfaceName(method.getDeclaringClass().getName())
                .parameterTypes(method.getParameterTypes())
                .methodName(method.getName())
                .parameters(args)
                .build();

        try {
            byte[] serializedRequest = serializer.serialize(rpcRequest);

            HttpRequest httpRequest = HttpRequest.post("http://localhost:8080")
                    .body(serializedRequest)
                    .contentType("application/json");

            byte[] responseBytes = null;
            try (HttpResponse httpResponse = httpRequest.execute()) {

                responseBytes = httpResponse.bodyBytes();
            }
            RpcResponse rpcResponse = serializer.deserialize(responseBytes, RpcResponse.class);

            return rpcResponse.getResult();
        } catch (IOException | HttpException e) {
            e.printStackTrace();
        }
        return null;
    }
}

