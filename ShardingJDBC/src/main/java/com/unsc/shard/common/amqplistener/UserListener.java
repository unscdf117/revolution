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
        try {

            log.info("receive: {}", message.getBody());
            Map<String, Object> receive = JSON.parseObject(message.getBody(), Map.class);
            if (receive != null && "try".equals(receive.get("status"))) {
                //MQ中为待发送时
                User user = (User) receive.get("user");
                log.info("User : {}", user);
                //落地
                boolean flag = true;
                if (flag) {
                    //落地成功 采用手动应答模式 手动确认应答更为安全稳定
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    //人为制造故障
                    res = new HashMap<>(2);
                    res.put("status", "200");
                    return res;
                }else {
                    //落地失败 告知MQ 改状态为 cancel
                    channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
                    //人为制造故障
                }
            }
        } catch (Exception e) {
            res = new HashMap<>(2);
            res.put("status", "500");
            e.printStackTrace();
            return res;
        }
        return res;
    }

}
