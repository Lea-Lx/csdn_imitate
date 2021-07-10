package com.csdn.notice.listener;

import com.csdn.entity.Result;
import com.csdn.entity.StatusCode;
import com.csdn.notice.netty.MyWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;

import java.util.HashMap;

public class UserNoticeListener implements ChannelAwareMessageListener {
    private static ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public void onMessage(Message msg, Channel channel) throws Exception {
        //获取用户id，可以通过队列名称获取
        String queueName = msg.getMessageProperties().getConsumerQueue();
        String userId = queueName.substring(queueName.lastIndexOf("_")+1);
        io.netty.channel.Channel wsChannel = MyWebSocketHandler.userChannelMap.get(userId);

        if(wsChannel != null) {
            HashMap countMap = new HashMap();
            countMap.put("userNoticeCount", 1);
            Result result = new Result(true, StatusCode.OK, "查询成功", countMap);

            wsChannel.writeAndFlush(new TextWebSocketFrame(MAPPER.writeValueAsString(result)));
        }
    }
}
