package com.unsc.shard.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Redis设置 采用Redission
 * @author DELL
 * @date 2018/12/28
 */
@Component
public class RedisConfig {

    @Value("${redis.host}")
    private String host;

    @Value("${redis.port}")
    private String port;

    public Config getConfig() {
        Config config = new Config();
        //这里得设置成NIO 如果是EPOLL 则只能运行于Linux上
        config.setTransportMode(TransportMode.NIO);
        //单机运行于Docker上
        config.useSingleServer().setAddress("redis://" + host + ":" + port);
        return config;
    }

    @Bean
    public RedissonClient getRedisClient() {
        return Redisson.create(getConfig());
    }
}
