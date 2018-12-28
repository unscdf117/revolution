//package com.unsc.shard.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.curator.framework.CuratorFramework;
//import org.apache.curator.framework.CuratorFrameworkFactory;
//import org.apache.curator.retry.RetryNTimes;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * ZK 配置 虽然写着是ZK 但是其实是Curator客户端的设置啦 : )
// * @author DELL
// * @date 2018/12/19
// */
//@Configuration
//@Slf4j
//public class ZooKeeperConfig {
//
//    /**
//     * ZK主机IP
//     */
//    @Value("${zookeeper.host}")
//    private String host;
//    /**
//     * ZK主机port
//     */
//    @Value("${zookeeper.port}")
//    private String port;
//
//    @Bean(initMethod = "start")
//    public CuratorFramework curatorFramework() {
//        return CuratorFrameworkFactory.newClient(host + ":" + port, 5000, 5000, new RetryNTimes(3, 5000));
//    }
//}
