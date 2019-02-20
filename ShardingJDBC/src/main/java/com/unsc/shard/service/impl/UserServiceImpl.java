package com.unsc.shard.service.impl;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;

import com.alibaba.fastjson.JSONObject;
import com.unsc.shard.bean.User;
import com.unsc.shard.mapper.UserMapper;
import com.unsc.shard.service.UserService;
import com.unsc.shard.shadow.dto.UserDto;
import com.unsc.shard.shadow.facade.interfaces.UserBizFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
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

    @Resource
    private UserBizFacade userBizFacade;

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

    @Override
    public String mockOrderOperateWithUser(User user, List<User> list) {
        //业务逻辑
        UserDto dto = new UserDto();
        BeanUtils.copyProperties(user, dto);

        ThreadPoolExecutor tpe = new ThreadPoolExecutor(20, 1000, 20, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2, true));

        try {
            tpe.submit(() -> {
                boolean f1 = userBizFacade.checkAvailableUser(dto);
                boolean f2 = userBizFacade.checkUserInfoFromNC(dto);
                System.out.println("Sku信息检查 库存信息检查");
                return f1 && f2;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return "False";
        }

        try {
            var newList = new ArrayList<User>();

            //这里模拟拆单 转换订单 计算金额等操作....哎 作孽
            list.forEach(u -> {
                //phone 替代 金额
                String amount = u.getPhone();
                //cityID是int 代替数量
                Integer count = u.getCityId();
                for (int i = 0; i < 5; i++) {
                    //哎 实际情况 狗比 模拟NC的BigDecimal 草泥马
                    BigDecimal decimalAmount = new BigDecimal(amount);
                    BigDecimal decimalCount = new BigDecimal(count);
                    BigDecimal total = decimalAmount.multiply(decimalCount);
                    u.setEmail(total.toString());
                }
                newList.add(u);
            });
            //接下去模拟下单..传输给OMS/NC + 本地落地
            tpe.submit(() -> {
                try {
                    System.out.println("下发下游");
                    Thread.sleep(35);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            return "True";
        } catch (Exception e) {
            e.printStackTrace();
            return "False";
        }
    }
}
