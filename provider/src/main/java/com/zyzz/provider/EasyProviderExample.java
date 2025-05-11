package com.zyzz.provider;

import com.zyzz.registry.LocalRegistry;
import com.zyzz.server.HttpServer;
import com.zyzz.server.VertxHttpServer;

public class EasyProviderExample {
    /**
 * 程序的入口点
 * 初始化HTTP服务器并启动，注册本地服务
 * @param args 命令行参数，本程序未使用
 */
public static void main(String[] args) {
    // 在本地注册中心注册userService服务
    LocalRegistry.register("com.zyzz.service.UserService", UserServiceImpl.class);
    // 创建HTTP服务器实例
    HttpServer httpServer = new VertxHttpServer();
    // 启动HTTP服务器，监听8080端口
    httpServer.doStart(8080);

}

}
