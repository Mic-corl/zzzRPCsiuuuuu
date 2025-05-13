package com.zyzz;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 表示一个远程调用的响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    // 对应的请求ID
    private String requestId;

    // 正常返回结果
    private Object result;

    // 返回值类型(预留
    private Class<?> dataType;

    // 异常信息（如果调用失败）
    private Exception exception;

    // 是否成功
    private String  Message;

    public static RpcResponse success(String requestId, Object result) {
        RpcResponse response = new RpcResponse();
        response.setRequestId(requestId);
        response.setResult(result);
        response.setMessage("ok");
        return response;
    }

    public static RpcResponse fail(String requestId, Exception exception) {
        RpcResponse response = new RpcResponse();
        response.setRequestId(requestId);
        response.setException(exception);
        response.setMessage(exception.getMessage());
        return response;
    }
}

