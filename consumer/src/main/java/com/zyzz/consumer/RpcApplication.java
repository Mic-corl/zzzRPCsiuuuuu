package com.zyzz.consumer;

import com.zyzz.ConfigLoader;
import com.zyzz.RpcConfig;


public class RpcApplication {
    public static void main(String[] args) {
        RpcConfig rpcConfig = ConfigLoader.load("application.yml");
        RpcClient rpcClient = new RpcClient.RpcClientBuilder().setRpcConfig(rpcConfig).build();

    }
}
