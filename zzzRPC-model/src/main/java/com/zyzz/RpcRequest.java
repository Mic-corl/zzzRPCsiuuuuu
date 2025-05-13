package com.zyzz;


import cn.hutool.core.lang.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
/**
 * RpcRequest类用于表示RPC（远程过程调用）请求的信息
 * 它实现了Serializable接口，以确保请求对象可以被序列化和反序列化，
 * 从而能够通过网络进行传输
 */
@Data
@Builder
@AllArgsConstructor
public class RpcRequest implements Serializable {

    // 序列化ID，用于版本控制和兼容性检查
    private static final long serialVersionUID = 1L;

    // 请求ID，唯一标识一个RPC请求，用于跟踪和管理请求
    private String requestId;

    // 接口名称，指定需要调用的服务接口的全限定名
    private String interfaceName;

    // 方法名称，指定在服务接口中需要调用的具体方法名
    private String methodName;

    // 参数类型数组，表示目标方法的参数类型列表
    private Class<?>[] parameterTypes;

    // 参数数组，包含目标方法的实际参数值
    private Object[] parameters;

    /**
     * 默认构造函数，初始化RpcRequest对象时生成一个唯一的请求ID
     */
    public RpcRequest() {
        this.requestId = UUID.randomUUID().toString();
    }
}
