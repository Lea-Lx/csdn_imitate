package com.csdn.notice.config;

import com.csdn.notice.listener.SysNoticeListener;
import com.csdn.notice.listener.UserNoticeListener;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean("sysNoticeContainer")
    public SimpleMessageListenerContainer createSys(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setExposeListenerChannel(true);
        container.setMessageListener(new SysNoticeListener());
        return container;
    }

    @Bean("userNoticeContainer")
    public SimpleMessageListenerContainer createUsr(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);

        container.setMessageListener(new UserNoticeListener());

        container.setExposeListenerChannel(true);

        return container;
    }
}
