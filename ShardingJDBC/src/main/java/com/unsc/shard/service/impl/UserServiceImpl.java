package com.unsc.shard.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.unsc.shard.bean.User;
import com.unsc.shard.mapper.UserMapper;
import com.unsc.shard.service.UserService;
import com.unsc.shard.shadow.facade.interfaces.UserBizFacade;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.Null;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;


@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RabbitTemplate rabbit;

    @Resource
    private RestTemplate rest;

    @Resource
    private TransactionTemplate tx;

    @Override
    public Long save(User user) {
        user.setCreateTime(new Date());
        return userMapper.save(user);
    }

    @Override
    public User get(Long id) {
        // TODO Auto-generated method stub
        return userMapper.get(id);
    }

    @Override
    public User findUser(User user) {
        return userMapper.findUser(user);
    }

    @Override
    public String multiTxTest() {
        //1. Prepare 消息
        User user = new User(281786684163313640L, "测试回滚", "10000", "10000@live.cn", "gaojing", 310000, 1);
        String json = JSONObject.toJSONString(user);
        Map<String, Object> send = new HashMap<>();
        send.put("user", json);
        send.put("status", "try");
        Map<String, Object> res = (Map<String, Object>) rabbit.convertSendAndReceive("queue-test", send);
        log.info("res : {}", res);
        /*String result = tx.execute(transactionStatus -> {
            String flag = "Failure";
            try {
                if (res != null && res.get("status") != null && res.get("status").equals("200")) {
                    //消息prepare成功
                    Long save = userMapper.save(user);
//                    int i = 1 / 0;
//                    System.out.println(i);
                    if (save != null && save > 0) {
                        //save操作成功 发消息告知对方落地
                        flag = "success";
                        return flag;
                    } else {
                        //通知消费端口回滚
                        throw new RuntimeException("生产者消息落地失败");
                    }
                } else {
                    throw new RuntimeException("Rabbit挂了");
                }
            } catch (Exception e) {
                e.printStackTrace();
                //出错了 因为上了Tx注解 此处落地回滚
                flag = "Failure";
            } finally {
                log.info("最终结果: {}");
                if (flag.equals("Failure")) {
                    transactionStatus.setRollbackOnly();
                }
            }
            return flag;
        });*/
        return "自己看日志";
    }
}
