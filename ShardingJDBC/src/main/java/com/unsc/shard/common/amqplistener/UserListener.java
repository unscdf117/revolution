package com.unsc.shard.common.amqplistener;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import com.unsc.shard.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by DELL on 2018/12/24.
 */
@Component
@Slf4j
public class UserListener {

    @Resource
    private RabbitTemplate template;

    @RabbitListener(queues = "queue-test")
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> process(Message message, Channel channel) throws IOException {
        Map<String, Object> res = new HashMap<>();
        res.put("status", "500");
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        log.info("body: {}", message.getBody());
        return res;
    }

}
