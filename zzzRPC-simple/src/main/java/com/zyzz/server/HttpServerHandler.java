package com.zyzz.server;

import cn.hutool.core.lang.UUID;
import com.zyzz.model.RpcRequest;
import com.zyzz.model.RpcResponse;
import com.zyzz.registry.LocalRegistry;
import com.zyzz.serializer.JDKSerializer;
import com.zyzz.serializer.Serializer;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 请求处理器
 */
public class HttpServerHandler implements Handler<HttpServerRequest> {

    //todo serializer 固定
    private final Serializer serializer = new JDKSerializer();


    @Override
    public void handle(HttpServerRequest request) {

        /**
         * 1.从请求体中读取请求数据,并反序列化成 RpcRequest 对象。
         */
        request.body(result -> {
            if (result.succeeded()) {
                Buffer buffer = result.result();
                byte[] requestBytes = buffer.getBytes();

                RpcRequest rpcRequest = null;
                try {
                    rpcRequest = serializer.deserialize(requestBytes, RpcRequest.class);
                } catch (IOException e) {
                    e.printStackTrace();
                    String requestId = UUID.randomUUID().toString();
                    RpcResponse response = RpcResponse.fail(requestId, new RuntimeException("反序列化失败"));
                    sendResponse(request, response);
                    return;
                }

                if (rpcRequest == null) {
                    String requestId = UUID.randomUUID().toString();
                    RpcResponse response = RpcResponse.fail(requestId, new RuntimeException("请求为空"));
                    sendResponse(request, response);
                    return;
                }

                // 后续处理 rpcRequest 服务调用成功
                processRpcRequest(request, rpcRequest);
            } else {
                System.err.println("请求体读取失败: " + result.cause());
                RpcResponse response = RpcResponse.fail(UUID.randomUUID().toString(), new RuntimeException("请求体读取失败"));
                sendResponse(request, response);
            }
        });
    }


    private void processRpcRequest(HttpServerRequest request, RpcRequest rpcRequest) {
        RpcResponse response;
        String interfaceName = rpcRequest.getInterfaceName();
        String methodName = rpcRequest.getMethodName();
        Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
        Object[] parameters = rpcRequest.getParameters();
        /**
         * 2.根据服务名称从本地注册器中获取到对应的服务实现类。
         */
        Object result = null;

        try {
            Class<?> implClass = LocalRegistry.get(interfaceName);
            if(implClass == null){
                throw new RuntimeException("找不到对应的服务实现类");
            }
            /**
             * 3.通过反射机制调用方法,得到返回结果。
             */
            // todo 反射调用的是无参构造器
            Object implClassObject = implClass.getDeclaredConstructor().newInstance();
            Method method = implClass.getMethod(methodName, parameterTypes);
            result = method.invoke(implClassObject, parameters);
            /**
             * 4.对返回结果进行封装和序列化,并写入到响应中。
             */
            response = RpcResponse.success(rpcRequest.getRequestId(), result);
            sendResponse(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response = RpcResponse.fail(rpcRequest.getRequestId(), e);
            sendResponse(request, response);
        }
    }

    private void sendResponse(HttpServerRequest request, RpcResponse response) {
        HttpServerResponse httpServerResponse = request.response()
                .putHeader("Content-Type", "application/json");
        byte[] serializedResponse = null;
        try {
            serializedResponse = serializer.serialize(response);
            Buffer buffer = Buffer.buffer(serializedResponse);
            httpServerResponse.end(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }
    }
}
