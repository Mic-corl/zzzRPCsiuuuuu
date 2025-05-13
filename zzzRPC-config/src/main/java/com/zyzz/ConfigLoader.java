package com.zyzz;

import cn.hutool.setting.yaml.YamlUtil;

public class ConfigLoader {

    /**
     * 加载RPC配置信息
     * 首先尝试从"application.yml"文件中读取配置信息如果读取失败或配置信息为空，
     * 则使用默认的配置信息创建RpcConfig对象
     *
     * @return RpcConfig对象，包含RPC通信所需的配置信息
     */
    public static RpcConfig load(String configPath) {
        // 读取配置文件
        RpcConfig rpcConfig = null;
        try {
            rpcConfig = YamlUtil.loadByPath(configPath, RpcConfig.class);
        } catch (Exception ignored){}
        // 如果配置文件读取失败或为空，则使用默认配置
        if(rpcConfig == null){
            rpcConfig = new RpcConfig.RpcConfigBuilder()
                    .serializer("jdk")
                    .timeout(3000)
                    .protocol("http")
                    .enableRegistry(true)
                    .build();
        }
        return rpcConfig;
    }
}
