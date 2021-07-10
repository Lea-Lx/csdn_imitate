package com.csdn.notice.netty;

import com.csdn.entity.Result;
import com.csdn.entity.StatusCode;
import com.csdn.notice.config.ApplicationContextProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class MyWebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static ObjectMapper MAPPER = new ObjectMapper();

    //从spring容器中获取消息监听器容器，处理订阅消息sysNotice
    SimpleMessageListenerContainer sysNoticeContainer = (SimpleMessageListenerContainer) ApplicationContextProvider
            .getApplicationContext().getBean(("sysNoticeContainer"));

    //从spring容器中获取消息监听器容器，处理点赞消息usrNotice
    SimpleMessageListenerContainer usrNoticeContainer = (SimpleMessageListenerContainer) ApplicationContextProvider
            .getApplicationContext().getBean("userNoticeContainer");

    //从spring容器中获取RabbitTemplate
    RabbitTemplate rabbitTemplate = ApplicationContextProvider.getApplicationContext().getBean(RabbitTemplate.class);

    //存放WebSocket连接Map,根据用户id存放
    public static ConcurrentHashMap<String, Channel> userChannelMap = new ConcurrentHashMap<>();

    //用户请求WebSocket服务端，制行的方法
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        //约定用户第一次请求携带的数据：{"userId":"1"}
        //获取用户请求数据并解析
        String json = msg.text();
        //解析json，获取用户id
        String userId = MAPPER.readTree(json).get("userId").asText();

        //第一次请求的时候，需要建立WebSocket连接
        Channel channel = userChannelMap.get(userId);
        if(channel == null) {
            //获取webSocket的连接
            channel = ctx.channel();

            //把连接放到容器中
            userChannelMap.put(userId, channel);
        }
        //只用完成新消息的提醒即可，只需要获取消息的数量
        //获取RabbitMQ的消息内容，并发送给用户
        RabbitAdmin rabbitAdmin = new RabbitAdmin(rabbitTemplate);

        //-----------------------------------------------------------------------
        //处理订阅消息
        String queueName = "article_subscribe_" + userId;
        Properties sysProperties = rabbitAdmin.getQueueProperties(queueName);

        //获取消息数量
        int noticeCount = 0;
        //判断Properties是否不为空
        if(sysProperties != null) {
            noticeCount = (int)sysProperties.get("QUEUE_MESSAGE_COUNT");
        }

        //-----------------------------------------------------------------------
        //处理点赞消息
        String userQueueName = "article_thumbup_" + userId;
        Properties userProperties = rabbitAdmin.getQueueProperties(userQueueName);

        int userNoticeCount = 0;

        if(userProperties != null) {
            userNoticeCount = (int)userProperties.get("QUEUE_MESSAGE_COUNT");
        }
        //-----------------------------------------------------------------------
        //封装返回的数据
        HashMap countMap = new HashMap();
        countMap.put("sysNoticeCount", noticeCount);
        countMap.put("userNoticeCount", userNoticeCount);
        Result result = new Result(true, StatusCode.OK, "查询成功", countMap);

        //发送数据
        channel.writeAndFlush(new TextWebSocketFrame(MAPPER.writeValueAsString(result)));

        //把消息从队列中清空，否则MQ消息监听器会再次消费一次
        if(noticeCount > 0) {
            rabbitAdmin.purgeQueue(queueName, true);
        }

        if(userNoticeCount > 0) {
            rabbitAdmin.purgeQueue(userQueueName, true);
        }
        //为用户的消息通知队列注册监听器，便于用户在线的时候，
        //一旦有消息，可以主动推送给用户，不需要用户请求服务器获取数据
        sysNoticeContainer.addQueueNames(queueName);

        usrNoticeContainer.addQueueNames(userQueueName);
    }
}
