package com.zyzz;

import lombok.Data;

@Data
/**
 * Rpc配置类，用于配置Rpc客户端或服务器的相关属性
 */
public class RpcConfig {
    // 注册中心地址，用于发现服务提供者
    private String registryAddress;

    // 通信协议，默认为http，可根据需要修改为其他协议如https
    private String protocol ;

    // 序列化方式，默认使用jdk序列化，可选其他序列化方式如json、protobuf
    private String serializer;

    // 超时时间，默认为3000毫秒，即3秒，根据网络情况和业务需求可调整
    private int timeout ;

    // 是否启用注册中心，默认启用，设为false时将不使用注册中心进行服务发现
    private boolean enableRegistry = true;

    /**
     * 使用链式编程内部类，用于设置Rpc配置属性
     */
    public static class RpcConfigBuilder{
        private RpcConfig rpcConfig;

        public RpcConfigBuilder(){
            rpcConfig = new RpcConfig();
        }
        public RpcConfigBuilder registryAddress(String registryAddress){
            rpcConfig.setRegistryAddress(registryAddress);
            return this;
        }
        public RpcConfigBuilder protocol(String protocol){
            rpcConfig.setProtocol(protocol);
            return this;
        }
        public RpcConfigBuilder serializer(String serializer){
            rpcConfig.setSerializer(serializer);
            return this;
        }
        public RpcConfigBuilder timeout(int timeout){
            rpcConfig.setTimeout(timeout);
            return this;
        }
        public RpcConfigBuilder enableRegistry(boolean enableRegistry){
            rpcConfig.setEnableRegistry(enableRegistry);
            return this;
        }
        public RpcConfig build(){
            return rpcConfig;
        }
    }
}

