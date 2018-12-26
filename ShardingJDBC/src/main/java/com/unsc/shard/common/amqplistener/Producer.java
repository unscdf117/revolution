package com.unsc.shard.common.amqplistener;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by DELL on 2018/12/24.
 */
@Component
public class Producer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 给hello队列发送消息
     */
    public void send() {
        for (int i = 0; i < 100; i++) {
            String msg = "hello, 序号: " + i;
            System.out.println("Producer, " + msg);
            rabbitTemplate.convertAndSend("queue-test", msg);
        }
    }
}
