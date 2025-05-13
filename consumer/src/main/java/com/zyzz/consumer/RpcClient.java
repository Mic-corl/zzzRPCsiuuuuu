package com.zyzz.consumer;

import com.zyzz.RpcConfig;

public class RpcClient {
    private RpcConfig rpcConfig;

    public static class RpcClientBuilder{
        RpcClient rpcClient;
        public RpcClientBuilder()
        {
            rpcClient=new RpcClient();
        }
        public RpcClient build()
        {
            return rpcClient;
        }
        public RpcClientBuilder setRpcConfig(RpcConfig rpcConfig)
        {
            rpcClient.rpcConfig=rpcConfig;
            return this;
        }
    }


}
