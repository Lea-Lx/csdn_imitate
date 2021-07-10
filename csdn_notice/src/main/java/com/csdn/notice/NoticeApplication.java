package com.csdn.notice;

import com.csdn.notice.config.ApplicationContextProvider;
import com.csdn.notice.config.NettyConfig;
import com.csdn.notice.netty.NettyServer;
import com.csdn.utils.IdWorker;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@MapperScan("com.csdn.notice.dao")
@EnableEurekaClient
@EnableFeignClients
public class NoticeApplication {

    public static void main(String[] args) {

        SpringApplication.run(NoticeApplication.class, args);

        NettyServer nettyServer = ApplicationContextProvider.getApplicationContext().getBean(NettyServer.class);

        try {
            nettyServer.start(12345);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Bean
    public IdWorker idWorker() {
        return new IdWorker(1, 1);
    }
}
