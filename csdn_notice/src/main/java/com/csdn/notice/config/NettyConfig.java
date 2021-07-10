package com.csdn.notice.config;

import com.csdn.notice.netty.NettyServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NettyConfig {

    @Bean
    public NettyServer createNettyServer() {
        NettyServer nettyServer = new NettyServer();

        //启动Netty服务，使用新的线程启动
        new Thread() {
            @Override
            public void run() {
                nettyServer.start(123);
            }
        }.start();

        return nettyServer;
    }
}
